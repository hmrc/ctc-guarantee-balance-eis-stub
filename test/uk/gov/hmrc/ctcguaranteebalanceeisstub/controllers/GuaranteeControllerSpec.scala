/*
 * Copyright 2023 HM Revenue & Customs
 *
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
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.GuaranteeReferenceNumber
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.responses.AccessCodeResponse
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
      val validGrn = guaranteeReferenceNumberGenerator().sample.get

      val request = fakeRequest(Json.toJson(validGrn).toString(), routes.GuaranteeController.getAccessCode().url)

      val result = route(app, request).get

      status(result) shouldBe Status.OK
      contentAsJson(result).validate[AccessCodeResponse].map {
        response =>
          response.guaranteeReferenceNumber.GRN shouldBe validGrn.GRN
          response.accessCode.value.length shouldBe 10
      }
    }

    "when the GRN format is invalid, should return 500" in {
      val invalidGRN = Gen.stringOfN(10, Gen.alphaNumChar).sample.get
      val request    = fakeRequest(Json.toJson(GuaranteeReferenceNumber(invalidGRN)).toString(), routes.GuaranteeController.getAccessCode().url)
      val result     = route(app, request).get

      status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      contentAsJson(result) shouldBe Json.toJson(s"The guarantee reference number [$invalidGRN] is not in the correct format.")
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
      val validGrn = guaranteeReferenceNumberGenerator().sample.get

      val request = fakeRequest(Json.toJson(validGrn).toString(), routes.GuaranteeController.getBalance().url)

      val result = route(app, request).get

      status(result) shouldBe Status.OK
      contentAsJson(result).validate[BalanceResponse].map {
        response =>
          response.guaranteeReferenceNumber.GRN shouldBe validGrn.GRN
          response.remainingBalance.value >= 0 shouldBe true
      }
    }

    "when the GRN format is invalid, should return 500" in {
      val invalidGRN = Gen.stringOfN(10, Gen.alphaNumChar).sample.get
      val request    = fakeRequest(Json.toJson(GuaranteeReferenceNumber(invalidGRN)).toString(), routes.GuaranteeController.getBalance().url)
      val result     = route(app, request).get

      status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      contentAsJson(result) shouldBe Json.toJson(s"The guarantee reference number [$invalidGRN] is not in the correct format.")
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
