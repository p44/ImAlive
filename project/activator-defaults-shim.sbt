
// Note: This file is autogenerated by Builder.  Please do not modify!
// Full resolvers can be removed in sbt 0.13
fullResolvers <<= (fullResolvers, bootResolvers, appConfiguration) map {
  case (rs, Some(b), app) =>
    def getResolvers(app: xsbti.AppConfiguration): Option[Seq[xsbti.Repository]] =
      try Some(app.provider.scalaProvider.launcher.ivyRepositories.toSeq)
      catch { case _: NoSuchMethodError => None }
    def findLocalResolverNames(resolvers: Seq[xsbti.Repository]): Seq[String] =
      for {
        r <- resolvers
        if r.isInstanceOf[xsbti.IvyRepository]
        ivy = r.asInstanceOf[xsbti.IvyRepository]
        if ivy.url.getProtocol == "file"
      } yield ivy.id
    val newResolvers: Seq[Resolver] =
      getResolvers(app).map(findLocalResolverNames).getOrElse(Nil).flatMap(name => b.find(_.name == name))
    newResolvers ++ rs
  case (rs, _, _) => rs
}


// shim plugins are needed when plugins are not "UI aware"
// (we need an interface for the UI program rather than an interface
// for a person at a command line).
// In future plans, we want plugins to have a built-in ability to be
// remote-controlled by a UI and then we would drop the shims.
addSbtPlugin("com.typesafe.sbtrc" % "sbt-rc-defaults-0-12" % "0.2.0")
