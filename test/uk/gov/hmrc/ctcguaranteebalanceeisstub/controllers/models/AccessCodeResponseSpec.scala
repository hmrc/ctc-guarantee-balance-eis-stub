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

package uk.gov.hmrc.ctcguaranteebalanceeisstub.controllers.models

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.Json
import uk.gov.hmrc.ctcguaranteebalanceeisstub.controllers.Generators
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.responses.AccessCodeResponse

class AccessCodeResponseSpec extends AnyFreeSpec with Matchers with Generators {

  for (accessCodeResponse <- accessCodeResponseGenerator.sample) {
    val expectedJson = Json.obj(
      "grn"                   -> accessCodeResponse.grn.value,
      "masterAccessCode"      -> accessCodeResponse.masterAccessCode.value,
      "additionalAccessCodes" -> accessCodeResponse.additionalAccessCodes
    )

    "AccessCodeResponse should serialize as expected" in {
      expectedJson.validate[AccessCodeResponse].get shouldBe accessCodeResponse
    }

    "AccessCodeResponse should deserialize as expected" in {
      Json
        .toJson(accessCodeResponse) shouldBe expectedJson
    }
  }

}
