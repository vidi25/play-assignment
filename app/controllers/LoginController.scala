package controllers

import javax.inject.Inject

import forms.UserForms
import models.{UserData, UserRepository}
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LoginController @Inject()(cc: ControllerComponents,
                                userRepository: UserRepository,
                                userForms: UserForms)
  extends AbstractController(cc) with I18nSupport {

  def showLoginForm() = Action {
    implicit request =>
      Ok(views.html.login(userForms.loginForm))
  }

  def handleLogin() = Action.async {
    implicit request =>
      userForms.loginForm.bindFromRequest().fold(
        formWithError => {
          Future.successful(BadRequest(views.html.login(formWithError)))
        },
        data => {
          userRepository.validateUser(data.userName, data.password).map {
            case Some(user) => Redirect(routes.ProfileController.displayUser()).withSession("userName" -> data.userName).flashing("logged in" -> "logged in successfully")
            case None => Redirect(routes.LoginController.showLoginForm()).flashing("incorrect" -> "invalid credentials")
          }
        }
      )
  }

  def showForgetPasswordForm() = Action {
    implicit request =>
      Ok(views.html.forgetPassword(userForms.forgetPasswordForm))
  }

  def changePassword() = Action.async {
      implicit request =>
        userForms.forgetPasswordForm.bindFromRequest().fold(
          formWithError => {
            Future.successful(BadRequest(views.html.forgetPassword(formWithError)))
          },
          data => {
            userRepository.checkUserExists(data.username) flatMap {
              case true => userRepository.updatePassword(data.username,data.newPassword).map {
                case true =>
                  Redirect(routes.ProfileController.displayUser()).withSession("userName" -> data.username).flashing("password updated" -> "password changed successfully")
                case false => InternalServerError("Could not update password")
              }
              case false => Future.successful(Redirect(routes.LoginController.showForgetPasswordForm()).flashing("not exists" -> "user does not exist, try again.."))
            }
          })
    }

}