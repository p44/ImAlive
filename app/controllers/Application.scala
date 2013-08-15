package controllers

import play.api.Play.current
import play.api.libs.concurrent.Akka
import scala.concurrent.Future
import play.api.libs.iteratee._
import play.api.libs.concurrent._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.Logger

/**
 * Entry application controller
 */
object Application extends Controller {
	
	/** main page */
	def index = Action {
	  Ok(views.html.index.render)
	}
}

