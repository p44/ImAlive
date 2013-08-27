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

  /** ImAlive actor system */
  val system = ActorSystem("imalive-simulate")

  /** Supervisor for Simulation */
  val supervisor = system.actorOf(Props(new SimulatorSupervisor()), "SimulatorSupervisor")

  case object SimulateOne
}

/** Supervisor initiating simulation actors and scheduling */
class SimulatorSupervisor extends Actor {

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
      // POSTs to imalive
      SimulatorHelper.simulateOne
  }

}


/** Simulation logic used by actors or testing */
object SimulatorHelper {
  
  val possibleMessages = Seq("Yo", "I'm Alive", "On Line", "All Good Here", "Still Running", "Yep", "Bueno", "Boo", "Hola", "Status Green")
  def genMessage: String = { possibleMessages(scala.util.Random.nextInt(possibleMessages.size)) }

  /** POSTs single one time json to imalive service*/
  def simulateOne = {
    val now: Long = System.currentTimeMillis
    val j:JsValue = genOneJsonFixed(1L, "1234567890", now)
    val callImAlive: WS.WSRequestHolder = WS.url(ImAliveConstants.URL_IMALIVE)
    Logger.info("SimulateOne - post status " + j)
    // TODO oAuth 2 simple bearer token
    callImAlive.withHeaders((ImAliveConstants.SECURITY_TOKEN_KEY, ImAliveConstants.SIMULATION_SECURITY_TOKEN)).post(j) 
    //callImAlive.post(JsNull)
  }
  
  /** simple one json message with "timestamp" param in millis as specified */
  def genOneJsonFixed(custId: Long, mac: String, tsMillis: Long): JsValue = {
    Json.obj(
      "customerid" -> JsNumber(custId),
      "mac" -> mac,
      "message" -> "I'm Alive",
      "timestamp" -> JsNumber(tsMillis))
  }
  
  /**
   * Uses Models.customerDeviceCatalog and genMessage to create a status message
   */
  def genOneJsonRandom(tsMillis: Long): JsValue = {
    val d = Models.getRandomDeviceFromCatalog
    val sm = StatusMessage.simulateFromDevice(d, tsMillis, genMessage)
    Json.toJson(sm)
  }
  
  /** POSTs repeating multiple json to imalive service*/
  def simulateMultipleRepeating = {
    
  }
  
  
}