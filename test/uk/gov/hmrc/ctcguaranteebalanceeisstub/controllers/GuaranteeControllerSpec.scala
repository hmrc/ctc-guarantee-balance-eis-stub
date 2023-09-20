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

package uk.gov.hmrc.ctcguaranteebalanceeisstub.controllers

import org.scalacheck.Gen
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import play.api.http.Status
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.FakeHeaders
import play.api.test.FakeRequest
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.AccessCode
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.GuaranteeReferenceNumber
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.requests.AccessCodeRequest
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.responses.AccessCodeResponse
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.responses.BalanceResponse
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.responses.RequestErrorResponse
import play.api.mvc.AnyContentAsEmpty

class GuaranteeControllerSpec extends AnyFreeSpec with GuiceOneAppPerSuite with Matchers with Generators with ScalaCheckDrivenPropertyChecks {

  def fakeAccessCodeRequest[A](body: A, url: String) = FakeRequest(
    method = "POST",
    uri = url,
    headers = FakeHeaders(
      Seq(
        "content-type" -> "application/json"
      )
    ),
    body = body
  )

  override lazy val app = GuiceApplicationBuilder().build()

  "POST /guarantees/:grn/access-codes" - {
    val validAccessCodeRequest: AccessCodeRequest = AccessCodeRequest(AccessCode.constantAccessCodeValue)

    "when the GRN format is valid, should return 200 with GRN and accessCode" in forAll(guaranteeReferenceNumberGenerator()) {
      grn =>
        val request = fakeAccessCodeRequest(Json.toJson(validAccessCodeRequest), routes.GuaranteeController.validateAccessCode(grn).url)

        val result = route(app, request).get

        status(result) shouldBe Status.OK
        contentAsJson(result).validate[AccessCodeResponse].map {
          response =>
            response.grn.value shouldBe grn.value
            response.masterAccessCode shouldBe AccessCode.constantAccessCodeValue
        }
    }

    "when the GRN is invalid, should return 403 with appropriate error message" in forAll(invalidGrnGenerator) {
      grn =>
        val request = fakeAccessCodeRequest(Json.toJson(validAccessCodeRequest), routes.GuaranteeController.validateAccessCode(grn).url)
        val result  = route(app, request).get

        status(result) shouldBe Status.FORBIDDEN
        contentAsJson(result).validate[RequestErrorResponse].map {
          errorResponse =>
            errorResponse.message shouldBe s"Guarantee not found for GRN: ${grn.value}"
            errorResponse.path shouldBe "..."
        }
    }

    "when the guarantee type is invalid, should return 403 with appropriate error message" in forAll(invalidGrnTypeGenerator) {
      grn =>
        val request = fakeAccessCodeRequest(Json.toJson(validAccessCodeRequest), routes.GuaranteeController.validateAccessCode(grn).url)
        val result  = route(app, request).get

        status(result) shouldBe Status.FORBIDDEN
        contentAsJson(result).validate[RequestErrorResponse].map {
          errorResponse =>
            errorResponse.message shouldBe s"Not Valid Guarantee Type for this operation"
            errorResponse.path shouldBe "..."
        }
    }

    "when the GRN is invalid, should return 400 " in forAll(Gen.stringOfN(10, Gen.alphaNumChar).map(GuaranteeReferenceNumber.apply)) {
      grn =>
        val request = fakeAccessCodeRequest(Json.toJson("invalid request body"), routes.GuaranteeController.validateAccessCode(grn).url)
        val result  = route(app, request).get

        status(result) shouldBe Status.FORBIDDEN
    }

    "when the access code is invalid, should return 403 with appropriate error message" in forAll(guaranteeReferenceNumberGenerator(), invalidAccessCode) {
      (grn, accessCode) =>
        val invalidAccessCode: AccessCodeRequest = AccessCodeRequest(accessCode)
        val request                              = fakeAccessCodeRequest(Json.toJson(invalidAccessCode), routes.GuaranteeController.validateAccessCode(grn).url)
        val result                               = route(app, request).get

        status(result) shouldBe Status.FORBIDDEN
        contentAsJson(result).validate[RequestErrorResponse].map {
          errorResponse =>
            errorResponse.message shouldBe "Not Valid Access Code for this operation"
            errorResponse.path shouldBe "..."
        }
    }
  }

  def fakeBalanceRequest(grn: GuaranteeReferenceNumber) = FakeRequest(
    method = "GET",
    uri = routes.GuaranteeController.getBalance(grn).url,
    headers = FakeHeaders(
      Seq(
        "Accept" -> "application/json"
      )
    ),
    body = AnyContentAsEmpty
  )

  "GET /guarantees/:grn/balance" - {
    "when the GRN format is valid, should return 200 with GRN and remainingBalance" in forAll(guaranteeReferenceNumberGenerator()) {
      grn =>
        val request = fakeBalanceRequest(grn)
        val result  = route(app, request).get

        status(result) shouldBe Status.OK
        contentAsJson(result).validate[BalanceResponse].map {
          response =>
            response.grn.value shouldBe grn.value
            response.currencyCL shouldBe "GBP"
        }
    }

    "when the GRN format is invalid, should return 500" in forAll(invalidGrnGenerator) {
      grn =>
        val request = fakeBalanceRequest(grn)
        val result  = route(app, request).get

        status(result) shouldBe Status.FORBIDDEN
    }

  }
}
