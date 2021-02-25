package xyz.hyperreal.datetime

import scala.collection.mutable.ListBuffer

import xyz.hyperreal.char_reader._

object DatetimeFormat {

  private abstract class Element
  private case class CharElement(c: Char) extends Element
  private case class YearElement(variant: Int) extends Element
  private case class MonthElement(variant: Int) extends Element
  private case class DayElement(variant: Int) extends Element
  private case class WeekdayElement(variant: Int) extends Element
  private case class HourElement(variant: Int) extends Element
  private case class Hour12Element(variant: Int) extends Element
  private case class MinuteElement(variant: Int) extends Element
  private case class SecondElement(variant: Int) extends Element
  private case object AmPmElement extends Element
  private case class FractionalElement(decimals: Int) extends Element

  def parse(s: String): DatetimeFormat = {
    val buf = new ListBuffer[Element]

  }
}

class DatetimeFormat private (elems: List[DatetimeFormat.Element]) {}
