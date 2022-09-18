package io.github.edadma.datetime

import java.time.temporal.ChronoField._
import java.time.{Instant, ZoneId, ZonedDateTime}
import math._

object Main extends App {

  javaTimeTest()

  def prt(a: Any*): Unit = println(a.mkString(", "))

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
      val localDate = utc.toLocalDate
      val jleap = localDate.isLeapYear
      val jlom = localDate.lengthOfMonth
      val jloy = localDate.lengthOfYear
      val doy = localDate.getDayOfYear
      val d1 =
        Datetime(
          utc.getYear,
          utc.getMonthValue,
          utc.getDayOfMonth,
          utc.getHour,
          utc.getMinute,
          utc.getSecond,
          utc.getNano,
        )
      val d2 = Datetime.fromDays(floorDiv(ms, 24 * 60 * 60 * 1000).toInt)
      val d3 = Datetime.fromMillis(ms)

      if (d1 != d3) {
        prt("fromMillis", d1, d3, ms)
        sys.exit(1)
      }

      if (d1.startOfDay != d2) {
        prt("fromDays", d1.startOfDay, d2, ms)
        sys.exit(1)
      }

      if (d1.dayOfYear != doy) {
        prt("dayOfYear", d1, d1.dayOfYear, doy)
        sys.exit(1)
      }

      if (d1.epochMillis != ms) {
        prt("millis", d1.epochMillis, ms)
        sys.exit(1)
      }

      if (d1.lengthOfYear != jloy) {
        prt("length of year", d1, utc, d1.lengthOfYear, jloy)
        sys.exit(1)
      }

      if (d1.lengthOfMonth != jlom) {
        prt("length of month", d1, utc, d1.lengthOfMonth, jlom)
        sys.exit(1)
      }

      if (d1.isLeapYear != jleap) {
        prt("leap year", d1, utc, d1.isLeapYear, jleap)
        sys.exit(1)
      }

      if (d1.epochDays != jdays) {
        prt("days from civil", d1, utc, d1.epochDays, jdays)
        sys.exit(1)
      }

      if (d1.dayOfWeek != jdow) {
        prt("day of week", d1, utc, d1.dayOfWeek, jdow)
        sys.exit(1)
      }
    }
  }

}
