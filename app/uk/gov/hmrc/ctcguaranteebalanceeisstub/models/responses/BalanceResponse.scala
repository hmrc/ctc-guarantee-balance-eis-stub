/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.ctcguaranteebalanceeisstub.models.responses

import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.Format
import play.api.libs.json.Json
import play.api.libs.json.__
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.AccessCode
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.GuaranteeReferenceNumber

import scala.util.Random

case class BalanceResponse(guaranteeReferenceNumber: GuaranteeReferenceNumber, remainingBalance: Balance)

case class Balance(value: Int) extends AnyVal

object Balance {
  implicit val balanceFormat = Json.valueFormat[Balance]

  def generateBalance(): Balance = Balance(Random.nextInt(10000))
}

object BalanceResponse {

  implicit val format: Format[BalanceResponse] =
    ((__ \ "GRN").format[GuaranteeReferenceNumber] and (__ \ "remainingBalance")
      .format[Balance])(BalanceResponse.apply(_, _), response => (response.guaranteeReferenceNumber, response.remainingBalance))
}
