package util

// https://developers.google.com/bigquery/docs/authorization

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.auth.oauth2.TokenResponse
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse

import com.google.api.client.http.HttpTransport
import com.google.api.client.http.HttpRequestFactory
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory

import scala.collection.JavaConversions._

object GoogleBigQueryUtil {

}

/**
 * 
 */
object GoogleCredentialsUtil {
  
  val TRANSPORT: HttpTransport = new NetHttpTransport()
  val JSON_FACTORY: JsonFactory = new JacksonFactory()
  val RESOURCE_LOCATION: String = "conf/google_client_secrets.json";
  lazy val clientSecrets: GoogleClientSecrets = null;
  
  /**
   * Reads RESOURCE_LOCATION into List of Strings
   */
  def getResourceLines: List[String] = {
    val f = play.Play.application().getFile(RESOURCE_LOCATION);
    println("getResourceLines - " + f.getAbsolutePath)
    scala.io.Source.fromFile(f).getLines.toList
  }
  
  /** */
  def getClientCredentialsExplicit(authUri: String, clientId: String, clientSecret: String, redirectUris: List[String]): GoogleClientSecrets = {
    val jRedirectUris: java.util.List[String] = redirectUris // requires scala.collection.JavaConversions._
    val web: GoogleClientSecrets.Details = new GoogleClientSecrets.Details()
       .setAuthUri(authUri) 
       .setClientId(clientId) 
       .setClientSecret(clientSecret) 
       .setRedirectUris(jRedirectUris) // setRedirectUris(List<String> redirectUris) 
    val r: GoogleClientSecrets = (new GoogleClientSecrets()).setWeb(web)
    r
  }
  
  /** */
  def getClientCredentialsFromConfig: GoogleClientSecrets = {
    val inputStream: java.io.InputStream = new java.io.FileInputStream(new java.io.File(RESOURCE_LOCATION))
    //val ras = getClass.getResourceAsStream(RESOURCE_LOCATION_FULL) // this doesn't work, ras is null
    GoogleClientSecrets.load(JSON_FACTORY, new java.io.InputStreamReader(inputStream))
  }

}