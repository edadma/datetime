package xyz.hyperreal.datetime

import org.scalatest._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class BasicTests extends AnyFreeSpec with Matchers {

  val ms = 1614522673991L

  "fromMillis" in {
    Datetime.fromMillis(ms, Timezone.ET) shouldBe Datetime(2021, 2, 28, 9, 31, 13, 991000000)
  }

  "toISOString" in {
    Datetime.fromMillis(ms).toISOString shouldBe "2021-02-28T14:31:13.991Z"
  }

  "millis" in {
    Datetime(2021, 2, 28, 14, 31, 13, 991000000).millis shouldBe ms
  }

  "dayOfWeek" in {
    Datetime(2021, 2, 28, 14, 31, 13, 991000000).dayOfWeek shouldBe 0
  }

  "fromString" in {
    Datetime.fromString("2021-02-28T14:31:13.991Z") shouldBe Datetime(2021, 2, 28, 14, 31, 13, 991000000)
  }

}
