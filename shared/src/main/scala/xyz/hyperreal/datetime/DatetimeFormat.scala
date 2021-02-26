package xyz.hyperreal.datetime

import scala.collection.mutable.ListBuffer
import xyz.hyperreal.char_reader._
import xyz.hyperreal.datetime

import scala.annotation.tailrec
import scala.collection.immutable.ArraySeq

object DatetimeFormat {

  val ISO: DatetimeFormat = DatetimeFormat.parse("YYYY-MM-DDThh:mm:ss.fffZ")
  val DISPLAY_DATE: DatetimeFormat = DatetimeFormat.parse("WWWW, MMMM D, YYYY |at| h12:mm a")

  private abstract class Element
  private case class StringElement(s: String) extends Element
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

  private val monthsShort = ArraySeq("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
  private val monthsLong =
    ArraySeq("January",
             "February",
             "March",
             "April",
             "May",
             "June",
             "July",
             "August",
             "September",
             "October",
             "November",
             "December")
  private val weekdayShort = ArraySeq("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
  private val weekdayLong = ArraySeq("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")

  def parse(s: String): DatetimeFormat = {
    val buf = new ListBuffer[Element]
    val r = CharReader.fromString(s)

    def rep(c: Char, r: CharReader): (Int, CharReader) = {
      @tailrec
      def rep(r: CharReader, count: Int): (Int, CharReader) =
        r.ch match {
          case `c` => rep(r.next, count + 1)
          case _   => (count, r)
        }

      rep(r, 0)
    }

    def string(r: CharReader, s: String): Boolean = {
      @tailrec
      def string(r: CharReader, idx: Int): Boolean =
        if (idx == s.length) true
        else if (r.ch == s(idx)) string(r.next, idx + 1)
        else false

      string(r, 0)
    }

    @tailrec
    def skip(r: CharReader, count: Int): CharReader =
      if (r.eoi || count <= 0) r
      else skip(r.next, count - 1)

    def literal(delim: Char, r: CharReader): Option[(String, CharReader)] = {
      val buf = new StringBuilder

      @tailrec
      def literal(r: CharReader): Option[(String, CharReader)] =
        r.ch match {
          case `delim`        => Some((buf.toString, r.next))
          case CharReader.EOI => None
          case c =>
            buf += c
            literal(r.next)
        }

      literal(r)
    }

    @tailrec
    def element(r: CharReader): Unit =
      r.ch match {
        case CharReader.EOI =>
        case '|' =>
          literal('|', r.next) match {
            case None => r.error("unclosed literal")
            case Some((s, rest)) =>
              buf += StringElement(s)
              element(rest)
          }
        case 'h' if string(r, "h12") =>
          buf += Hour12Element(1)
          element(skip(r, 3))
        case 'h' if string(r, "hh12") =>
          buf += Hour12Element(2)
          element(skip(r, 4))
        case 'Y' | 'M' | 'D' | 'W' | 'h' | 'a' | 'm' | 's' | 'f' =>
          val (count, rest) = rep(r.ch, r)

          buf +=
            ((r.ch, count) match {
              case ('Y', 4 | 2)           => YearElement(count)
              case ('M', 1 | 2 | 3 | 4)   => MonthElement(count)
              case ('D', 1 | 2)           => DayElement(count)
              case ('W', 3 | 4)           => WeekdayElement(count)
              case ('h', 1 | 2)           => HourElement(count)
              case ('a', 1)               => AmPmElement
              case ('m', 1 | 2)           => MinuteElement(count)
              case ('s', 1 | 2)           => SecondElement(count)
              case ('f', _) if count <= 9 => FractionalElement(count)
              case _                      => r.error(s"unrecognized date/time format element: '${r.ch.toString * count}'")
            })
          element(rest)
        case _ =>
          buf += StringElement(r.ch.toString)
          element(r.next)
      }

    element(r)
    new datetime.DatetimeFormat(buf.toList)
  }

}

class DatetimeFormat private (elems: List[DatetimeFormat.Element]) {

  import DatetimeFormat._

  private def variants(variant: Int, value: Int): String =
    variant match {
      case 1 => value.toString
      case 2 => f"$value%02d"
    }

  def format(d: Datetime): String =
    (elems map {
      case AmPmElement               => if (d.hours < 12) "a.m." else "p.m."
      case StringElement(s)          => s
      case DayElement(v)             => variants(v, d.day)
      case HourElement(v)            => variants(v, d.hours)
      case YearElement(2)            => (d.year % 100).toString
      case YearElement(4)            => d.year.toString
      case MonthElement(v @ (1 | 2)) => variants(v, d.month)
      case MonthElement(3)           => monthsShort(d.month - 1)
      case MonthElement(4)           => monthsLong(d.month - 1)
      case Hour12Element(v)          => variants(v, if (d.hours > 12) d.hours - 12 else d.hours)
      case MinuteElement(v)          => variants(v, d.minutes)
      case SecondElement(v)          => variants(v, d.seconds)
      case WeekdayElement(3)         => weekdayShort(d.dayOfWeek)
      case WeekdayElement(4)         => weekdayLong(d.dayOfWeek)
      case FractionalElement(1)      => (d.millis / 100).toString
      case FractionalElement(2)      => f"${d.millis / 10}%02d"
      case FractionalElement(3)      => f"${d.millis}%03d"
      case FractionalElement(4)      => f"${d.millis}%03d${d.nanos / 100000}%d"
      case FractionalElement(5)      => f"${d.millis}%03d${d.nanos / 10000}%02d"
      case FractionalElement(6)      => f"${d.millis}%03d${d.nanos / 1000}%03d"
      case FractionalElement(7)      => f"${d.millis}%03d${d.nanos / 100}%04d"
      case FractionalElement(8)      => f"${d.millis}%03d${d.nanos / 10}%05d"
      case FractionalElement(9)      => f"${d.millis}%03d${d.nanos}%06d"
    }).mkString

}
