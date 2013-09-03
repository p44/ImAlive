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

  /**
   * get the big query service obj.  Uses google oauth service account (.p12 file)
   * See https://developers.google.com/bigquery/docs/authorization#service-accounts-server
   */
  def getBigQueryUsingServiceAuth: Bigquery = {
    val credential: GoogleCredential = GoogleCredentialsUtil.buildServiceCredential
    GoogleCredentialsUtil.buildBigqueryService(credential, "BigQuery-Service-Accounts/0.1")
  }

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
  val SERVICE_KEY_LOCATION: String = models.Models.GOOGLE_SERVICE_KEY_LOCATION
  val GOOGLE_SERVICE_ACCOUNT_ID: String = models.Models.GOOGLE_SERVICE_ACCOUNT_ID
  val SERVICE_SCOPES: java.util.Collection[String] = buildScopes
  val REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob"
  lazy val clientSecrets: GoogleClientSecrets = getClientSecretsFromConfig(RESOURCE_LOCATION)
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

  def buildScopes: java.util.Collection[String] = {
     val r = (new java.util.ArrayList[String]())
     r.add("https://www.googleapis.com/auth/bigquery")
     r
  }

  /**
   * Reads file at path provided into a List of Strings
   */
  def getResourceLines(resourceLocation: String): List[String] = {
    val f = play.Play.application().getFile(resourceLocation);
    println("GoogleCredentialsUtil.getResourceLines - " + f.getAbsolutePath)
    scala.io.Source.fromFile(f).getLines.toList
  }

  /** */
  def getClientSecretsExplicit(authUri: String, clientId: String, clientSecret: String, redirectUris: List[String]): GoogleClientSecrets = {
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
  def getClientSecretsFromConfig(resourceLocation: String): GoogleClientSecrets = {
    val inputStream: java.io.InputStream = new java.io.FileInputStream(new java.io.File(resourceLocation))
    //val ras = getClass.getResourceAsStream(resourceLocation) // this doesn't work, ras is null
    GoogleClientSecrets.load(JSON_FACTORY, new java.io.InputStreamReader(inputStream))
  }

  /**
   * A new GoogleAuthorizationCodeFlow using the client secrets
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
   * With no existing code, request an authorization code from Google (provides a web page for user to get a token)
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
  def buildBigqueryService(credential: Credential): Bigquery = {
    (new Bigquery.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)).build();
  }

  /**
   * Get a Bigquery from a Credential with appname (e.g. "BigQuery-Service-Accounts/0.1")
   */
  def buildBigqueryService(credential: Credential, appName: String): Bigquery = {
    (new Bigquery.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)).setApplicationName(appName).build();
  }

  /**
   * For app to app service authentication with google, use this credential builder.
   * This uses the google provided .p12 file the location of which is at SERVICE_KEY_LOCATION from the config file
   *
   */
  def buildServiceCredential: GoogleCredential = {
    (new GoogleCredential.Builder()).setTransport(HTTP_TRANSPORT)
      .setJsonFactory(JSON_FACTORY).setServiceAccountId(GOOGLE_SERVICE_ACCOUNT_ID)
      .setServiceAccountScopes(SERVICE_SCOPES)
      .setServiceAccountPrivateKeyFromP12File(new java.io.File(SERVICE_KEY_LOCATION))
      .build()
  }

  //  GoogleCredential credential = new GoogleCredential.Builder().setTransport(TRANSPORT)
  //        .setJsonFactory(JSON_FACTORY)
  //        .setServiceAccountId("XXXXXXX@developer.gserviceaccount.com")
  //        .setServiceAccountScopes(SCOPE)
  //        .setServiceAccountPrivateKeyFromP12File(new File("my_file.p12"))
  //        .build();
  //
  //    bigquery = new Bigquery.Builder(TRANSPORT, JSON_FACTORY, credential)
  //        .setApplicationName("BigQuery-Service-Accounts/0.1")
  //        .setHttpRequestInitializer(credential).build();

}
