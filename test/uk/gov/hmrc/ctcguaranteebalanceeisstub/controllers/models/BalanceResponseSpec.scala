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
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.responses.BalanceResponse

class BalanceResponseSpec extends AnyFreeSpec with Matchers with Generators {

  for (balanceResponse <- balanceResponseGenerator.sample) {
    val expectedJson = Json.obj("grn" -> balanceResponse.grn.value, "balance" -> balanceResponse.balance.value, "currencyCL" -> balanceResponse.currencyCL)

    "BalanceResponse should serialize as expected" in {
      expectedJson.validate[BalanceResponse].get shouldBe balanceResponse
    }

    "BalanceResponse should deserialize as expected" in {
      Json.toJson(balanceResponse) shouldBe expectedJson
    }
  }
}
