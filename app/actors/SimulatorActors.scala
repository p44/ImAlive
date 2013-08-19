package actors

import models._
import akka.actor._
import java.util.Random
import scala.collection.immutable.Queue
import scala.collection.JavaConverters._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import play.libs.Akka
import play.api.libs.ws.WS
import play.api.Logger
import play.api.libs.json._

/** Simulator Actor system primary setup; references and messages */
object SimulatorActors {

  /** SSE-Chat actor system */
  val system = ActorSystem("imalive-simulate")

  /** Supervisor for Romeo and Juliet */
  val supervisor = system.actorOf(Props(new Supervisor()), "Supervisor")

  case object SimulateOne
}

/** Supervisor initiating simulation actors and scheduling */
class Supervisor() extends Actor {

  val simulator = context.actorOf(Props(new SimulatorActor))
  context.system.scheduler.schedule(1 seconds, 15 seconds, simulator, SimulatorActors.SimulateOne)

  def receive = { case _ => }
}

/**
 * Does the simulation - POSTs calls to the controller with I'm Alive messages as an external client would
 */
class SimulatorActor extends Actor {

  def receive = {
    case SimulatorActors.SimulateOne => // simulates an external client posting alive messages
      // POST to imalive
      SimulatorHelper.simulateOne
  }

}


/** Simulation logic used by actors or testing */
object SimulatorHelper {

  /** POSTs single one time json to imalive service*/
  def simulateOne = {
    val now: Long = System.currentTimeMillis
    val j:JsValue = genOneJson(1L, "1234567890", now)
    val callImAlive: WS.WSRequestHolder = WS.url(ImAliveConstants.URL_IMALIVE)
    Logger.info("SimulateOne - post status " + j)
    // TODO oAuth 2 simple bearer token
    callImAlive.withHeaders((ImAliveConstants.SECURITY_TOKEN_KEY, ImAliveConstants.SIMULATION_SECURITY_TOKEN)).post(j) 
    //callImAlive.post(JsNull)
  }
  
  /** simple one json message with "timestamp" param in millis as specified */
  def genOneJson(custId: Long, mac: String, tsMillis: Long): JsValue = {
    Json.obj(
      "customer" -> JsNumber(custId),
      "mac" -> mac,
      "message" -> "I'm Alive",
      "timestamp" -> JsNumber(tsMillis))
  }
  
  /** POSTs repeating multiple json to imalive service*/
  def simulateMultipleRepeating = {
    
  }
  
  
}