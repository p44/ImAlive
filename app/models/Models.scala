package models

import play.api.libs.json._

object Models {
  
  val cid1: Long = 1L
  val cid2: Long = 2L
  lazy val customers: List[Customer] = List[Customer](Customer(cid1, "Govermnet Snoopers"), Customer(cid2, "Hackers Unlimited"))
  lazy val customerDeviceCatalog: List[Device] = List[Device](
      // Device(id: Long, cid: Long, mac: String, ip: String, lat: Long, long: Long, desc: String, misc: String)
      Device(1001L, cid1, "0090990F9F99", "222.222.222.222", 1, 1, "Snooper Alpha 99", "12,0,66,99,1,1,0"),
      Device(1002L, cid1, "0090990F9F87", "222.222.222.221", 1, 1, "Snooper Alpha 87", "11,3,62,92,1,0,5")
      )

}

/** */
case class Customer(id: Long, name: String)
object Customer {
  implicit val custWriter = Json.writes[Customer] // Json.toJson(obj): JsValue
  implicit val custReader = Json.reads[Customer] // Json.fromJson[Customer](jsval): JsResult[Customer]

  def empty: Customer = { Customer(0L, "") }

  /** explicit conversion using Json.obj */
  def toJsValue(obj: Customer): JsValue = {
    Json.obj(
      "id" -> JsNumber(obj.id),
      "name" -> obj.name)
  }
  /** explicit parsing from JsValue */
  def fromJsValue(j: JsValue): Option[Customer] = {
    val id: JsValue = j \ "id"
    val name: JsValue = j \ "name"
    try {
      val r = Customer(id.as[Long], name.as[String])
      Some(r)
    } catch {
      case e: play.api.libs.json.JsResultException => None
    }
  }
}

/** */
case class Device(id: Long, cid: Long, mac: String, ip: String, lat: Long, long: Long, desc: String, misc: String)
object Device {
  implicit val deviceWriter = Json.writes[Device] // Json.toJson(obj): JsValue
  implicit val deviceReader = Json.reads[Device] // Json.fromJson[Device](jsval): JsResult[Device]

  def empty: Device = { Device(0L, 0L, "", "", 0L, 0L, "", "") }
}

