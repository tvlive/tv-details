import sbt._
import Keys._
import scala.util.Properties._

object ApplicationBuild extends Build {

  val appName = "surfersTV"
  val appVersion = envOrElse("TV_API_VIEW_VERSION", "999-SNAPSHOT")

  def unitFilter(name: String): Boolean = !(integrationFilter(name) && acceptanceFilter(name))
  def integrationFilter(name: String): Boolean = name contains "Int"
  def acceptanceFilter(name: String): Boolean = name contains "Feature"

  lazy val IntTest = config("integration") extend(Test)
  lazy val AccTest = config("acceptance") extend(Test)

  val appDependencies = Seq(
    "org.reactivemongo" %% "reactivemongo" % "0.10.0"
  )

  val buildSettings = Defaults.defaultSettings

  val main = play.Project(appName, appVersion, appDependencies)
    .configs( IntTest )
    .settings(inConfig(IntTest)(Defaults.testSettings) : _*)
    .settings(scalaSource in Test <<= baseDirectory(_ / "test/unit"),
      testOptions in Test := Seq(Tests.Filter(unitFilter)),
      testOptions in IntTest := Seq(Tests.Filter(integrationFilter)),
      testOptions in AccTest := Seq(Tests.Filter(acceptanceFilter)))
    .settings(scalaSource in AccTest <<= baseDirectory(_ / "test/acceptance"))
    .settings(scalaSource in IntTest <<= baseDirectory(_ / "test/integration"))
    .settings(libraryDependencies ++= Seq("org.scalatestplus" % "play_2.10" % "1.0.0" % "test"))
    .settings(Keys.fork in (Test) := false)


}