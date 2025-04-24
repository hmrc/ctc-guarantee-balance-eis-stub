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
import play.api.Application
import play.api.http.Status
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers.*
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

  override lazy val app = GuiceApplicationBuilder()
    .configure("metrics.enabled" -> false)
    .build()

  lazy val appFlagOff: Application = GuiceApplicationBuilder()
    .configure(
      "features.TestScenarios.enabled" -> false,
      "metrics.enabled"                -> false
    )
    .build()

  lazy val appFlagOn: Application = GuiceApplicationBuilder()
    .configure(
      "features.TestScenarios.enabled" -> true,
      "metrics.enabled"                -> false
    )
    .build()

  "validateAccessCode endpoint" - {

    "when trader test behaviour is OFF" - {
      val originalValidAccessCodeReq = AccessCodeRequest(AccessCode.constantAccessCodeValue)

      "when the GRN format is valid (original logic), should return 200 with GRN and constant accessCode" in forAll(guaranteeReferenceNumberGenerator()) {
        grn =>
          assume(!grn.value.startsWith("1") && !grn.value.startsWith("02") && !grn.value.startsWith("04"))
          val request = fakeAccessCodeRequest(Json.toJson(originalValidAccessCodeReq), routes.GuaranteeController.validateAccessCode(grn).url)
          val result  = route(appFlagOff, request).get

          status(result) shouldBe Status.OK
          contentAsJson(result)
            .validate[AccessCodeResponse]
            .map {
              response =>
                response.grn.value shouldBe grn.value
                response.masterAccessCode shouldBe AccessCode.constantAccessCodeValue
            }
            .get
      }

      "when the GRN starts with original logic, should return 403 with error message" in forAll(invalidGrnGenerator) {
        grn =>
          val request = fakeAccessCodeRequest(Json.toJson(originalValidAccessCodeReq), routes.GuaranteeController.validateAccessCode(grn).url)
          val result  = route(appFlagOff, request).get

          status(result) shouldBe Status.FORBIDDEN
          contentAsJson(result)
            .validate[RequestErrorResponse]
            .map {
              errorResponse =>
                errorResponse.message shouldBe s"Guarantee not found for GRN: ${grn.value}"
            }
            .get
      }
    }

    "when trader test behaviour is ON" - {
      val originalValidAccessCodeReq = AccessCodeRequest(AccessCode("AC01"))

      "when the GRN format is valid, should return 200 with GRN and constant accessCode" in {
        val grn     = GuaranteeReferenceNumber("23GB0000010000854")
        val request = fakeAccessCodeRequest(Json.toJson(originalValidAccessCodeReq), routes.GuaranteeController.validateAccessCode(grn).url)
        val result  = route(appFlagOn, request).get

        status(result) shouldBe Status.OK
        contentAsJson(result)
          .validate[AccessCodeResponse]
          .map {
            response =>
              response.grn.value shouldBe grn.value
              response.masterAccessCode shouldBe AccessCode("AC01")
          }
          .get
      }

      "when the GRN format is valid, should return 400" in {
        val grn     = GuaranteeReferenceNumber("23GB0000010000854")
        val request = fakeAccessCodeRequest(Json.toJson("Invalid"), routes.GuaranteeController.validateAccessCode(grn).url)
        val result  = route(appFlagOn, request).get

        status(result) shouldBe Status.BAD_REQUEST

        contentAsJson(result)
          .validate[RequestErrorResponse]
          .map {
            response =>
              response.message shouldBe "Invalid request payload"
              response.path shouldBe "/ctc-guarantee-balance-eis-stub/guarantees/23GB0000010000854/access-codes"
          }
      }
    }
  }
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

        status(result) shouldBe Status.BAD_REQUEST
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

  "getBalance endpoint" - {

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

  "when TestScenario is ON" - {

    val configuredApp = GuiceApplicationBuilder()
      .configure("features.TestScenarios.enabled" -> true, "metrics.enabled" -> false)
      .build()

    val gbType1Grn = GuaranteeReferenceNumber("23GB0000010000863")
    val xiType1Grn = GuaranteeReferenceNumber("23XI0000010000664")
    val unknownGrn = GuaranteeReferenceNumber("99XX987654321098B2")

    "should return 200 OK with correct balance for GB Type 1 GRN" in {
      val request = fakeBalanceRequest(gbType1Grn)
      val result  = route(configuredApp, request).get

      status(result) shouldBe Status.OK
      contentAsJson(result)
        .validate[BalanceResponse]
        .map {
          response =>
            response.grn shouldBe gbType1Grn
            response.balance.value shouldBe BigDecimal("10000").doubleValue
            response.currencyCL shouldBe "GBP"
        }
        .get
    }

    "should return 200 OK with correct balance for XI Type 1 GRN" in {
      val request = fakeBalanceRequest(xiType1Grn)
      val result  = route(configuredApp, request).get

      status(result) shouldBe Status.OK
      contentAsJson(result)
        .validate[BalanceResponse]
        .map {
          response =>
            response.grn shouldBe xiType1Grn
            response.balance.value shouldBe BigDecimal("50000").doubleValue
            response.currencyCL shouldBe "GBP"
        }
        .get
    }

    "should return 403 Not Found for a GRN not present in the test data" in {
      val request = fakeBalanceRequest(unknownGrn)
      val result  = route(configuredApp, request).get

      status(result) shouldBe Status.FORBIDDEN
      contentAsJson(result)
        .validate[RequestErrorResponse]
        .map {
          errorResponse =>
            errorResponse.message shouldBe s"Guarantee not found for GRN: ${unknownGrn.value}"
        }
        .get
    }
  }
}
