package actors

import models._
import org.specs2.mutable._
import play.api.libs.json._

object SimulatorHelperSpec extends Specification {
  
  //sbt > test-only actors.SimulatorHelperSpec
  
  "SimulatorHelper" should {
    "genMessage" in {
      val s = SimulatorHelper.genMessage
      println("genMessage s " + s)
      s.length > 1 mustEqual true
      SimulatorHelper.possibleMessages.contains(s) mustEqual true
    }
    
    "genOneJsonRandom" in {
      val now = System.currentTimeMillis
      val smJsVal: JsValue = SimulatorHelper.genOneJsonRandom(now)
      println("genOneJsonRandom smJsVal - " + smJsVal)
      // convert to an obj
      val osm: Option[StatusMessage] = Json.fromJson[StatusMessage](smJsVal).asOpt 
      println("genOneJsonRandom osm - " + osm)
      osm mustNotEqual None
      val obj = osm.get
      (obj.cid == Models.cid1 || obj.cid == Models.cid2) mustEqual true
    }
  }

}