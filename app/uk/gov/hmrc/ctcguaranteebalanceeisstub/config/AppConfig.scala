/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.ctcguaranteebalanceeisstub.config

import javax.inject.Inject
import javax.inject.Singleton
import play.api.Configuration

@Singleton
class AppConfig @Inject() (config: Configuration) {

  val appName: String = config.get[String]("appName")
}
