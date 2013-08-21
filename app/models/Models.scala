package models

import play.api.libs.json._

object Models {}

/** */
case class Customer(id: Long, name: String)
object Customer {
  implicit val custWriter = Json.writes[Customer] // Json.toJson(c)
  implicit val custReader = Json.reads[Customer] // 
  def empty: Customer = { Customer(0L, "") }
  
  /** explicit conversion using Json.obj */
  def toJsValue(obj: Customer): JsValue = {
    Json.obj(
      "id" -> JsNumber(obj.id),
      "name" -> obj.name)
  }
  /** explicit parsing from JsValue */
  def fromJsValue(j: JsValue): Customer = {
    val id: JsValue = j \ "id"
    val name: JsValue = j \ "name"
    Customer(id.as[Long], name.as[String])
  }
}
