import actors._
import play.api.GlobalSettings
import play.api.mvc.Results._
import play.api.mvc.RequestHeader

object Global extends GlobalSettings {

  override def onStart(application: play.api.Application) {
    SimulatorActors
  }
  
  override def onStop(application: play.api.Application) { 
    SimulatorActors.system.shutdown()
  }
  
  override def onBadRequest(request: RequestHeader, error: String) = {
    BadRequest("Bad Request: " + error)
  } 
  
  override def onError(request: RequestHeader, ex: Throwable) = {
    InternalServerError(views.html.e500(ex))
  }
}