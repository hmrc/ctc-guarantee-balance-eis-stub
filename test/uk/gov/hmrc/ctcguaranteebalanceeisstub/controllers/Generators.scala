package uk.gov.hmrc.ctcguaranteebalanceeisstub.controllers

class Generator {

  def guaranteeReferenceNumberGenerator(countryCode: Gen[String]): Gen[GuaranteeReferenceNumber] =
    for {
      year     <- Gen.choose(23, 39).map(_.toString)
      country  <- countryCode
      alphanum <- Gen.stringOfN(12, Gen.alphaNumChar).map(_.toUpperCase)
      num1     <- Gen.numChar.map(_.toString)
      alpha    <- Gen.alphaChar.map(_.toString.toUpperCase)
      num      <- Gen.stringOfN(6, Gen.numChar)
    } yield GuaranteeReferenceNumber(s"$year$country$alphanum$num1$alpha$num")

}
