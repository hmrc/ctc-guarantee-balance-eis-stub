/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.ctcguaranteebalanceeisstub.models.responses

import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.Format
import play.api.libs.json.__
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.GuaranteeReferenceNumber
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.AccessCode

case class AccessCodeResponse(guaranteeReferenceNumber: GuaranteeReferenceNumber, accessCode: AccessCode)

object AccessCodeResponse {

  implicit val format: Format[AccessCodeResponse] =
    ((__ \ "GRN").format[GuaranteeReferenceNumber] and (__ \ "accessCode")
      .format[AccessCode])(AccessCodeResponse.apply(_, _), response => (response.guaranteeReferenceNumber, response.accessCode))
}
