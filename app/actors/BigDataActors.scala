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

object BigDataActors {

  /** SSE-Chat actor system */
  val system = ActorSystem("imalive-bigdata")

  /** Supervisor for Romeo and Juliet */
  val supervisor = system.actorOf(Props(new BigDataSupervisor), "BigDataSupervisor")

  case class ReceiveOneStatusMessage(json: JsValue)

}

/** Supervisor initiating simulation actors and scheduling */
class BigDataSupervisor extends Actor {

  val statusMessageReceiver = context.actorOf(Props(new StatusMessageReceivingActor))

  def receive = {
    case BigDataActors.ReceiveOneStatusMessage(json) =>
      statusMessageReceiver ! BigDataActors.ReceiveOneStatusMessage(json)
  }
}

/**
 * Handles the receipt of a status message
 */
class StatusMessageReceivingActor extends Actor {

  def receive = {
    case BigDataActors.ReceiveOneStatusMessage(json) => // simulates an external client posting alive messages
      Logger.info("StatusMessageReceivingActor - TODO implement - store in big query - json " + json)
  }

}