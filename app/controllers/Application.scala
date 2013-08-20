package controllers

import models._
import play.api.mvc._
import play.api.libs.json.JsValue
import play.api.libs.iteratee.{ Concurrent, Enumeratee, Enumerator }
import play.api.libs.EventSource
import play.api.Logger

/**
 * Entry application controller
 */
object Application extends Controller with Secured {

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
    Logger.info("postAliveMessage - request.headers " + req.headers)
    Logger.info("postAliveMessage - request.body.asJson " + jsonValue)
    jsonValue match {
      case None => {
        Logger.info("postAliveMessage - Bad request body " + req.body)
        BadRequest("Could not resolve request body as json " + req.body)
      }
      case _ => {
        // TODO utilize token in oAuth bearer token manner in security wrapper for this POST
        val secToken: Option[String] = req.headers.get(ImAliveConstants.SECURITY_TOKEN_KEY)
        Logger.info("postAliveMessage - secToken " + secToken + " is defined " + secToken.isDefined) 
        // NOTE: assumes a standard format of json here
        // TODO: push to a big data store
        aliveChannel.push(jsonValue.get); // publish to the channel
        Ok
      }
    }

  }

  /** Enumeratee for filtering messages based on customer * /
  def filter(customerid: String) = Enumeratee.filter[JsValue] { json: JsValue =>
    val i = (json \ "customerid").as[String]
    val b: Boolean = (i == customerid)
    println("filtercustomer - json " + json)
    println("filtercustomer - comparing the customer id " + customerid  + " to the json customer id " + i + " for a result of " + b)
    b
  }
  */
  
  def filtercustomer(cid: String) = Enumeratee.filter[JsValue] { json: JsValue => (json \ "customerid").as[String] == cid }

  /** Enumeratee for detecting disconnect of the stream */
  def connDeathWatch(addr: String): Enumeratee[JsValue, JsValue] = {
    Enumeratee.onIterateeDone { () =>
      println(addr + " - imAliveFeed disconnected")
    }
  }

  /** Controller action serving activity based on customer */
  def imAliveFeed(customerid: String) = Action { req =>
    Logger.info("FEED imAliveFeed (customer filtered [" + customerid + "] - " + req.remoteAddress + " - imAliveFeed connected")
    //val filterResult = filtercustomer(customerid: String)
    //println("imAliveFeed - filterResult " + filterResult)
    Ok.stream(aliveOut
      &> filtercustomer(customerid: String)
      &> Concurrent.buffer(50)
      &> connDeathWatch(req.remoteAddress)
      &> EventSource()).as("text/event-stream")
  }
  
  /** Controller action serving activity all (no filter) */
  def imAliveFeedAll = Action { req =>
    Logger.info("FEED imAliveFeedAll - " + req.remoteAddress + " - imAliveFeed connected")
    Ok.stream(aliveOut
      &> Concurrent.buffer(50)
      &> connDeathWatch(req.remoteAddress)
      &> EventSource()).as("text/event-stream")
  }

}

