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

package uk.gov.hmrc.ctcguaranteebalanceeisstub.models.responses

import play.api.libs.json.Format
import play.api.libs.json.Json
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.GuaranteeReferenceNumber

case class BalanceResponse(grn: GuaranteeReferenceNumber, balance: Balance, currencyCL: String = "GBP")

case class Balance(value: Double) extends AnyVal

object Balance {
  implicit val balanceFormat: Format[Balance] = Json.valueFormat[Balance]
}

object BalanceResponse {

  val constantBalanceValue: Balance = Balance(1234.56)

  implicit val format: Format[BalanceResponse] = Json.format[BalanceResponse]
}
