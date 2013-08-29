// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository 
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Maven Central Server" at "http://repo1.maven.org/maven2"

// resolvers += "Google" at "https://google-api-client-libraries.appspot.com/mavenrepo/"

// Use the Play sbt plugin for Play projects
addSbtPlugin("play" % "sbt-plugin" % "2.1.3")
