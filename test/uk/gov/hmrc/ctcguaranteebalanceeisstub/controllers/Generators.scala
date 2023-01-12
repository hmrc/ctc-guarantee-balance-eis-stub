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
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.AccessCode
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.GuaranteeReferenceNumber
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.requests.GuaranteeReferenceNumberRequest
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.responses.AccessCodeResponse
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.responses.BalanceResponse

trait Generators {

  val balanceResponseGenerator: Gen[BalanceResponse] = for {
    grn <- guaranteeReferenceNumberGenerator
  } yield BalanceResponse(grn, BalanceResponse.constantBalanceValue)

  val accessCodeResponseGenerator: Gen[AccessCodeResponse] = for {
    grn <- guaranteeReferenceNumberGenerator
  } yield AccessCodeResponse(grn, AccessCode.constantAccessCodeValue)

  val guaranteeReferenceNumberRequestGenerator: Gen[GuaranteeReferenceNumberRequest] =
    for {
      grn <- guaranteeReferenceNumberGenerator
    } yield GuaranteeReferenceNumberRequest(grn)

  def guaranteeReferenceNumberGenerator: Gen[GuaranteeReferenceNumber] =
    for {
      year     <- Gen.choose(23, 39).map(_.toString)
      country  <- Gen.oneOf("GB", "XI")
      alphanum <- Gen.stringOfN(12, Gen.alphaNumChar).map(_.toUpperCase)
      num1     <- Gen.numChar.map(_.toString)
      alpha    <- Gen.alphaChar.map(_.toString.toUpperCase)
      num      <- Gen.stringOfN(6, Gen.numChar)
    } yield GuaranteeReferenceNumber(s"$year$country$alphanum$num1$alpha$num")

}
