import scoverage.ScoverageKeys
import play.sbt.routes.RoutesKeys

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "2.13.12"

lazy val microservice = Project("ctc-guarantee-balance-eis-stub", file("."))
  .enablePlugins(PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(inThisBuild(buildSettings))
  .settings(
    PlayKeys.playDefaultPort := 9518,
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    // https://www.scala-lang.org/2021/01/12/configuring-and-suppressing-warnings.html
    // suppress warnings in generated routes files
    scalacOptions += "-Wconf:src=routes/.*:s",
    // Import models by default in route files
    RoutesKeys.routesImport ++= Seq(
      "uk.gov.hmrc.ctcguaranteebalanceeisstub.models._"
    )
  )
  .settings(scalacSettings)
  .settings(scoverageSettings)
  .settings(resolvers += Resolver.jcenterRepo)
  .settings(CodeCoverageSettings.settings: _*)

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test") // the "test->test" allows reusing test code and test dependencies
  .settings(libraryDependencies ++= AppDependencies.test)

// Settings for the whole build
lazy val buildSettings = Def.settings(
  scalafmtOnCompile := true,
  useSuperShell := false
)

// Scalac options
lazy val scalacSettings = Def.settings(
  // Disable fatal warnings and warnings from discarding values
  scalacOptions ~= {
    opts =>
      opts.filterNot(Set("-Xfatal-warnings", "-Ywarn-value-discard"))
  },
  // Disable dead code warning as it is triggered by Mockito any()
  Test / scalacOptions ~= {
    opts =>
      opts.filterNot(Set("-Ywarn-dead-code"))
  },
  // Disable warnings arising from generated routing code
  scalacOptions += "-Wconf:src=routes/.*:s"
)

lazy val scoverageSettings = Def.settings(
  Test / parallelExecution := false,
  ScoverageKeys.coverageMinimumStmtTotal := 90,
  ScoverageKeys.coverageFailOnMinimum := true,
  ScoverageKeys.coverageHighlighting := true,
  ScoverageKeys.coverageExcludedPackages := Seq(
    "<empty>",
    "Reverse.*",
    ".*(config|views.*)",
    ".*(BuildInfo|Routes).*"
  ).mkString(";"),
  ScoverageKeys.coverageExcludedFiles := Seq(
    "<empty>",
    "Reverse.*",
    ".*BuildInfo.*",
    ".*javascript.*",
    ".*Routes.*",
    ".*GuiceInjector"
  ).mkString(";")
)
