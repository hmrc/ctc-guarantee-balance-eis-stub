/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.ctcguaranteebalanceeisstub.controllers.models

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.Json
import uk.gov.hmrc.ctcguaranteebalanceeisstub.controllers.Generators
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.responses.BalanceResponse

class BalanceResponseSpec extends AnyFreeSpec with Matchers with Generators {

  for (balanceResponse <- balanceResponseGenerator().sample) {
    val expectedJson = Json.obj("GRN" -> balanceResponse.guaranteeReferenceNumber, "remainingBalance" -> balanceResponse.remainingBalance.value)

    "AccessCodeResponse should serialize as expected" in {
      expectedJson.validate[BalanceResponse].get shouldBe balanceResponse
    }

    "AccessCodeResponse should deserialize as expected" in {
      Json.toJson(balanceResponse) shouldBe expectedJson
    }
  }
}
