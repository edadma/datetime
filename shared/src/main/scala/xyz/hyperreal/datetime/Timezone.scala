package xyz.hyperreal.datetime

import scala.collection.mutable

object Timezone {

  val UTC: Timezone = new Timezone {
    def offset(year: Int, month: Int, day: Int, millis: Int): Int = 0

    def offset(millis: Long): Int = 0

    def getID: String = "UTC"

    def getDisplayName: String = "Coordinated Universal Time"
  }

  val EST: Timezone = new Timezone {
    def offset(year: Int, month: Int, day: Int, millis: Int): Int = -5 * 60 * 60 * 1000

    def offset(millis: Long): Int = -5 * 60 * 60 * 1000

    def getID: String = "EST"

    def getDisplayName: String = "Eastern Standard Time"
  }

  private val tz = mutable.HashMap[String, Timezone]("UTC" -> UTC, "EST" -> EST)

  def forID(id: String): Option[Timezone] = tz get id

}

trait Timezone {

  def offset(year: Int, month: Int, day: Int, millis: Int): Int

  def offset(millis: Long): Int

  def getID: String

  def getDisplayName: String

}
