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

import play.api.libs.json.JsString
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import play.api.mvc.Action
import play.api.mvc.ControllerComponents
import play.api.mvc.Result
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.AccessCode
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.GuaranteeReferenceNumber
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.requests.GuaranteeReferenceNumberRequest
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.responses.AccessCodeResponse
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.responses.BalanceResponse

import javax.inject.Inject
import javax.inject.Singleton
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

@Singleton()
class GuaranteeController @Inject() (cc: ControllerComponents)(implicit ec: ExecutionContext) extends BackendController(cc) {

  def getAccessCode(): Action[JsValue] = Action.async(parse.json) {
    implicit request =>
      Future {
        validateGRN(request.body) match {
          case Right(grn)        => Ok(Json.toJson(AccessCodeResponse(grn, AccessCode.constantAccessCodeValue)))
          case Left(errorResult) => errorResult
        }
      }
  }

  def getBalance(): Action[JsValue] = Action.async(parse.json) {
    implicit request =>
      Future {
        validateGRN(request.body) match {
          case Right(grn)        => Ok(Json.toJson(BalanceResponse(grn, BalanceResponse.constantBalanceValue)))
          case Left(errorResult) => errorResult
        }
      }
  }

  private def validateGRN(body: JsValue): Either[Result, GuaranteeReferenceNumber] =
    body
      .validate[GuaranteeReferenceNumberRequest]
      .map {
        request =>
          request.GRN.hasValidFormat() match {
            case true  => Right(request.GRN)
            case false => Left(InternalServerError(JsString(s"The guarantee reference number [${request.GRN.value}] is not in the correct format.")))
          }
      }
      .getOrElse(
        Left(BadRequest(JsString("Expected GRN in the body.")))
      )
}
