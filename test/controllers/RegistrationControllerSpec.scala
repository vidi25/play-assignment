package controllers

import forms.UserForms
import models.{UserData, UserRepository}
import org.mockito.Mockito.when
import org.scalatestplus.play.PlaySpec
import org.specs2.mock.Mockito
import play.api.mvc.ControllerComponents
import play.api.test.FakeRequest
import play.api.test.Helpers.{stubControllerComponents, _}
import play.api.test.CSRFTokenHelper._

import scala.concurrent.Future

class RegistrationControllerSpec extends PlaySpec with Mockito {

  val controller: TestObjects = getMockedObject
  when(controller.userForm.registrationForm) thenReturn new UserForms().registrationForm

  "Registration Controller" should {
    "render the registration form for user" in {

      val result = controller.registrationController.showRegistrationForm().apply(FakeRequest(GET, "/").withFormUrlEncodedBody("csrfToken"
        -> "9c48f081724087b31fcf6099b7eaf6a276834cd9")
        .withCSRFToken)
      status(result) must equal(OK)

    }

    "register user and save details in database" in {

      when(controller.userInfoRepo.checkUserExists("amit@12")) thenReturn Future.successful(false)

      val user = UserData( 0,"Amit",Some("Kumar"),"Singh","amit@12","amit1234","9790681651","male",34,"reading",isAdmin = false)

      when(controller.userInfoRepo.store(user)) thenReturn Future.successful(true)

      val request = FakeRequest(POST, "/registerUser").withFormUrlEncodedBody("csrfToken"
        -> "9c48f081724087b31fcf6099b7ea", "firstName" -> "Amit","middleName" -> "Kumar", "lastName" -> "Singh",
      "userName"->"amit@12","password"->"amit1234","reEnterPassword"->"amit1234","mobileNumber"->"9790681651",
      "gender"->"male","age"->"34","hobbies"->"reading")
        .withCSRFToken

      val result = controller.registrationController.registerUser().apply(request)
      status(result) mustBe  SEE_OTHER

    }

    "redirect to login if user exists already" in {

      when(controller.userInfoRepo.checkUserExists("amit@12")) thenReturn Future.successful(true)
      val request = FakeRequest(POST, "/registerUser").withFormUrlEncodedBody("csrfToken"
        -> "9c48f081724087b31fcf6099b7ea234", "firstName" -> "Amit","middleName" -> "Kumar", "lastName" -> "Singh",
        "userName"->"amit@12","password"->"amit1234","reEnterPassword"->"amit1234","mobileNumber"->"9790681651",
        "gender"->"male","age"->"34","hobbies"->"reading")
        .withCSRFToken

      val result = controller.registrationController.registerUser().apply(request)
      status(result) mustBe SEE_OTHER

    }

    "handle if user is not added in database" in {

      when(controller.userInfoRepo.checkUserExists("amit@12")) thenReturn Future.successful(false)

      val user = UserData( 0,"Amit",Some("Kumar"),"Singh","amit@12","amit1234","9790681651","male",34,"reading",isAdmin = false)

      when(controller.userInfoRepo.store(user)) thenReturn Future.successful(false)

      val request = FakeRequest(POST, "/registerUser").withFormUrlEncodedBody("csrfToken"
        -> "9c48f081724087b31fcf6099b7ea", "firstName" -> "Amit","middleName" -> "Kumar", "lastName" -> "Singh",
        "userName"->"amit@12","password"->"amit1234","reEnterPassword"->"amit1234","mobileNumber"->"9790681651",
        "gender"->"male","age"->"34","hobbies"->"reading")
        .withCSRFToken

      val result = controller.registrationController.registerUser().apply(request)
      status(result) mustBe  500

    }

    "handle form with errors or a bad request" in {
      val request = FakeRequest(POST, "/registerUser").withFormUrlEncodedBody("csrfToken"
        -> "9c48f081724087b31fcf6099b7ea234", "firstName" -> "Amit","middleName" -> "Kumar", "lastName" -> "",
        "userName"->"amit@12","password"->"amit1234","reEnterPassword"->"amit1234","mobileNumber"->"9790681651",
        "gender"->"male","age"->"34","hobbies"->"reading")
        .withCSRFToken

      val result = controller.registrationController.registerUser().apply(request)
      status(result) mustBe 400

    }

  }

  def getMockedObject: TestObjects = {
    val mockedUserFormRepo = mock[UserForms]
    val mockedUserUserInfoRepo = mock[UserRepository]

    val controller = new RegistrationController(mockedUserFormRepo, mockedUserUserInfoRepo, stubControllerComponents())

    TestObjects(stubControllerComponents(), mockedUserFormRepo, mockedUserUserInfoRepo, controller)
  }

  case class TestObjects(controllerComponent: ControllerComponents,
                         userForm: UserForms,
                         userInfoRepo: UserRepository,
                         registrationController: RegistrationController)

}
