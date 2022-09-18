package io.github.edadma.datetime

import math._
import scala.collection.immutable.ArraySeq

object Datetime {

  private[datetime] val SECOND = 1000
  private[datetime] val MINUTE = 60 * SECOND
  private[datetime] val HOUR = 60 * MINUTE
  private[datetime] val DAY: Long = 24 * HOUR

  private val months = ArraySeq(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
  private val monthlyDays = ArraySeq(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)

  def now(tz: Timezone = Timezone.UTC): Datetime = fromMillis(System.currentTimeMillis, tz)

  def today(tz: Timezone = Timezone.UTC): Datetime = fromDays((currentAdjust(tz) / DAY).toInt)

  private def currentAdjust(tz: Timezone) = adjust(System.currentTimeMillis, tz)

  private def adjust(ms: Long, tz: Timezone) = ms + tz.offset

  private def civilFromDays(days: Int): (Int, Int, Int) = {
    val z = days + 719468
    val era = (if (z >= 0) z else z - 146096) / 146097
    val doe = z - era * 146097
    val yoe = (doe - doe / 1460 + doe / 36524 - doe / 146096) / 365
    val y = yoe + era * 400
    val doy = doe - (365 * yoe + yoe / 4 - yoe / 100)
    val mp = (5 * doy + 2) / 153
    val d = doy - (153 * mp + 2) / 5 + 1
    val m = mp + (if (mp < 10) 3 else -9)

    (y + (if (m <= 2) 1 else 0), m, d)
  }

  def fromDays(days: Int): Datetime = {
    val (y, m, d) = civilFromDays(days)

    Datetime(y, m, d)
  }

  def fromMillis(millis: Long, tz: Timezone = Timezone.UTC): Datetime = {
    val t = adjust(millis, tz)
    val (y, m, d) = civilFromDays(floorDiv(t, DAY).toInt)
    val day = floorMod(t, DAY).toInt
    val hour = day % HOUR
    val minute = hour % MINUTE

    Datetime(y, m, d, day / HOUR, hour / MINUTE, minute / 1000, day % 1000 * 1000000)
  }

  def fromString(s: String): Datetime = {
    var idx = 0

    def error = sys.error(s"error parsing date-time at character ${idx + 1}")

    def digits(n: Int, cs: Char*): Int = {
      var count = 0

      while (count < n && idx + count < s.length && s(idx + count).isDigit) count += 1

      if (count > 0 && (cs.isEmpty || (cs contains s(idx + count)))) {
        val res = (idx until idx + count).map(s).mkString.toInt

        if (cs.nonEmpty)
          idx += 1

        idx += count
        res
      } else error
    }

    def opt(cs: Char*) =
      if (idx < s.length && cs.contains(s(idx))) {
        idx += 1
        true
      } else false

    def space: Boolean =
      if (idx < s.length && s(idx).isWhitespace) {
        idx += 1
        space
        true
      } else false

    val y = digits(4, '-')
    val m = digits(2, '-')
    val d = digits(2)

    if (opt('t', 'T') || space) {
      val h = digits(2, ':')
      val min = digits(2, ':')
      val sec = digits(2)
      val n =
        if (opt('.')) {
          val start = idx
          val x = digits(9)

          (x * math.pow(10, 9 - idx + start)).toInt
        } else 0

      space

      if (opt('Z'))
        if (idx == s.length)
          Datetime(y, m, d, h, min, sec, n)
        else
          error
      else if (opt('+', '-')) {
        val sign = if (s(idx - 1) == '+') 1 else -1
        val hoff = sign * digits(2)
        val moff =
          if (opt(':'))
            sign * digits(2)
          else 0

        if (idx == s.length)
          Datetime(y, m, d, h, min, sec, n).changeTimezone(Timezone.UTC, new SimpleTimezone(hoff, moff))
        else
          error
      } else error

    } else if (idx == s.length)
      Datetime(y, m, d)
    else
      error
  }

}

case class Datetime(year: Int, month: Int, day: Int, hours: Int = 0, minutes: Int = 0, seconds: Int = 0, nanos: Int = 0)
    extends Ordered[Datetime] {

  import Datetime._

  private var iso = false

  def timestamp: Datetime =
    iso = true
    this

  check("year", year, -10000, 10000)

  val isLeapYear: Boolean = floorMod(year, 4) == 0 && floorMod(year, 100) > 0 || floorMod(year, 400) == 0

  check("month", month, 1, 12)

  val lengthOfMonth: Int = if (month == 2 && isLeapYear) 29 else months(month - 1)

  check("day", day, 1, lengthOfMonth)
  check("hours", hours, 0, 23)
  check("minutes", minutes, 0, 59)
  check("seconds", seconds, 0, 59)
  check("nanos", nanos, 0, 999999999)

  private def check(name: String, v: Int, l: Int, h: Int): Unit =
    require(l <= v && v <= h, s"$name is out of range: $v")

  infix def compare(that: Datetime): Int =
    var i = 0

    while i < productArity do
      productElement(i).asInstanceOf[Int] - that.productElement(i).asInstanceOf[Int] match {
        case 0    =>
        case diff => return math.signum(diff)
      }

      i += 1

    0

  def startOfDay: Datetime = Datetime(year, month, day)

  def endOfDay: Datetime = Datetime(year, month, day, 23, 59, 59, 999999999)

  def startOfMonth: Datetime = Datetime(year, month, 1)

  def endOfMonth: Datetime = Datetime(year, month, lengthOfMonth, 23, 59, 59, 999999999)

  def lengthOfYear: Int = if (isLeapYear) 366 else 365

  def dayOfYear: Int = monthlyDays(month - 1) + day + (if (isLeapYear && month > 2) 1 else 0)

  lazy val epochDays: Int =
    val y = if (month <= 2) year - 1 else year
    val era = (if (y >= 0) y else y - 399) / 400
    val yoe = y - era * 400
    val doy = (153 * (month + (if (month > 2) -3 else 9)) + 2) / 5 + day - 1
    val doe = yoe * 365 + yoe / 4 - yoe / 100 + doy

    era * 146097 + doe - 719468

  def epochSeconds: Long = epochDays.toLong * DAY + hours * HOUR + minutes * MINUTE + seconds * 1000

  lazy val epochMillis: Long = epochSeconds + nanos / 1000000

  def epochNanos: Long = epochSeconds + nanos

  def dayOfWeek: Int =
    val z = epochDays

    if (z >= -4) (z + 4) % 7 else (z + 5) % 7 + 6

  def sameDateAs(that: Datetime): Boolean = year == that.year && month == that.month && day == that.day

  def plusDays(days: Int): Datetime = fromMillis(epochMillis + days * DAY)

  def minusDays(days: Int): Datetime = fromMillis(epochMillis - days * DAY)

  def plusHours(hours: Int): Datetime = fromMillis(epochMillis + hours * HOUR)

  def minusHours(hours: Int): Datetime = fromMillis(epochMillis - hours * HOUR)

  def plusMinutes(minutes: Int): Datetime = fromMillis(epochMillis + minutes * MINUTE)

  def minusMinutes(minutes: Int): Datetime = fromMillis(epochMillis - minutes * MINUTE)

  def plusSeconds(seconds: Int): Datetime = fromMillis(epochMillis + seconds * SECOND)

  def minusSeconds(seconds: Int): Datetime = fromMillis(epochMillis - seconds * MINUTE)

  def changeTimezone(from: Timezone, to: Timezone): Datetime = fromMillis(epochMillis - from.offset, to)

  def format(s: String): String = DatetimeFormatter(s).format(this)

  def isFuture(tz: Timezone = Timezone.UTC): Boolean = epochMillis > currentAdjust(tz)

  def isPast(tz: Timezone = Timezone.UTC): Boolean = epochMillis < currentAdjust(tz)

  def toISOString: String = DatetimeFormatter.ISO.format(this)

  def toDisplayString: String = DatetimeFormatter.DISPLAY_DATE.format(this)

  override def toString: String = if (iso) toISOString else productPrefix + productIterator.mkString("(", ", ", ")")

}
