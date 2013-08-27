package models

import util._
import play.api.libs.json._

object Models {
  
  val cid1: Long = 1L
  val cid2: Long = 2L
  lazy val customers: List[Customer] = List[Customer](Customer(cid1, "Govermnet Snoopers"), Customer(cid2, "Hackers Unlimited"))
  lazy val customerDeviceCatalog: Seq[Device] = Seq[Device](
      // Device(id: Long, cid: Long, mac: String, ip: String, lat: Long, long: Long, desc: String, misc: String)
      Device(1001L, cid1, "0090990F9F99", "222.222.222.222", 1, 1, "Email reader 39", "12,0,66,99,1,1,0"),
      Device(1002L, cid1, "0090990F9F87", "222.222.222.221", 1, 1, "FB scraper 87", "11,3,62,92,1,0,5"),
      Device(7001L, cid2, "0040690F8A09", "111.333.333.11", 1, 1, "Constitution X", "100110111101"),
      Device(7002L, cid2, "0040110F8B77", "111.333.333.14", 1, 1, "Of The People By The People Z", "101110110101")
      )
      
  def getRandomDeviceFromCatalog: Device =  { customerDeviceCatalog(scala.util.Random.nextInt(customerDeviceCatalog.size)) }

}

/** */
case class Customer(id: Long, name: String)
object Customer {
  implicit val jWriter = Json.writes[Customer] // Json.toJson(obj): JsValue
  implicit val jReader = Json.reads[Customer] // Json.fromJson[Customer](jsval): JsResult[Customer] .asOpt Option[Customer]

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
  implicit val jWriter = Json.writes[Device] // Json.toJson(obj): JsValue
  implicit val jReader = Json.reads[Device] // Json.fromJson[Device](jsval): JsResult[Device] .asOpt Option[Device]

  def empty: Device = { Device(0L, 0L, "", "", 0L, 0L, "", "") }
}

/** */
case class StatusMessage(id: Long, message: String, ts: Long, tsFormatted: String, cid: Long, mac: String, ip: String, lat: Long, long: Long, info: String)
object StatusMessage {
  implicit val jWriter = Json.writes[StatusMessage] // Json.toJson(obj): JsValue
  implicit val jReader = Json.reads[StatusMessage] // Json.fromJson[StatusMessage](jsval): JsResult[StatusMessage] .asOpt Option[StatusMessage]

  def empty: StatusMessage = { StatusMessage(0L, "", DateTimeUtil.defaultDateMillis, "", 0L, "", "", 0L, 0L, "") }
  
  /** build simulated message for a device */
  def simulateFromDevice(d: Device, ts: Long, msg: String): StatusMessage = {
    val tsFormatted = DateTimeUtil.formatLongMillis(ts, DateTimeUtil.DATE_FORMATTER_STD_WITH_TIME)
    StatusMessage(d.id, msg, ts, tsFormatted, d.cid, d.mac, d.ip, d.lat, d.long, d.desc)
  }
}

