/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.ctcguaranteebalanceeisstub.config

import com.google.inject.AbstractModule

class Module extends AbstractModule {

  override def configure(): Unit =
    bind(classOf[AppConfig]).asEagerSingleton()

}
