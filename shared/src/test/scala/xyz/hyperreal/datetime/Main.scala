package xyz.hyperreal.datetime

object Main extends App {

  val d = Datetime(1, 2021, 2, 24, 22, 27, 23, 123)

  println(d.toISOString)

}
