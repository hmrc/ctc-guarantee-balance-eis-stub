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

import play.api.libs.json.*
import play.api.mvc.*
import uk.gov.hmrc.ctcguaranteebalanceeisstub.config.AppConfig
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.requests.AccessCodeRequest
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.responses.AccessCodeResponse
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.responses.BalanceResponse
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.responses.RequestErrorResponse
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.AccessCode
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.GuaranteeReferenceNumber
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.TestScenarios
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton()
class GuaranteeController @Inject() (
  cc: ControllerComponents,
  appConfig: AppConfig
) extends BackendController(cc) {

  def validateAccessCode(grn: GuaranteeReferenceNumber): Action[JsValue] = Action(parse.json) {
    implicit request =>
      request.body
        .validate[AccessCodeRequest]
        .map {
          accessCodeRequest =>
            if (appConfig.testScenariosFeatureEnabled) {
              TestScenarios.getAccessCodeValidationScenarios(grn, accessCodeRequest.masterAccessCode)
            } else {
              validateAndRespond(grn, Some(accessCodeRequest)) {
                Ok(Json.toJson(AccessCodeResponse(grn, accessCodeRequest.masterAccessCode)))
              }
            }
        }
        .getOrElse(invalidJsonFormat())
  }

  def getBalance(grn: GuaranteeReferenceNumber): Action[AnyContent] = Action {
    if (appConfig.testScenariosFeatureEnabled) { TestScenarios.getBalanceResponse(grn) }
    else {
      validateAndRespond(grn) {
        Ok(Json.toJson(BalanceResponse(grn, BalanceResponse.constantBalanceValue)))
      }
    }
  }

  private def validateAndRespond(grn: GuaranteeReferenceNumber, accessCodeRequest: Option[AccessCodeRequest] = None)(onSuccess: => Result) =
    (grn, accessCodeRequest.map(_.masterAccessCode)) match {
      case (grn, _) if grn.value.startsWith("1")                                     => Forbidden(Json.toJson(RequestErrorResponse.invalidGrnError(grn)))
      case (grn, _) if grn.value.startsWith("02") || grn.value.startsWith("04")      => Forbidden(Json.toJson(RequestErrorResponse.invalidTypeError))
      case (_, Some(accessCode)) if accessCode != AccessCode.constantAccessCodeValue => Forbidden(Json.toJson(RequestErrorResponse.invalidAccessCode))
      case _                                                                         => onSuccess
    }

  private def invalidJsonFormat()(implicit request: Request[JsValue]) =
    BadRequest(Json.toJson(RequestErrorResponse("Invalid request payload", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME), request.path)))
}
