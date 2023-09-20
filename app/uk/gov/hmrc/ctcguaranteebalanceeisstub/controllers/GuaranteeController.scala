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

import play.api.libs.json.JsValue
import play.api.libs.json.Json
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents
import play.api.mvc.Result
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.AccessCode
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.GuaranteeReferenceNumber
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.requests.AccessCodeRequest
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.responses.AccessCodeResponse
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.responses.BalanceResponse
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.responses.RequestErrorResponse

import javax.inject.Inject
import javax.inject.Singleton
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

@Singleton()
class GuaranteeController @Inject() (cc: ControllerComponents)(implicit ec: ExecutionContext) extends BackendController(cc) {

  private def validateGrn(grn: GuaranteeReferenceNumber): Either[Result, Unit] =
    if (grn.value.startsWith("1")) Left(Forbidden(Json.toJson(RequestErrorResponse.invalidGrnError(grn))))
    else if (grn.value.startsWith("02") || grn.value.startsWith("04")) Left(Forbidden(Json.toJson(RequestErrorResponse.invalidTypeError)))
    else Right((): Unit)

  private def validateAccessCode(accessCodeRequest: AccessCodeRequest): Either[Result, Unit] =
    if (accessCodeRequest.masterAccessCode == AccessCode.constantAccessCodeValue) Right((): Unit)
    else Left(Forbidden(Json.toJson(RequestErrorResponse.invalidAccessCode)))

  def validateAccessCode(grn: GuaranteeReferenceNumber): Action[JsValue] = Action.async(parse.json) {
    implicit request =>
      Future {
        request.body
          .validate[AccessCodeRequest]
          .map {
            accessCodeRequest =>
              for {
                _ <- validateGrn(grn)
                _ <- validateAccessCode(accessCodeRequest)
              } yield Ok(Json.toJson(AccessCodeResponse(grn, AccessCode.constantAccessCodeValue)))
          }
          .map(_.fold(x => x, x => x))
          .getOrElse(
            Forbidden
          )
      }
  }

  def getBalance(grn: GuaranteeReferenceNumber): Action[AnyContent] = Action {
    if (validateGrn(grn).isRight)
      Ok(Json.toJson(BalanceResponse(grn, BalanceResponse.constantBalanceValue)))
    else Forbidden
  }
}
