/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.ctcguaranteebalanceeisstub.controllers.models

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.Json
import uk.gov.hmrc.ctcguaranteebalanceeisstub.controllers.Generators
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.responses.AccessCodeResponse

class AccessCodeResponseSpec extends AnyFreeSpec with Matchers with Generators {

  for (accessCodeResponse <- accessCodeResponseGenerator().sample) {
    val expectedJson = Json.obj("GRN" -> accessCodeResponse.guaranteeReferenceNumber, "accessCode" -> accessCodeResponse.accessCode.value)

    "AccessCodeResponse should serialize as expected" in {
      expectedJson.validate[AccessCodeResponse].get shouldBe accessCodeResponse
    }

    "AccessCodeResponse should deserialize as expected" in {
      Json
        .toJson(accessCodeResponse) shouldBe expectedJson
    }
  }

}
