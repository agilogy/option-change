package com.agilogy.json

import _root_.com.agilogy.json.OptionChangeFormat.OptionChangePathOps
import org.scalatest.FlatSpec
import play.api.libs.functional.syntax._
import play.api.libs.json._

class OptionChangeTest extends FlatSpec {


  case class Test(a1: OptionChange[Int], a2: OptionChange[Int], a3: OptionChange[Int])

  object Test {

    val testReads = (
      (__ \ "a1").readOptionChange[Int] and
        (__ \ "a2").readOptionChange[Int] and
        (__ \ "a3").readOptionChange[Int]
      )(Test.apply _)

    val testWrites = (
      (__ \ "a1").writeOptionChange[Int] and
        (__ \ "a2").writeOptionChange[Int] and
        (__ \ "a3").writeOptionChange[Int]
      )(unlift(Test.unapply _))

    implicit val fmt = (
      (__ \ "a1").formatOptionChange[Int] and
        (__ \ "a2").formatOptionChange[Int] and
        (__ \ "a3").formatOptionChange[Int]
      )(Test.apply _, unlift(Test.unapply _))
  }

  it should "be serializable to JSON" in {
    val obj = Test(LeaveUnchanged, SetNone, SetSome(3))
    val json = Json.obj("a2" -> JsNull, "a3" -> JsNumber(3))
    assert(Json.toJson(obj) === json)
    assert(Json.fromJson(json)(Test.testReads) === JsSuccess(obj))

  }

}
