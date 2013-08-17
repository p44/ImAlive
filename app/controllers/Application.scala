package controllers

import play.api.mvc._
import play.api.libs.json.JsValue
import play.api.libs.iteratee.{ Concurrent, Enumeratee, Enumerator }
import play.api.libs.EventSource
import play.api.Logger

/**
 * Entry application controller
 */
object Application extends Controller {

  /**
   * Central hub for distributing I'm Alive messages
   *  aliveOut: Enumerator[JsValue]
   *  aliveChannel: Channel[JsValue]
   */
  val (aliveOut, aliveChannel) = Concurrent.broadcast[JsValue] // aliveOut: Enumerator[JsValue] 

  /** main page */
  def index = Action {
    Ok(views.html.index.render)
  }

  /** Controller action for Posting 'I'm Alive' messages */
  def postAliveMessage = Action { req =>
    val jsonValue: Option[JsValue] = req.body.asJson
    Logger.info("postAliveMessage - request.body.asJson " + jsonValue)
    jsonValue match {
      case None => {
        Logger.info("postAliveMessage - Bad request body " + req.body)
        BadRequest("Could not resolve request body as json " + req.body)
      }
      case _ => {
        aliveChannel.push(jsonValue.get);
        Ok
      }
    }

  }

  /** Enumeratee for filtering messages based on customer */
  def filtercustomer(customerid: String) = Enumeratee.filter[JsValue] { json: JsValue =>
    val i = (json \ "customerid").as[String]
    val b = i == customerid
    Logger.debug("filtercustomer - json id " + i + " customerid " + customerid + " match " + b)
    b
  }

  /** Enumeratee for detecting disconnect of the stream */
  def connDeathWatch(addr: String): Enumeratee[JsValue, JsValue] = {
    Enumeratee.onIterateeDone { () =>
      Logger.info(addr + " - imAliveFeed disconnected")
    }
  }

  /** Controller action serving activity based on customer */
  def imAliveFeed(customerid: String) = Action { req =>
    Logger.info(req.remoteAddress + " - imAliveFeed connected")
    Ok.stream(aliveOut
      &> filtercustomer(customerid)
      &> Concurrent.buffer(50)
      &> connDeathWatch(req.remoteAddress)
      &> EventSource()).as("text/event-stream")
  }
  
  /** Controller action serving activity all (no filter) */
  def imAliveFeedAll = Action { req =>
    Logger.info(req.remoteAddress + " - imAliveFeed connected")
    Ok.stream(aliveOut
      &> Concurrent.buffer(50)
      &> connDeathWatch(req.remoteAddress)
      &> EventSource()).as("text/event-stream")
  }

}

