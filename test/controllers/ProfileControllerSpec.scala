package controllers

import forms.{UserForms, UserProfile}
import models.UserRepository
import org.mockito.Mockito.when
import org.scalatestplus.play.PlaySpec
import org.specs2.mock.Mockito
import play.api.mvc.ControllerComponents
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers.{stubControllerComponents, _}

import scala.concurrent.Future

class ProfileControllerSpec extends PlaySpec with Mockito {

  val controller: TestObjects = getMockedObject
  val userProfile = UserProfile("Amit", Some("Kumar"), "Singh", "9790681651", "male", 34, "reading")

  "Profile Controller" should {
    "display user profile in a form" in {

      when(controller.userInfoRepo.getUserDetails("amit@12")) thenReturn Future.successful(UserProfile("Amit", Some("Kumar"), "Singh", "9790681651", "male", 34, "reading"))
      when(controller.userForm.profileForm) thenReturn new UserForms().profileForm.fill(userProfile)

      val request = FakeRequest(GET, "/userprofile")
        .withSession("userName"->"amit@12","isAdmin"->"false")
        .withFormUrlEncodedBody("csrfToken" -> "9c48f081724087b31fcf6099b7ea",
          "firstName" -> "Amit", "middleName" -> "Kumar", "lastName" -> "Singh",
        "mobileNumber" -> "9790681651", "gender" -> "male", "age" -> "34", "hobbies" -> "reading")
        .withCSRFToken

      val result = controller.profileController.displayUser().apply(request)
      status(result) must equal(OK)

    }

    " not display user profile in a form if session not found" in {

      when(controller.userInfoRepo.getUserDetails("amit@12")) thenReturn Future.successful(UserProfile("Amit", Some("Kumar"), "Singh", "9790681651", "male", 34, "reading"))
      when(controller.userForm.profileForm) thenReturn new UserForms().profileForm.fill(userProfile)

      val request = FakeRequest(GET, "/userprofile")
        .withFormUrlEncodedBody("csrfToken" -> "9c48f081724087b31fcf6099b7ea",
          "firstName" -> "Amit", "middleName" -> "Kumar", "lastName" -> "Singh",
          "mobileNumber" -> "9790681651", "gender" -> "male", "age" -> "34", "hobbies" -> "reading")
        .withCSRFToken

      val result = controller.profileController.displayUser().apply(request)
      status(result) mustBe 500

    }

    "update user profile successfully" in {
      when(controller.userForm.profileForm) thenReturn new UserForms().profileForm

      val updatedUser = UserProfile("Amit", Some("Kumar"), "Singh", "9112908765", "male", 35, "reading")

      when(controller.userInfoRepo.updateProfile("amit@12",updatedUser)) thenReturn Future.successful(true)

      val request = FakeRequest(POST, "/updateprofile")
        .withSession("userName"->"amit@12","isAdmin"->"false")
        .withFormUrlEncodedBody("csrfToken"
        -> "9c48f081724087b31fcf6099b7ea","firstName" -> "Amit", "middleName" -> "Kumar", "lastName" -> "Singh",
        "mobileNumber" -> "9112908765", "gender" -> "male", "age" -> "35", "hobbies" -> "reading")
        .withCSRFToken

      val result = controller.profileController.updateUser().apply(request)
      status(result) mustBe SEE_OTHER
    }

    "not update user profile successfully" in {
      when(controller.userForm.profileForm) thenReturn new UserForms().profileForm

      val updatedUser = UserProfile("Amit", Some("Kumar"), "Singh", "9112908765", "male", 35, "reading")

      when(controller.userInfoRepo.updateProfile("amit@12",updatedUser)) thenReturn Future.successful(false)

      val request = FakeRequest(POST, "/updateprofile")
        .withSession("userName"->"amit@12","isAdmin"->"false")
        .withFormUrlEncodedBody("csrfToken"
          -> "9c48f081724087b31fcf6099b7ea","firstName" -> "Amit", "middleName" -> "Kumar", "lastName" -> "Singh",
          "mobileNumber" -> "9112908765", "gender" -> "male", "age" -> "35", "hobbies" -> "reading")
        .withCSRFToken

      val result = controller.profileController.updateUser().apply(request)
      status(result) mustBe 500
    }

    "fail to update user profile if session not found" in {
      when(controller.userForm.profileForm) thenReturn new UserForms().profileForm

      val updatedUser = UserProfile("Amit", Some("Kumar"), "Singh", "9112908765", "male", 35, "reading")

      when(controller.userInfoRepo.updateProfile("amit@12",updatedUser)) thenReturn Future.successful(true)

      val request = FakeRequest(POST, "/updateprofile")
        .withFormUrlEncodedBody("csrfToken"
          -> "9c48f081724087b31fcf6099b7ea","firstName" -> "Amit", "middleName" -> "Kumar", "lastName" -> "Singh",
          "mobileNumber" -> "9112908765", "gender" -> "male", "age" -> "35", "hobbies" -> "reading")
        .withCSRFToken

      val result = controller.profileController.updateUser().apply(request)
      status(result) mustBe 500
    }

    "handle updated profile form with errors or bad request" in {
      when(controller.userForm.profileForm) thenReturn new UserForms().profileForm

      val request = FakeRequest(POST, "/updateprofile")
        .withSession("userName"->"amit@12","isAdmin"->"false")
        .withFormUrlEncodedBody("csrfToken"
          -> "9c48f081724087b31fcf6099b7ea","firstName" -> "", "middleName" -> "Kumar", "lastName" -> "Singh",
          "mobileNumber" -> "9112908765", "gender" -> "male", "age" -> "35", "hobbies" -> "reading")
        .withCSRFToken

      val result = controller.profileController.updateUser().apply(request)
      status(result) mustBe 400
    }

    "log out user" in {
      val request = FakeRequest(GET,"/logout")
      val result = controller.profileController.logout().apply(request)
      status(result) mustBe 303
    }
  }


  def getMockedObject: TestObjects = {
    val mockedUserFormRepo = mock[UserForms]
    val mockedUserUserInfoRepo = mock[UserRepository]

    val controller = new ProfileController(stubControllerComponents(), mockedUserUserInfoRepo, mockedUserFormRepo)

    TestObjects(stubControllerComponents(), mockedUserFormRepo, mockedUserUserInfoRepo, controller)
  }

  case class TestObjects(controllerComponent: ControllerComponents,
                         userForm: UserForms,
                         userInfoRepo: UserRepository,
                         profileController: ProfileController)

}
