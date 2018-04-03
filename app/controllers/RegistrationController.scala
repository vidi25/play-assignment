package controllers

import javax.inject.Inject

import forms.UserForms
import models.{UserData, UserRepository}
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RegistrationController @Inject()(userForms: UserForms,
                                       userRepository: UserRepository,
                                       cc: ControllerComponents)
  extends AbstractController(cc) with I18nSupport {

  def showRegistrationForm(): Action[AnyContent] = Action {
    implicit request =>
      Ok(views.html.register(userForms.registrationForm))
  }

  def registerUser(): Action[AnyContent] = Action.async {
    implicit request =>
      userForms.registrationForm.bindFromRequest().fold(
        formWithError => {
          Future.successful(BadRequest(views.html.register(formWithError)))
        },
        data => {
          val user = UserData(0, data.firstName, data.middleName, data.lastName
            ,data.userName, data.password, data.mobileNo, data.gender, data.age, data.hobbies,isAdmin = false)
          userRepository.checkUserExists(user.userName) flatMap {
            case true => Future.successful(Redirect(routes.LoginController.showLoginForm()).flashing("user exists" -> "user already exists, log in"))
            case false => userRepository.store(user).map {
              case true =>
                Redirect(routes.ProfileController.displayUser())
                  .withSession("userName" -> data.userName,"isAdmin" -> false.toString).flashing("success" -> "user created successfully")
              case false => InternalServerError("Could not create user")
            }
          }
        })
  }

}
