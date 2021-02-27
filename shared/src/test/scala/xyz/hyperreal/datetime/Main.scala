package xyz.hyperreal.datetime

import java.time.temporal.ChronoField._
import java.time.{Instant, ZoneId, ZonedDateTime}
import math._

object Main extends App {

  println(Datetime.now(Timezone.EST))

//  javaTimeTest()

  def javaTimeTest(): Unit = {
    val UTC = ZoneId.of("UTC")
    val minms = ZonedDateTime.of(-10000, 1, 1, 0, 0, 0, 0, UTC).toInstant.toEpochMilli
    val maxms = ZonedDateTime.of(10000, 12, 31, 23, 59, 59, 999000000, UTC).toInstant.toEpochMilli

    def rnd(l: Long, u: Long) = util.Random.nextLong(u - l + 1) + l

    def rndms = rnd(minms, maxms)

    for (_ <- 1 to 50000000) {
      val ms = rndms
      val utc = Instant.ofEpochMilli(ms).atZone(UTC)
      val jdays = utc.getLong(EPOCH_DAY)
      val jdow = utc.getDayOfWeek.getValue % 7
      val jleap = utc.toLocalDate.isLeapYear
      val jlom = utc.toLocalDate.lengthOfMonth
      val jloy = utc.toLocalDate.lengthOfYear
      val d1 =
        Datetime(
          utc.getYear,
          utc.getMonthValue,
          utc.getDayOfMonth,
          utc.getHour,
          utc.getMinute,
          utc.getSecond,
          utc.getNano
        )
      val d2 = Datetime.fromDays(floorDiv(ms, 24 * 60 * 60 * 1000).toInt)
      val d3 = Datetime.fromMillis(ms)

      if (d1 != d3) {
        println("fromMillis", d1, d3, ms)
        sys.exit(1)
      }

      if (d1.withoutTime != d2) {
        println("fromDays", d1.withoutTime, d2, ms)
        sys.exit(1)
      }

      if (d1.lengthOfYear != jloy) {
        println("length of year", d1, utc, d1.lengthOfYear, jloy)
        sys.exit(1)
      }

      if (d1.lengthOfMonth != jlom) {
        println("length of month", d1, utc, d1.lengthOfMonth, jlom)
        sys.exit(1)
      }

      if (d1.isLeapYear != jleap) {
        println("leap year", d1, utc, d1.isLeapYear, jleap)
        sys.exit(1)
      }

      if (d1.days != jdays) {
        println("days from civil", d1, utc, d1.days, jdays)
        sys.exit(1)
      }

      if (d1.dayOfWeek != jdow) {
        println("day of week", d1, utc, d1.dayOfWeek, jdow)
        sys.exit(1)
      }
    }
  }

}
