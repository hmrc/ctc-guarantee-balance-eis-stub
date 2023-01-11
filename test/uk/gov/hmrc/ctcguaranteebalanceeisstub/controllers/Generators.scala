/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.ctcguaranteebalanceeisstub.controllers

import org.scalacheck.Gen
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.AccessCode
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.GuaranteeReferenceNumber
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.responses.AccessCodeResponse
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.responses.Balance
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.responses.BalanceResponse

trait Generators {

  def balanceResponseGenerator(): Gen[BalanceResponse] = for {
    grn <- guaranteeReferenceNumberGenerator()
  } yield BalanceResponse(grn, Balance.generateBalance())

  def accessCodeResponseGenerator(): Gen[AccessCodeResponse] = for {
    grn <- guaranteeReferenceNumberGenerator()
  } yield AccessCodeResponse(grn, AccessCode.generateAccessCode())

  def guaranteeReferenceNumberGenerator(): Gen[GuaranteeReferenceNumber] =
    for {
      year     <- Gen.choose(23, 39).map(_.toString)
      country  <- Gen.oneOf("GB", "XI")
      alphanum <- Gen.stringOfN(12, Gen.alphaNumChar).map(_.toUpperCase)
      num1     <- Gen.numChar.map(_.toString)
      alpha    <- Gen.alphaChar.map(_.toString.toUpperCase)
      num      <- Gen.stringOfN(6, Gen.numChar)
    } yield GuaranteeReferenceNumber(s"$year$country$alphanum$num1$alpha$num")

}
