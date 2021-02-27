package xyz.hyperreal.datetime

import java.nio.file.{Files, Paths}
import java.time.Clock

object platform {

  def currentTimeMillis: Long = Clock.systemUTC().millis()

}
