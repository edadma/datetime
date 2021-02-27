package xyz.hyperreal.datetime

import math._
import scala.collection.immutable.ArraySeq

object Datetime {

  def now(tz: Timezone): Datetime = forInstant(xyz.hyperreal.datetime.Platform.currentTimeMillis, tz)

  def forInstant(millis: Long, tz: Timezone): Datetime = {
    val t = millis + tz.offset(millis)

    Datetime(1, 1, 1, 1, 1, 1, 1)
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

  check("year", year, 1, 10000)

  val isLeapYear: Boolean = year % 4 == 0 && year % 100 > 0 || year % 400 == 0

  check("month", month, 1, 12)
  check("day", day, 1, if (month == 2 && isLeapYear) 29 else months(month - 1))
  check("hours", hours, 0, 23)
  check("minutes", minutes, 0, 59)
  check("seconds", seconds, 0, 59)
  check("nanos", nanos, 0, 999999999)

  private val m = if (month <= 2) month + 10 else month - 2
  private val c = year % 100

  // https://cs.uwaterloo.ca/~alopez-o/math-faq/node73.html
  lazy val dayOfWeek: Int = (day + floor(2.6 * m - 0.2) - 2 * c + year + floor(year / 4.0) + floor(c / 4.0)).toInt % 7

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

  def toISOString: String = DatetimeFormat.ISO.format(this)

  override def toString: String = DatetimeFormat.DISPLAY_DATE.format(this)

}
