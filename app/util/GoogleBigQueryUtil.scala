package util

// https://developers.google.com/bigquery/docs/authorization

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.auth.oauth2.CredentialStore // deprecated
import com.google.api.client.auth.oauth2.TokenResponse
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse

import com.google.api.services.bigquery.BigqueryScopes

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
  
  val HTTP_TRANSPORT: HttpTransport = new NetHttpTransport()
  val JSON_FACTORY: JsonFactory = new JacksonFactory()
  //val RESOURCE_LOCATION: String = "conf/google_client_secrets.json";
  val RESOURCE_LOCATION: String = models.Models.GOOGLE_CLIENT_SECRETS_LOCATION
  lazy val clientSecrets: GoogleClientSecrets = null;
  
  /**
   * Reads RESOURCE_LOCATION into List of Strings
   */
  def getResourceLines(resourceLocation: String): List[String] = {
    val f = play.Play.application().getFile(resourceLocation);
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
  def getClientCredentialsFromConfig(resourceLocation: String): GoogleClientSecrets = {
    val inputStream: java.io.InputStream = new java.io.FileInputStream(new java.io.File(resourceLocation))
    //val ras = getClass.getResourceAsStream(resourceLocation) // this doesn't work, ras is null
    GoogleClientSecrets.load(JSON_FACTORY, new java.io.InputStreamReader(inputStream))
  }

  /**
   * 
   */
  def newFlow: GoogleAuthorizationCodeFlow = {
    val r = new GoogleAuthorizationCodeFlow.Builder(
      HTTP_TRANSPORT,
      JSON_FACTORY,
      getClientCredentialsFromConfig(RESOURCE_LOCATION),
      java.util.Collections.singleton(BigqueryScopes.BIGQUERY))
      //.setCredentialStore(new AppEngineCredentialStore()) // not using app engine, credentialStore is deprecated
      .setAccessType("offline")
      .setApprovalPrompt("auto")
      .build();
    r
  }

}
