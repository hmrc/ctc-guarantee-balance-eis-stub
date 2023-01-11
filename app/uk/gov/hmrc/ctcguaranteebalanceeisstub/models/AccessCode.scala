/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.ctcguaranteebalanceeisstub.models

import play.api.libs.json.Json

import scala.util.Random

case class AccessCode(value: String) extends AnyVal

object AccessCode {
  implicit val accessCodeFormat = Json.valueFormat[AccessCode]

  def generateAccessCode(): AccessCode = AccessCode(Random.alphanumeric.take(10).mkString)
}
