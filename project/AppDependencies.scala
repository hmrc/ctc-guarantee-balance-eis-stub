import play.core.PlayVersion
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  private val bootstrapVersion = "7.12.0"

  val compile = Seq(
    "uk.gov.hmrc" %% "bootstrap-backend-play-28" % bootstrapVersion
  )

  val test = Seq(
    "uk.gov.hmrc"         %% "bootstrap-test-play-28" % bootstrapVersion,
    "org.scalacheck"      %% "scalacheck"             % "1.17.0",
    "org.scalatestplus"   %% "scalacheck-1-17"        % "3.2.17.0",
    "com.vladsch.flexmark" % "flexmark-all"            % "0.62.2"
  ).map(_ % "test, it")
}
