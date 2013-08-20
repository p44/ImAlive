package controllers

import models._
import play.api.mvc.Action
import play.api.mvc.Request
import play.api.mvc.RequestHeader
import play.api.mvc.Results.Unauthorized
import play.api.mvc.Security
import play.api.mvc.Result
import play.api.mvc.AnyContent

trait Secured {
  
  def onUnauthorized(request: RequestHeader) = Unauthorized("Authorization Failure")
  def headerToken(request: RequestHeader): Option[String] = {
    request.headers.get(ImAliveConstants.SECURITY_TOKEN_KEY)
  }
  def headerTokenAthenticated(request: RequestHeader): Option[String] = {
    val ot = headerToken(request)
    ot.isDefined match {
      case false => None
      case _ => {
        (ot.get == ImAliveConstants.SIMULATION_SECURITY_TOKEN) match {
          case false => None
          case _ => ot
        }
      }
    }
  }
  
  /** Enforces valid token in the header */
  def withTokenAuth(f: => String => Request[AnyContent] => Result) = { 
    Security.Authenticated(headerTokenAthenticated, onUnauthorized) { token =>
      Action(request => f(token)(request))
    }
  }
  
}