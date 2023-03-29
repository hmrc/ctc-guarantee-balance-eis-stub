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

package uk.gov.hmrc.ctcguaranteebalanceeisstub.models

import play.api.libs.json._

case class GuaranteeReferenceNumber(value: String) extends AnyVal {

  // we consider all GRNs starting with "1" as not found in the DB.
  def isValid: Boolean = value match {
    case GuaranteeReferenceNumber.grnPattern(_) => !value.startsWith("1")
    case _                                      => false
  }
}

object GuaranteeReferenceNumber {
  val grnPattern = """[0-9]{2}[A-Z]{2}[A-Z0-9]{12}[0-9]([A-Z][0-9]{6})?""".r

  implicit val grnFormat = Json.valueFormat[GuaranteeReferenceNumber]
}
