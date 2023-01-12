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
import play.api.http.Status
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.FakeHeaders
import play.api.test.FakeRequest
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.AccessCode
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.GuaranteeReferenceNumber
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.requests.GuaranteeReferenceNumberRequest
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.responses.AccessCodeResponse
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.responses.Balance
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.responses.BalanceResponse

class GuaranteeControllerSpec extends AnyFreeSpec with GuiceOneAppPerSuite with Matchers with Generators {

  def fakeRequest[A](body: A, url: String) = FakeRequest(
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

  "POST /guarantee/accessCode" - {
    "when the GRN format is valid, should return 200 with GRN and accessCode" in {
      val validGrnRequest = guaranteeReferenceNumberRequest

      val request = fakeRequest(Json.toJson(validGrnRequest).toString(), routes.GuaranteeController.getAccessCode().url)

      val result = route(app, request).get

      status(result) shouldBe Status.OK
      contentAsJson(result).validate[AccessCodeResponse].map {
        response =>
          response.guaranteeReferenceNumber.value shouldBe validGrnRequest.GRN.value
          response.accessCode.value shouldBe AccessCode.constantAccessCodeValue.value
      }
    }

    "when the GRN format is invalid, should return 500" in {
      val invalidGRN = GuaranteeReferenceNumber(Gen.stringOfN(10, Gen.alphaNumChar).sample.get)
      val request    = fakeRequest(Json.toJson(GuaranteeReferenceNumberRequest(invalidGRN)).toString(), routes.GuaranteeController.getAccessCode().url)
      val result     = route(app, request).get

      status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      contentAsJson(result) shouldBe Json.toJson(s"The guarantee reference number [${invalidGRN.value}] is not in the correct format.")
    }

    "when GRN is not present in the body, should return 400" in {
      val invalidBody = Json.obj("test" -> "invalid_body")
      val request     = fakeRequest(invalidBody, routes.GuaranteeController.getAccessCode().url)
      val result      = route(app, request).get

      status(result) shouldBe Status.BAD_REQUEST
      contentAsJson(result) shouldBe Json.toJson("Expected GRN in the body.")
    }
  }

  "POST /guarantee/balance" - {
    "when the GRN format is valid, should return 200 with GRN and remainingBalance" in {
      val validGrnRequest = guaranteeReferenceNumberRequest

      val request = fakeRequest(Json.toJson(validGrnRequest).toString(), routes.GuaranteeController.getBalance().url)

      val result = route(app, request).get

      status(result) shouldBe Status.OK
      contentAsJson(result).validate[BalanceResponse].map {
        response =>
          response.guaranteeReferenceNumber.value shouldBe validGrnRequest.GRN.value
          response.remainingBalance shouldBe Balance.constantBalanceValue
      }
    }

    "when the GRN format is invalid, should return 500" in {
      val invalidGRN = GuaranteeReferenceNumber(Gen.stringOfN(10, Gen.alphaNumChar).sample.get)
      val request    = fakeRequest(Json.toJson(GuaranteeReferenceNumberRequest(invalidGRN)).toString(), routes.GuaranteeController.getBalance().url)
      val result     = route(app, request).get

      status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      contentAsJson(result) shouldBe Json.toJson(s"The guarantee reference number [${invalidGRN.value}] is not in the correct format.")
    }

    "when GRN is not present in the body, should return 400" in {
      val invalidBody = Json.obj("test" -> "invalid_body")
      val request     = fakeRequest(invalidBody, routes.GuaranteeController.getBalance().url)
      val result      = route(app, request).get

      status(result) shouldBe Status.BAD_REQUEST
      contentAsJson(result) shouldBe Json.toJson("Expected GRN in the body.")
    }
  }
}
