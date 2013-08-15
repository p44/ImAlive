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
  
  /** Controller action for POSTing I'm Alive messages */
  def postAliveMessage = Action(parse.json) { request => 
    val b = request.body
    aliveChannel.push(b); 
    Ok 
  }

  /** Enumeratee for filtering messages based on room */
  def filtercustomer(customerid: String) = Enumeratee.filter[JsValue] { json: JsValue =>  (json \ "customerid").as[String] == customerid }

}

