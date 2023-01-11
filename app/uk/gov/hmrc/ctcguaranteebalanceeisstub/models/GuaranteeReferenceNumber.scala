/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.ctcguaranteebalanceeisstub.models

import play.api.libs.json._

case class GuaranteeReferenceNumber(GRN: String) extends AnyVal {

  def hasValidFormat(): Boolean = GRN match {
    case GuaranteeReferenceNumber.grnPattern(_) => true
    case _                                      => false
  }
}

object GuaranteeReferenceNumber {
  val grnPattern = """[0-9]{2}[A-Z]{2}[A-Z0-9]{12}[0-9]([A-Z][0-9]{6})?""".r

  implicit val grnFormat = Json.format[GuaranteeReferenceNumber]

}
