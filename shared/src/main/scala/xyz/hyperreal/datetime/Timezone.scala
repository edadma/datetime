package xyz.hyperreal.datetime

import scala.collection.mutable

object UTC extends Timezone {

  def offset(year: Int, month: Int, day: Int, millis: Int): Int = 0

  def offset(millis: Long): Int = 0

  def getID: String = "UTC"

  def getDisplayName: String = "Coordinated Universal Time"

}

object Timezone {

  private val tz = mutable.HashMap[String, Timezone]("UTC" -> UTC)

  def forID(id: String): Option[Timezone] = tz get id

}

trait Timezone {

  def offset(year: Int, month: Int, day: Int, millis: Int): Int

  def offset(millis: Long): Int

  def getID: String

  def getDisplayName: String

}
