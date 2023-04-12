/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.ctcguaranteebalanceeisstub.models.responses

import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.functional.syntax.unlift
import play.api.libs.json.JsPath
import play.api.libs.json.OWrites
import play.api.libs.json.Reads
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.GuaranteeReferenceNumber

case class RequestErrorResponse(message: String, timestamp: String, path: String)

object RequestErrorResponse {

  implicit val reads: Reads[RequestErrorResponse] =
    ((JsPath \ "message").read[String] and
      (JsPath \ "timestamp").read[String] and
      (JsPath \ "path").read[String])(RequestErrorResponse.apply _)

  implicit val writes: OWrites[RequestErrorResponse] =
    ((JsPath \ "message").write[String] and
      (JsPath \ "timestamp").write[String] and
      (JsPath \ "path").write[String])(unlift(RequestErrorResponse.unapply))

  def invalidAccessCode = RequestErrorResponse("Not Valid Access Code for this operation", "2023-01-24T11:57:36.0537863801", "...")

  def invalidGrnError(grn: GuaranteeReferenceNumber) =
    RequestErrorResponse(s"Guarantee not found for GRN: ${grn.value}", "2023-01-24T11:57:36.0537863801", "...")
}
