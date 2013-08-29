package util

import org.specs2.mutable._
import play.api.libs.json._
import play.api.test.FakeApplication
import play.api.test.Helpers._
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets

object GoogleBigQueryUtilSpec extends Specification {

  //sbt > test-only util.GoogleBigQueryUtilSpec
  
  val ATUH_URI = "https://accounts.google.com/o/oauth2/auth"

  "GoogleBigQueryUtil" should {

    "GoogleCredentialsUtil.getResourceLines" in {
      running(FakeApplication()) {
        val lines = GoogleCredentialsUtil.getResourceLines
        println("GoogleCredentialsUtil.getResourceLines...")
        lines.foreach(println(_))
        lines mustNotEqual Nil
        lines.size > 5 mustEqual true
      }
    }

    // getClientCredentialsExplicit(authUri: String, clientId: String, clientSecret: String, redirectUris: List[String]): GoogleClientSecrets
    "GoogleCredentialsUtil.getClientCredentialsExplicit" in {
      val cId = "XXXXXXXXXXXXXXXXXXXX.apps.googleusercontent.com"
      val cs = "XXXXXXXXXXXXXXXXXXXX"
      val rUris = List("http://localhost/oauth2callback", "https://www.example.com/oauth2callback")
      val gcs: GoogleClientSecrets = GoogleCredentialsUtil.getClientCredentialsExplicit(ATUH_URI, cId, cs, rUris)
      println("GoogleCredentialsUtil.getClientCredentialsExplicit - gcs" + gcs)
      gcs.getDetails.getClientId mustEqual cId
      gcs.getDetails.getAuthUri mustEqual ATUH_URI
    }

    "io test" in {
      running(FakeApplication()) {
        val path = "conf/google_client_secrets.json"
        val inputStream: java.io.InputStream = new java.io.FileInputStream(new java.io.File(path))
        val ras = getClass.getResourceAsStream(path)
        println("ras - " + ras + " inputstream - " + inputStream)
        inputStream mustNotEqual null
      }
    }

    "GoogleCredentialsUtil.getClientCredentialsFromConfig" in {
      running(FakeApplication()) {
        val gcs: GoogleClientSecrets = GoogleCredentialsUtil.getClientCredentialsFromConfig
        println("GoogleCredentialsUtil.getClientCredentialsFromConfig - gcs" + gcs)
        gcs.getDetails.getClientId.isEmpty mustNotEqual true
        gcs.getDetails.getAuthUri mustEqual ATUH_URI
        
      }
    }
  }

}