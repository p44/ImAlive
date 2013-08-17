import actors._
import play.api.GlobalSettings

object Global extends GlobalSettings {

  override def onStart(application: play.api.Application) {
    SimulatorActors
  }
  
  override def onStop(application: play.api.Application) { 
    SimulatorActors.system.shutdown()
  }
}