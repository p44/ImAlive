package actors

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


/** Simulation logic */
object SimulatorHelper {

  val imaliveUrl = "http://localhost:9000/imalive"

  /** POSTs single one time json to imalive service*/
  def simulateOne = {
    val now: Long = System.currentTimeMillis
    val j:JsValue = Json.obj(
      "customer" -> 1,
      "mac" -> "1234567890",
      "message" -> "I'm Alive",
      "timestamp" -> JsNumber(now))
    val callImAlive: WS.WSRequestHolder = WS.url("http://localhost:9000/imalive")
    Logger.info("SimulateOne - post content " + j)
    callImAlive.post(j) //callImAlive.post(JsNull)
  }
  
  /** POSTs repeating multiple json to imalive service*/
  def simulateMultipleRepeating = {
    
  }
  
  
}