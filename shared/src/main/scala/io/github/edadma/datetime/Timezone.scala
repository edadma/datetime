package io.github.edadma.datetime

import scala.collection.mutable

object Timezone {

  val UTC: Timezone = new SimpleTimezone(0, 0, "UTC", "Coordinated Universal Time")
  val ET: Timezone = new SimpleTimezone(-5, 0, "ET", "Eastern Time")
  val CT: Timezone = new SimpleTimezone(-6, 0, "CT", "Central Time")

  private val tz = mutable.HashMap[String, Timezone]("UTC" -> UTC, "ET" -> ET, "CT" -> CT)

  def forID(id: String): Option[Timezone] = tz get id

}

trait Timezone {

  def offset: Int

  def id: String

  def displayName: String

}

class SimpleTimezone(hours: Int, minutes: Int, val id: String = "", val displayName: String = "") extends Timezone {
  val offset: Int = hours * Datetime.HOUR + minutes
}
