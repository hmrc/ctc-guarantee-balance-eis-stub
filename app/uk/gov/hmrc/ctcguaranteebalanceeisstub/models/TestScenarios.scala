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
import play.api.mvc.Results._
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.responses.AccessCodeResponse
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.responses.Balance
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.responses.BalanceResponse
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.responses.RequestErrorResponse

object TestScenarios {

  private def guaranteeNotFoundResult(grn: GuaranteeReferenceNumber): Result =
    NotFound(Json.toJson(RequestErrorResponse.invalidGrnError(grn)))

  val invalidAccessCodeResult: Result =
    Forbidden(Json.toJson(RequestErrorResponse.invalidAccessCode))

  private val invalidGuaranteeTypeResult: Result =
    Forbidden(Json.toJson(RequestErrorResponse.invalidTypeError))

  private val simpleGuaranteeTestDataList: List[TestData] = List(
    // GB data
    TestData(0, GuaranteeReferenceNumber("23GB0000010000854"), BigDecimal("262626"), "GBP", "AC01"),
    TestData(1, GuaranteeReferenceNumber("23GB0000010000863"), BigDecimal("10000"), "GBP", "AC01"),
    TestData(9, GuaranteeReferenceNumber("23GB0000010000872"), BigDecimal("90000"), "GBP", "AC01"),
    // XI data
    TestData(0, GuaranteeReferenceNumber("23XI0000010000655"), BigDecimal("30000"), "GBP", "AC01"),
    TestData(1, GuaranteeReferenceNumber("23XI0000010000664"), BigDecimal("50000"), "GBP", "AC01")
  )

  private val invalidBalanceCheckTypes: Set[Int] = Set(0, 2, 4, 9)

  def getBalanceResponse(grn: GuaranteeReferenceNumber): Either[Result, BalanceResponse] =
    simpleGuaranteeTestDataList.find(_.grn == grn) match {
      case None =>
        Left(guaranteeNotFoundResult(grn))
      case Some(data) if invalidBalanceCheckTypes.contains(data.guaranteeType) =>
        Left(invalidGuaranteeTypeResult)
      case Some(data) =>
        Right(BalanceResponse(data.grn, Balance(data.amount.doubleValue), data.currency))
    }

  def getAccessCodeValidationScenarios(grn: GuaranteeReferenceNumber, providedCode: AccessCode): Result =
    simpleGuaranteeTestDataList.find(_.grn == grn) match {
      case None =>
        guaranteeNotFoundResult(grn)
      case Some(data) =>
        val expectedCode = AccessCode(data.accessCode)
        if (providedCode == expectedCode) {
          Ok(Json.toJson(AccessCodeResponse(data.grn, providedCode)))
        } else {
          invalidAccessCodeResult
        }
    }

  def handleUnknownTraderTestGrn(grn: GuaranteeReferenceNumber): Result =
    guaranteeNotFoundResult(grn)

}
