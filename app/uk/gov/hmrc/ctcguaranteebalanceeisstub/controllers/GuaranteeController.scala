/*
 * Copyright 2023 HM Revenue & Customs
 *
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
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.responses.AccessCodeResponse
import uk.gov.hmrc.ctcguaranteebalanceeisstub.models.responses.Balance
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
          case Right(grn)        => Ok(Json.toJson(AccessCodeResponse(grn, AccessCode.generateAccessCode())))
          case Left(errorResult) => errorResult
        }
      }
  }

  def getBalance(): Action[JsValue] = Action.async(parse.json) {
    implicit request =>
      Future {
        validateGRN(request.body) match {
          case Right(grn)        => Ok(Json.toJson(BalanceResponse(grn, Balance.generateBalance())))
          case Left(errorResult) => errorResult
        }
      }
  }

  private def validateGRN(body: JsValue): Either[Result, GuaranteeReferenceNumber] =
    body
      .validate[GuaranteeReferenceNumber]
      .map {
        grn =>
          grn.hasValidFormat() match {
            case true  => Right(grn)
            case false => Left(InternalServerError(JsString(s"The guarantee reference number [${grn.GRN}] is not in the correct format.")))
          }
      }
      .getOrElse(
        Left(BadRequest(JsString("Expected GRN in the body.")))
      )
}
