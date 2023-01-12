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
    "uk.gov.hmrc"    %% "bootstrap-test-play-28" % bootstrapVersion,
    "org.scalacheck" %% "scalacheck"             % "1.17.0"
  ).map(_ % "test, it")
}
