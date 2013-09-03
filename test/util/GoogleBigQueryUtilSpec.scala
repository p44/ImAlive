package util

import org.specs2.mutable._
import play.api.libs.json._
import play.api.test.FakeApplication
import play.api.test.Helpers._
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets

object GoogleBigQueryUtilSpec extends Specification {

  //sbt > test-only util.GoogleBigQueryUtilSpec

  val ATUH_URI = "https://accounts.google.com/o/oauth2/auth"
  val RESOURCE_LOCATION_TEST: String = "conf/google_client_secrets.json";

  "GoogleBigQueryUtil" should {

    "GoogleCredentialsUtil.getResourceLines" in {
      running(FakeApplication()) {
        val lines = GoogleCredentialsUtil.getResourceLines(RESOURCE_LOCATION_TEST)
        println("GoogleCredentialsUtil.getResourceLines...")
        lines.foreach(println(_))
        lines mustNotEqual Nil
        lines.size > 5 mustEqual true
      }
    }

    "GoogleCredentialsUtil.getClientSecretsExplicit" in {
      val cId = "XXXXXXXXXXXXXXXXXXXX.apps.googleusercontent.com"
      val cs = "XXXXXXXXXXXXXXXXXXXX"
      val rUris = List("http://localhost/oauth2callback", "https://www.example.com/oauth2callback")
      val gcs: GoogleClientSecrets = GoogleCredentialsUtil.getClientSecretsExplicit(ATUH_URI, cId, cs, rUris)
      println("GoogleCredentialsUtil.getClientCredentialsExplicit - gcs" + gcs)
      gcs.getDetails.getClientId mustEqual cId
      gcs.getDetails.getAuthUri mustEqual ATUH_URI
    }

    "io test" in {
      running(FakeApplication()) {
        val inputStream: java.io.InputStream = new java.io.FileInputStream(new java.io.File(RESOURCE_LOCATION_TEST))
        val ras = getClass.getResourceAsStream(RESOURCE_LOCATION_TEST) // this does not work, ras is null
        println("ras - " + ras + " inputstream - " + inputStream)
        inputStream mustNotEqual null
      }
    }

    "GoogleCredentialsUtil.getClientCredentialsFromConfig" in {
      running(FakeApplication()) {
        val gcs: GoogleClientSecrets = GoogleCredentialsUtil.getClientSecretsFromConfig(RESOURCE_LOCATION_TEST)
        println("GoogleCredentialsUtil.getClientCredentialsFromConfig - gcs" + gcs)
        gcs.getDetails.getClientId.isEmpty mustNotEqual true
        gcs.getDetails.getAuthUri mustEqual ATUH_URI
      }
    }

    "GoogleCredentialsUtil.newFlow" in {
      val gcs: GoogleClientSecrets = GoogleCredentialsUtil.getClientSecretsFromConfig(RESOURCE_LOCATION_TEST)
      val flow = GoogleCredentialsUtil.newFlow(gcs)
      println("GoogleCredentialsUtil.newFlow - flow" + flow)
      flow.getApprovalPrompt mustEqual "auto"
    }

    "GoogleCredentialsUtil.buildAuthorizationUrl" in {
      running(FakeApplication()) {
        val realClientSecretsLoc = "conf/installed_bigquery_client_secrets.json"
        val gcs: GoogleClientSecrets = GoogleCredentialsUtil.getClientSecretsFromConfig(realClientSecretsLoc)
        val rUrl: String = GoogleCredentialsUtil.buildAuthorizationUrl(gcs)
        println("GoogleCredentialsUtil.buildAuthorizationUrl -  rUrl " + rUrl)
        rUrl.isEmpty mustNotEqual true
        rUrl.contains("oauth2") mustEqual true
        rUrl.contains("accounts.google.com") mustEqual true
      }
    }
    
    "GoogleCredentialsUtil.buildServiceCredential" in {
      val rCred = GoogleCredentialsUtil.buildServiceCredential
      println("GoogleCredentialsUtil.buildServiceCredential -  rCred " + rCred)
      rCred mustNotEqual null
    }
    
    // This test requires a valid p12 file in the conf dir as well as accurate setting in application.conf
    // google.service.key.location="conf/put-your-p12-file-from-google-in-conf-privatekey.p12"
    // or you get FileNotFoundException: (No such file or directory) 
    "getBigQueryUsingServiceAuth" in {
      val rBq = GoogleBigQueryUtil.getBigQueryUsingServiceAuth
      println("getBigQueryUsingServiceAuth - rBq " + rBq)
    }


  }

}