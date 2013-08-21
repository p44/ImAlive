package models

import org.specs2.mutable._
import play.api.libs.json._

object ModelsSpec extends Specification {
  
  //$ sbt > test-only models.ModelsSpec
  
  "Models" should {
    "serialize to from json" in { // tests json to from explicit and with Json.writes and .reads
      val c = Customer(11L, "Stubs")
      val jsVal: JsValue = Customer.toJsValue(c)
      val jsVal2: JsValue = Json.toJson(c) // uses Customer.custWriter 
      println("jsVal - " + jsVal + " jsVal2 - " + jsVal2)
      jsVal mustEqual jsVal2
      val cParsed: Customer = Customer.fromJsValue(jsVal)
      println("cParsed - " + cParsed)
      cParsed mustEqual c
      val cParsed2: JsResult[Customer] = Json.fromJson[Customer](jsVal2)
      val oc: Option[Customer] = cParsed2.asOpt
      println("cParsed2 - " + cParsed2 + " oc " + oc)
      oc mustNotEqual None
      oc.get mustEqual c
    }
  }

}