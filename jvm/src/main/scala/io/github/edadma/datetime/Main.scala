package io.github.edadma.datetime

@main def run(): Unit =
  var d = Datetime.now()

  d += 2
  println(d.plusDays(2))
