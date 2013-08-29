import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "ImAlive"
  val appVersion      = "0.1-SNAPSHOT"

  // https://github.com/GoogleCloudPlatform/bigquery-getting-started-java/blob/master/src/pom.xml
    
  val appDependencies = Seq(
    "com.typesafe.akka" %% "akka-actor" % "2.2.0",
    "com.typesafe.akka" %% "akka-slf4j" % "2.2.0",
    "org.webjars" %% "webjars-play" % "2.1.0-3",
    "org.webjars" % "bootstrap" % "2.3.1",
    "org.webjars" % "flot" % "0.8.0",
    "org.webjars" % "angularjs" % "1.1.5-1",
    "com.typesafe.akka" %% "akka-testkit" % "2.2.0" % "test",
    "com.google.oauth-client" % "google-oauth-client" % "1.16.0-rc", // this also pulls google-http-client-1.16.0-rc
    "com.google.api-client" % "google-api-client" % "1.16.0-rc",
    "com.google.api-client" % "google-api-client-jackson2" % "1.16.0-rc",
    //"com.google.apis" % "google-api-client-appengine" % "1.16.0-rc",
    "com.google.apis" % "google-api-services-bigquery" % "v2-rev103-1.16.0-rc",
    "javax" % "javaee-api" % "6.0-RC2"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    scalaVersion := "2.10.2"
  )

}
