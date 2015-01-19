import sbt.Keys._
import sbt._

import scala.util.Properties._

object ApplicationBuild extends Build {

  val appName = "surfersTV"
  val appVersion = envOrElse("TV_API_VIEW_VERSION", "999-SNAPSHOT")

  def integrationFilter(name: String): Boolean = name contains "Int"
  def acceptanceFilter(name: String): Boolean = name contains "FeaturesRunner"
  def unitFilter(name: String): Boolean =
    !integrationFilter(name) && !acceptanceFilter(name)

  lazy val IntTest = config("integration") extend (Test)
  lazy val AccTest = config("acceptance") extend (Test)

  val appDependencies = Seq(
    "org.reactivemongo" %% "reactivemongo" % "0.10.0"
  )

  val buildSettings = Defaults.defaultSettings

  val main = play.Project(appName, appVersion, appDependencies)
    .settings(scalaSource in Test <<= baseDirectory(_ / "test/unit"),
      testOptions in Test := Seq(Tests.Filter(unitFilter)))

    .configs(IntTest)
    .settings(inConfig(IntTest)(Defaults.testSettings): _*)
    .settings(testOptions in IntTest := Seq(Tests.Filter(integrationFilter)))
    .settings(scalaSource in IntTest <<= baseDirectory(_ / "test/integration"))
    .settings(libraryDependencies ++= Seq("org.scalatestplus" % "play_2.10" % "1.0.0" % "test"))
    .settings(Keys.fork in (Test) := false)

    .configs(AccTest)
    .settings(testOptions in AccTest := Seq(Tests.Filter(acceptanceFilter)))
    .settings(inConfig(AccTest)(Defaults.testSettings): _*)
    .settings(scalaSource in AccTest <<= baseDirectory(_ / "test/acceptance"))
    .settings(libraryDependencies ++= Seq("org.scalatestplus" % "play_2.10" % "1.0.0" % "test",
    "com.github.tomakehurst" % "wiremock" % "1.53" % "test",
    "info.cukes" %% "cucumber-scala" % "1.1.8" % "test",
    "info.cukes" % "cucumber-junit" % "1.1.8" % "test",
    "junit" % "junit" % "4.11" % "test"))

  //    .settings(CucumberPlugin.cucumberSettings: _*)
  //    .settings(
  //      CucumberPlugin.cucumberFeaturesLocation := "cucumber",
  //      CucumberPlugin.cucumberStepsBasePackage := "cucumber.steps"
  //    )


}