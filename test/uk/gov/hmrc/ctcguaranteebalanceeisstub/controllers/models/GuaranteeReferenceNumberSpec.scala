/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.ctcguaranteebalanceeisstub.controllers.models

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.Json
import uk.gov.hmrc.ctcguaranteebalanceeisstub.controllers.Generators
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.GuaranteeReferenceNumber

class GuaranteeReferenceNumberSpec extends AnyFreeSpec with Matchers with Generators {

  for (grn <- guaranteeReferenceNumberGenerator().sample) {

    val expectedJson = Json.obj("GRN" -> grn.GRN)

    "GuaranteeReferenceNumber should serialize as expected" in {
      expectedJson.validate[GuaranteeReferenceNumber].get shouldBe grn
    }

    "GuaranteeReferenceNumber should deserialize as expected" in {
      Json.toJson(grn) shouldBe expectedJson
    }
  }

}
