package xyz.hyperreal.datetime

import scala.collection.mutable

object Timezone {

  import Datetime._

  val UTC: Timezone = new Timezone {
    def offset(year: Int, month: Int, day: Int, millis: Int): Int = 0

    def offset(millis: Long): Int = 0

    def getID: String = "UTC"

    def getDisplayName: String = "Coordinated Universal Time"
  }

  val ET: Timezone = new Timezone {
    def offset(year: Int, month: Int, day: Int, millis: Int): Int = -5 * HOUR

    def offset(millis: Long): Int = -5 * HOUR

    def getID: String = "ET"

    def getDisplayName: String = "Eastern Time"
  }

  val CT: Timezone = new Timezone {
    def offset(year: Int, month: Int, day: Int, millis: Int): Int = -6 * HOUR

    def offset(millis: Long): Int = -6 * HOUR

    def getID: String = "CT"

    def getDisplayName: String = "Central Time"
  }

  private val tz = mutable.HashMap[String, Timezone]("UTC" -> UTC, "EST" -> ET, "CST" -> CT)

  def forID(id: String): Option[Timezone] = tz get id

}

trait Timezone {

  def offset(year: Int, month: Int, day: Int, millis: Int): Int

  def offset(millis: Long): Int

  def getID: String

  def getDisplayName: String

}
