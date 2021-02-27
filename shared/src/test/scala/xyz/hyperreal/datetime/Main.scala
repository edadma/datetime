package xyz.hyperreal.datetime

object Main extends App {

  val d = Datetime(2021, 2, 24, 22, 27, 23, 120000000)

  println(Datetime.from("2021-02-24T22:27:23.12Z") == d)

}
