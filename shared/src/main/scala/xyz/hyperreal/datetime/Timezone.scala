package xyz.hyperreal.datetime

import scala.collection.mutable

object Timezone {

  import Datetime._

  val UTC: Timezone = new SimpleTimezone(0, "UTC", "Coordinated Universal Time")
  val ET: Timezone = new SimpleTimezone(-5 * HOUR, "ET", "Eastern Time")
  val CT: Timezone = new SimpleTimezone(-6 * HOUR, "CT", "Central Time")

  private val tz = mutable.HashMap[String, Timezone]("UTC" -> UTC, "ET" -> ET, "CT" -> CT)

  def forID(id: String): Option[Timezone] = tz get id

}

trait Timezone {

  def offset(year: Int, month: Int, day: Int, millis: Int): Int

  def offset(millis: Long): Int

  def id: String

  def displayName: String

}

class SimpleTimezone(offset: Int, val id: String, val displayName: String) extends Timezone {
  def offset(year: Int, month: Int, day: Int, millis: Int): Int = offset

  def offset(millis: Long): Int = offset
}
