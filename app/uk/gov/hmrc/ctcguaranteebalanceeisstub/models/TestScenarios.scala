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

package uk.gov.hmrc.ctcguaranteebalanceeisstub.models

import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.mvc.Results.*
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.responses.AccessCodeResponse
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.responses.Balance
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.responses.BalanceResponse
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.responses.RequestErrorResponse

object TestScenarios {

  private val guaranteeData: List[TestData] = List(
    TestData(0, GuaranteeReferenceNumber("23GB0000010000854"), BigDecimal("262626"), "GBP", "AC01"),
    TestData(1, GuaranteeReferenceNumber("23GB0000010000863"), BigDecimal("10000"), "GBP", "AC01"),
    TestData(9, GuaranteeReferenceNumber("23GB0000010000872"), BigDecimal("90000"), "GBP", "AC01"),
    TestData(0, GuaranteeReferenceNumber("23XI0000010000655"), BigDecimal("30000"), "GBP", "AC01"),
    TestData(1, GuaranteeReferenceNumber("23XI0000010000664"), BigDecimal("50000"), "GBP", "AC01")
  )

  def getBalanceResponse(grn: GuaranteeReferenceNumber): Result =
    guaranteeData.find(_.grn == grn) match {
      case None =>
        guaranteeNotFoundResult(grn)
      case Some(data) =>
        Ok(Json.toJson(BalanceResponse(data.grn, Balance(data.amount.doubleValue), data.currency)))
    }

  def getAccessCodeValidationScenarios(grn: GuaranteeReferenceNumber, accessCode: AccessCode): Result =
    guaranteeData.find(_.grn == grn) match {
      case None =>
        guaranteeNotFoundResult(grn)
      case Some(data) if accessCode.value == data.accessCode => Ok(Json.toJson(AccessCodeResponse(data.grn, accessCode)))
      case _                                                 => Forbidden(Json.toJson(RequestErrorResponse.invalidAccessCode))
    }

  private def guaranteeNotFoundResult(grn: GuaranteeReferenceNumber): Result =
    NotFound(Json.toJson(RequestErrorResponse.invalidGrnError(grn)))
}
