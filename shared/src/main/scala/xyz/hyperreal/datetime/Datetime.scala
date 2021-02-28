package xyz.hyperreal.datetime

import math._

import scala.collection.immutable.ArraySeq

object Datetime {

  def now(tz: Timezone = Timezone.UTC): Datetime = fromMillis(platform.currentTimeMillis, tz)

  def today(tz: Timezone = Timezone.UTC): Datetime = {
    val ms = platform.currentTimeMillis

    fromDays(((ms + tz.offset(ms)) / 24 / 60 / 60 / 1000).toInt)
  }

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

    Datetime(y, m, d, 0, 0, 0)
  }

  def fromMillis(millis: Long, tz: Timezone = Timezone.UTC): Datetime = {
    val t = millis + tz.offset(millis)
    val (y, m, d) = civilFromDays(floorDiv(t, 24 * 60 * 60 * 1000).toInt)
    val day = floorMod(t, 24 * 60 * 60 * 1000).toInt
    val hour = day % (60 * 60 * 1000)
    val minute = hour % (60 * 1000)

    Datetime(y, m, d, day / 60 / 60 / 1000, hour / 60 / 1000, minute / 1000, day % 1000 * 1000000)
  }

  def from(s: String): Datetime = {
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
      } else
        error
    }

    def opt(c: Char) =
      if (idx < s.length && s(idx) == c) {
        idx += 1
        true
      } else false

    val y = digits(4, '-')
    val m = digits(2, '-')
    val d = digits(2, 'T', ' ')
    val h = digits(2, ':')
    val min = digits(2, ':')
    val sec = digits(2)
    val n =
      if (opt('.')) {
        val start = idx
        val x = digits(9)

        (x * math.pow(10, 9 - idx + start)).toInt
      } else 0

    opt('Z')

    if (idx == s.length)
      Datetime(y, m, d, h, min, sec, n)
    else
      error
  }

}

case class Datetime(year: Int, month: Int, day: Int, hours: Int, minutes: Int, seconds: Int, nanos: Int = 0)
    extends Ordered[Datetime] {

  private val months = ArraySeq(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)

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

  def compare(that: Datetime): Int = {
    for (i <- 0 until 9)
      productElement(i).asInstanceOf[Int] - that.productElement(i).asInstanceOf[Int] match {
        case 0    =>
        case diff => return math.signum(diff)
      }

    0
  }

  def withoutTime: Datetime = Datetime(year, month, day, 0, 0, 0)

  lazy val lengthOfYear: Int = if (isLeapYear) 366 else 365

  lazy val days: Int = {
    val y = if (month <= 2) year - 1 else year
    val era = (if (y >= 0) y else y - 399) / 400
    val yoe = y - era * 400
    val doy = (153 * (month + (if (month > 2) -3 else 9)) + 2) / 5 + day - 1
    val doe = yoe * 365 + yoe / 4 - yoe / 100 + doy

    era * 146097 + doe - 719468
  }

  lazy val millis: Long =
    days.toLong * 24 * 60 * 60 * 1000 + hours * 60 * 60 * 1000 + minutes * 60 * 1000 + seconds * 1000 + nanos / 1000000

  lazy val dayOfWeek: Int = {
    val z = days

    if (z >= -4) (z + 4) % 7 else (z + 5) % 7 + 6
  }

  def sameDateAs(that: Datetime): Boolean = year == that.year && month == that.month && day == that.day

  def toISOString: String = DatetimeFormat.ISO.format(this)

  def toDisplayString: String = DatetimeFormat.DISPLAY_DATE.format(this)

}
