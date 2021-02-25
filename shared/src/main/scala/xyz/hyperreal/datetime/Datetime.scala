package xyz.hyperreal.datetime

import math._
import scala.collection.immutable.ArraySeq

object Datetime {

  def now(tz: Timezone): Datetime = forInstant(xyz.hyperreal.datetime.Platform.currentTimeMillis, tz)

  def forInstant(millis: Long, tz: Timezone): Datetime = {
    val t = millis + tz.offset(millis)

    Datetime(1, 1, 1, 1, 1, 1, 1, 1)
  }

}

case class Datetime(era: Int,
                    year: Int,
                    month: Int,
                    day: Int,
                    hours: Int,
                    minutes: Int,
                    seconds: Int,
                    millis: Int = 0,
                    nanos: Int = 0)
    extends Ordered[Datetime] {

  private val months = ArraySeq(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)

  check("era", era, 0, 1)
  check("year", year, 1, 5000)

  private val yr = if (era > 0) year else -year + 1

  val leap: Boolean = yr % 4 == 0 && yr % 100 > 0 || yr % 400 == 0

  check("month", month, 1, 12)

  val days: Int = if (month == 2 && leap) 29 else months(month - 1)

  check("day", day, 1, days)
  check("hours", hours, 0, 23)
  check("minutes", minutes, 0, 59)
  check("seconds", seconds, 0, 59)
  check("millis", millis, 0, 999)
  check("nanos", millis, 0, 999999)

  private val m = if (month <= 2) month + 10 else month - 2
  private val c = year % 100

  // https://cs.uwaterloo.ca/~alopez-o/math-faq/node73.html
  val dayOfWeek: Int = (day + floor(2.6 * m - 0.2) - 2 * c + year + floor(year / 4.0) + floor(c / 4.0)).toInt % 7

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

  def toISOString: String = f"$year%04d-$month%02d-$day%02dT$hours%02d:$minutes%02d:$seconds%02d.$millis%03d"

}
