package controllers

import javax.inject.Inject

import forms.{UserForms, UserProfile}
import models.UserRepository
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ProfileController @Inject()(cc: ControllerComponents,
                                  userRepository: UserRepository,
                                  userForms: UserForms)
  extends AbstractController(cc) with I18nSupport {

  def displayUser(): Action[AnyContent] = Action.async {
    implicit request =>
      val userName = request.session.get("userName")
      userName match {
        case Some(username) =>
          val userData = userRepository.getUserDetails(username)
          userData.map{
            userdata =>
              val userProfile = UserProfile(userdata.firstName, userdata.middleName,
              userdata.lastName, userdata.mobileNo, userdata.gender, userdata.age, userdata.hobbies)
            val filledProfileForm = userForms.profileForm.fill(userProfile)
            Ok(views.html.profileDisplay(filledProfileForm))
          }
        case None => Future.successful(InternalServerError("session expired, user not found"))
      }
  }


  def updateUser(): Action[AnyContent] = Action.async {
    implicit request =>
      userForms.profileForm.bindFromRequest().fold(
        formWithError => {
          Future.successful(BadRequest(views.html.profileDisplay(formWithError)))
          },
        data => {
          val updatedUser = UserProfile(data.firstName, data.middleName, data.lastName, data.mobileNo, data.gender, data.age, data.hobbies)
          val userName = request.session.get("userName")
          userName match {
            case Some(username) =>
              userRepository.updateProfile(username, updatedUser).map {
                case true =>
                  Redirect(routes.ProfileController.displayUser()).flashing("profile updated" -> "profile updated successfully")
                case false => InternalServerError("Could not update user")
              }
            case None => Future.successful(InternalServerError("session expired, user not found"))
          }

        }
      )
  }

  def logout() = Action {
    implicit request =>
     Redirect(routes.LoginController.showLoginForm()).withNewSession.flashing("logged out" -> "You have been successfully logged out...")
  }

}
