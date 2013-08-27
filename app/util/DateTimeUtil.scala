package util

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format._

object DateTimeUtil {
  
  val defaultDate = new DateTime(1971, 1, 1, 0, 0)
  val defaultDateMillis = defaultDate.getMillis()

  val DATE_FORMATTER_STD: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")
  val DATE_FORMATTER_STD_WITH_TIME: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")
  
  /**  */
  def formatLongMillis(ts: Long, formatter: DateTimeFormatter): String = {
    val dt = new DateTime(ts)
    val r: String = formatter.print(dt)
    r
  }

}