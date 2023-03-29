import sbt.Defaults.itSettings
import scoverage.ScoverageKeys
import uk.gov.hmrc.DefaultBuildSettings.integrationTestSettings
import play.sbt.routes.RoutesKeys

lazy val microservice = Project("ctc-guarantee-balance-eis-stub", file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(inConfig(IntegrationTest)(itSettings))
  .settings(inThisBuild(buildSettings))
  .settings(
    majorVersion := 0,
    scalaVersion := "2.13.8",
    PlayKeys.playDefaultPort := 9518,
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    // https://www.scala-lang.org/2021/01/12/configuring-and-suppressing-warnings.html
    // suppress warnings in generated routes files
    scalacOptions += "-Wconf:src=routes/.*:s",
    // Import models by default in route files
    RoutesKeys.routesImport ++= Seq(
      "models.GuaranteeReferenceNumber"
    )
  )
  .settings(scalacSettings)
  .settings(scoverageSettings)
  .configs(IntegrationTest)
  .settings(integrationTestSettings(): _*)
  .settings(resolvers += Resolver.jcenterRepo)
  .settings(CodeCoverageSettings.settings: _*)

// Settings for the whole build
lazy val buildSettings = Def.settings(
  scalafmtOnCompile := true,
  useSuperShell := false
)

lazy val scalacSettings = Def.settings(
  // Disable warnings arising from generated routing code
  scalacOptions += "-Wconf:src=routes/.*:silent",
  // Disable fatal warnings and warnings from discarding values
  scalacOptions ~= {
    opts =>
      opts.filterNot(Set("-Xfatal-warnings", "-Ywarn-value-discard"))
  }
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
