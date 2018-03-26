package controllers

import javax.inject.Inject

import forms.{UserForms, UserProfile}
import models.UserRepository
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.ExecutionContext.Implicits.global

class ProfileController @Inject()(cc: ControllerComponents,
                                  userRepository: UserRepository,
                                  userForms: UserForms)
  extends AbstractController(cc) with I18nSupport {

  def displayUser() = Action.async {
    implicit request =>
      val userName = request.session.get("userName").get
      val isAdmin = userRepository.checkIsAdmin(userName)
      val userData = userRepository.getUserDetails(userName)
      for {
        admin <- isAdmin
        userDetails <- userData
      } yield {
        val userProfile = UserProfile(userDetails.firstName, userDetails.middleName,
          userDetails.lastName, userDetails.mobileNo, userDetails.gender, userDetails.age, userDetails.hobbies)
        val filledProfileForm = userForms.profileForm.fill(userProfile)
        Ok(views.html.profileDisplay(filledProfileForm, admin))
      }
  }


  def updateUser() = Action.async {
    implicit request =>
      val username = request.session.get("userName").get
      userForms.profileForm.bindFromRequest().fold(
        formWithError => {
          val isAdmin = userRepository.checkIsAdmin(username)
          isAdmin.map {
            case true => BadRequest(views.html.profileDisplay(formWithError, false))
            case false => Redirect(routes.LoginController.showLoginForm())
          }
        },
        data => {
          val updatedUser = UserProfile(data.firstName, data.middleName, data.lastName, data.mobileNo, data.gender, data.age, data.hobbies)
          userRepository.updateProfile(username, updatedUser).map {
            case true =>
              Redirect(routes.ProfileController.displayUser()).withSession("userName" -> username).flashing("profile updated" -> "profile updated successfully")
            case false => InternalServerError("Could not update user")
          }
        }
      )
  }

  def logout() = Action {
    implicit request =>
     Redirect(routes.LoginController.showLoginForm()).withNewSession.flashing("logged out" -> "You have been successfully logged out...")
  }

}
