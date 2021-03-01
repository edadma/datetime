package xyz.hyperreal.datetime

import org.scalatest._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class BasicTests extends AnyFreeSpec with Matchers {

  val time = 1614522673991L

  "fromMillis" in {
    Datetime.fromMillis(time, Timezone.ET) shouldBe Datetime(2021, 2, 28, 9, 31, 13, 991000000)
  }

  "toISOString" in {
    Datetime.fromMillis(time).toISOString shouldBe "2021-02-28T14:31:13.991Z"
  }

  "millis" in {
    Datetime(2021, 2, 28, 14, 31, 13, 991000000).epochMillis shouldBe time
  }

  "dayOfWeek" in {
    Datetime(2021, 2, 28, 14, 31, 13, 991000000).dayOfWeek shouldBe 0
  }

  "fromString" in {
    Datetime.fromString("2021-02-28T14:31:13.991Z") shouldBe Datetime(2021, 2, 28, 14, 31, 13, 991000000)
  }

}
