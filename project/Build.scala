import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {  
  val appName         = "LUCIDPresentation"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    "nebula" % "nebula_2.9.2" % "0.1-SNAPSHOT"
  )

//      "Local Ivy2 Repository" at "file://" + Path.userHome.absolutePath + "/.ivy2/local"
  def extraResolvers = Seq(
    resolvers ++= Seq(
      "NativeLibs4Java Respository" at "http://nativelibs4java.sourceforge.net/maven/",
      "Sonatype OSS Snapshots Repository" at "http://oss.sonatype.org/content/groups/public",
      "Sonatype OSS Releases" at "http://oss.sonatype.org/content/repositories/releases/",
      "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
      "repo.codahale.com" at "http://repo.codahale.com",
      "maven.twttr.com" at "http://maven.twttr.com",
      "typesafe-releases" at "http://repo.typesafe.com/typesafe/repo"
    )
  )

  def scalaSettings = Seq(
    scalacOptions ++= Seq(
      "-optimize",
      "-unchecked",
      "-deprecation"
    )
  )

//  def appSettings = extraResolvers ++ scalaSettings

  val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
    extraResolvers: _*
  ).settings(
    scalaSettings: _*
  )
}
