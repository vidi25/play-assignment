package controllers

import javax.inject.Inject

import forms.UserForms
import models.UserRepository
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LoginController @Inject()(cc: ControllerComponents,
                                userRepository: UserRepository,
                                userForms: UserForms)
  extends AbstractController(cc) with I18nSupport {

  /**
    * an action which renders login form.
    * @return the login html page in form of Ok
    */
  def showLoginForm(): Action[AnyContent] = Action {
    implicit request =>
      Ok(views.html.login(userForms.loginForm))
  }

  /**
    * an action which validates the user credentials and let him login.
    */
  def handleLogin(): Action[AnyContent] = Action.async {
    implicit request =>
      userForms.loginForm.bindFromRequest().fold(
        formWithError => {
          Future.successful(BadRequest(views.html.login(formWithError)))
        },
        data => {
          for {
            isEnabled <- userRepository.isUserEnabled(data.userName)
            validUser <- userRepository.validateUser(data.userName, data.password)
          } yield {
            if (isEnabled) {
              validUser match {
                case Some(user) => Redirect(routes.ProfileController.displayUser())
                  .withSession("userName" -> data.userName, "isAdmin" -> user.isAdmin.toString)
                  .flashing("logged in" -> "logged in successfully")
                case None => Redirect(routes.LoginController.showLoginForm()).flashing("incorrect user" -> "invalid credentials")
              }
            }
            else {
              Redirect(routes.LoginController.showLoginForm()).flashing("disabled" -> "can't login, account disabled")
            }
          }
        }
      )
  }

  /**
    * an action which renders a page for resetting password.
    * @return redirects to the html page
    */
  def showForgetPasswordForm(): Action[AnyContent] = Action {
    implicit request =>
      Ok(views.html.forgetPassword(userForms.forgetPasswordForm))
  }

  /**
    * an action which updates the password of user.
    * @return Redirect: if password updated successfully
    */
  def changePassword(): Action[AnyContent] = Action.async {
    implicit request =>
      userForms.forgetPasswordForm.bindFromRequest().fold(
        formWithError => {
          Future.successful(BadRequest(views.html.forgetPassword(formWithError)))
        },
        data => {
          userRepository.checkUserExists(data.username) flatMap {
            case true => userRepository.updatePassword(data.username, data.newPassword).map {
              case true =>
                Redirect(routes.ProfileController.displayUser())
                  .withSession("userName" -> data.username)
                  .flashing("password updated" -> "password changed successfully")
              case false => InternalServerError("Could not update password")
            }
            case false => Future.successful(Redirect(routes.LoginController.showForgetPasswordForm())
              .flashing("not exists" -> "user does not exist, try again.."))
          }
        })
  }

}
