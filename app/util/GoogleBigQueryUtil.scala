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

import com.google.api.services.bigquery.Bigquery
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
 * https://developers.google.com/accounts/docs/OAuth2?csw=1
 * https://developers.google.com/bigquery/docs/authorization
 * https://developers.google.com/accounts/docs/OAuth2ServiceAccount#libraries (service account access)
 */
object GoogleCredentialsUtil {

  val HTTP_TRANSPORT: HttpTransport = new NetHttpTransport()
  val JSON_FACTORY: JsonFactory = new JacksonFactory()
  val RESOURCE_LOCATION: String = models.Models.GOOGLE_CLIENT_SECRETS_LOCATION
  val REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob"
  lazy val clientSecrets: GoogleClientSecrets = getClientCredentialsFromConfig(RESOURCE_LOCATION)
  lazy val flow = newFlow(clientSecrets)
  lazy val refreshToken = ""

  //  lazy val bigquery: Bigquery = buildNewBigquery
  //
  //  /**
  //   * 
  //   */
  //  def buildNewBigquery: Bigquery = {
  //    val r = (new Bigquery.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets))
  //      .setApplicationName("ImAlive")
  //      .setHttpRequestInitializer(clientSecrets).build();
  //    println("buildNewBigquery - r " + r)
  //    r
  //  }

  /**
   * Reads file at path provided into a List of Strings
   */
  def getResourceLines(resourceLocation: String): List[String] = {
    val f = play.Play.application().getFile(resourceLocation);
    println("GoogleCredentialsUtil.getResourceLines - " + f.getAbsolutePath)
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
  def newFlow(cSecrets: GoogleClientSecrets): GoogleAuthorizationCodeFlow = {
    val r = new GoogleAuthorizationCodeFlow.Builder(
      HTTP_TRANSPORT,
      JSON_FACTORY,
      cSecrets,
      java.util.Collections.singleton(BigqueryScopes.BIGQUERY))
      //.setCredentialStore(new AppEngineCredentialStore()) // not using app engine, credentialStore is deprecated
      .setAccessType("offline")
      .setApprovalPrompt("auto")
      .build();
    r
  }

  /**
   * With no existing code, request an authorization code from Google
   */
  def buildAuthorizationUrl(cSecrets: GoogleClientSecrets): String = {
    val authorizeUrl: String = new GoogleAuthorizationCodeRequestUrl(cSecrets, REDIRECT_URI,
      java.util.Collections.singleton(BigqueryScopes.BIGQUERY)).setState("").build();
    authorizeUrl
  }

  /**
   * Exchange the authorization code for OAuth 2.0 credentials.
   */
  def exchangeCode(authorizationCode: String): Credential = {
    val gtResponse: GoogleTokenResponse = flow.newTokenRequest(authorizationCode).setRedirectUri(REDIRECT_URI).execute();
    println("GoogleCredentialsUtil.exchangeCode gtResponse" + gtResponse)
    flow.createAndStoreCredential(gtResponse, null)
  }

  /**
   * Get a Bigquery from a Credential
   */
  def buildService(credential: Credential): Bigquery = {
    (new Bigquery.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)).build();
  }

}
