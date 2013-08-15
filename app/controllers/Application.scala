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
import play.api.mvc.WebSocket
import play.api.libs.Comet

/**
 * Entry application controller
 */
object Application extends Controller {
	
   /** main page */
   def index = Action {
     Ok(views.html.index.render)
   }
   
   /** Based on Play examples here http://www.playframework.com/documentation/2.1.3/ScalaComet */
   def simplecomet = Action {
     Logger.info("simplecomet - received request for stream")
     val events = Enumerator("one", "two", "three")
     Ok.stream(events &> Comet(callback = "parent.cometMessage"))
   }
   
   /** Based on Play examples here http://www.playframework.com/documentation/2.1.3/ScalaComet */
   def simplestcomet = Action {
     Logger.info("simplestcomet - received request for stream")
     val events = Enumerator(
         """<script>console.log('uno')</script>""",
         """<script>console.log('dos')</script>""",
         """<script>console.log('tres')</script>"""
     )
     Ok.stream(events >>> Enumerator.eof).as(HTML)
   }
	
   /** */
   def simplewebsocket(uuid: String) = WebSocket.using[String] { request => 
  
      // Log events to the console
      val in = Iteratee.foreach[String](println).mapDone { _ =>
	     println("Disconnected")
	  }
  
      // Send a single 'Hello!' message
      val out = Enumerator("Hello! " + uuid)
  
      (in, out)
   }

}

