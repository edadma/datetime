package xyz.hyperreal.datetime

import java.time.temporal.ChronoField
import java.time.temporal.ChronoField._
import java.time.{Instant, ZoneId, ZonedDateTime}
import math._

object Main extends App {

  def rndl(l: Long, u: Long) = util.Random.nextLong(u - l + 1) + l

  def rndi(l: Int, u: Int) = util.Random.nextInt(u - l + 1) + l

  val UTC = ZoneId.of("UTC")
  val minms = ZonedDateTime.of(-10000, 1, 1, 0, 0, 0, 0, UTC).toInstant.toEpochMilli
  val maxms = ZonedDateTime.of(10000, 12, 31, 23, 59, 59, 999000000, UTC).toInstant.toEpochMilli

  def rndInstant = Instant.ofEpochMilli(rndl(minms, maxms))

//  for (_ <- 1 to 50000000) {
//    val utc = rndInstant.atZone(UTC)
//    val jdays = utc.getLong(EPOCH_DAY) //floorDiv(inst.getLong(INSTANT_SECONDS), 60 * 60 * 24)
//    val jdow = utc.getDayOfWeek.getValue % 7
//    val jleap = utc.toLocalDate.isLeapYear
//    val jlom = utc.toLocalDate.lengthOfMonth
//    val jloy = utc.toLocalDate.lengthOfYear
//    val d =
//      Datetime(
//        utc.getYear,
//        utc.getMonthValue,
//        utc.getDayOfMonth,
//        utc.getHour,
//        utc.getMinute,
//        utc.getSecond,
//        utc.getNano
//      )
//
//    if (d.lengthOfYear != jloy) {
//      println("length of year", d.toString, utc, d.lengthOfYear, jloy)
//      sys.exit(1)
//    }
//
//    if (d.lengthOfMonth != jlom) {
//      println("length of month", d.toString, utc, d.lengthOfMonth, jlom)
//      sys.exit(1)
//    }
//
//    if (d.isLeapYear != jleap) {
//      println("leap year", d.toString, utc, d.isLeapYear, jleap)
//      sys.exit(1)
//    }
//
//    if (d.days != jdays) {
//      println("days from civil", d.toString, utc, d.days, jdays)
//      sys.exit(1)
//    }
//
//    if (d.dayOfWeek != jdow) {
//      println("day of week", d.toString, utc, d.dayOfWeek, jdow)
//      sys.exit(1)
//    }
//  }

}
