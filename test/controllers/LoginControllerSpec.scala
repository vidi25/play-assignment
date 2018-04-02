package controllers

import forms.UserForms
import models.{UserData, UserRepository}
import org.mockito.Mockito.when
import org.scalatestplus.play.PlaySpec
import org.specs2.mock.Mockito
import play.api.mvc.ControllerComponents
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.test.CSRFTokenHelper._

import scala.concurrent.Future

class LoginControllerSpec extends PlaySpec with Mockito {

  val controller: TestObjects = getMockedObject
  when(controller.userForm.loginForm) thenReturn new UserForms().loginForm
  when(controller.userForm.forgetPasswordForm) thenReturn new UserForms().forgetPasswordForm

  "Login Controller" should {
    "show login form" in {

      controller.loginController.showLoginForm().apply(FakeRequest(GET, "/").withFormUrlEncodedBody("csrfToken"
        -> "9c48f081724087b31fcf6099b7eaf6a276834cd9")
        .withCSRFToken)
    }

    "login the valid user" in {
      when(controller.userInfoRepo.isUserEnabled("amit@12")) thenReturn Future.successful(true)
      when(controller.userInfoRepo.validateUser("amit@12","amit1234")) thenReturn Future.successful(Some(UserData(1,"Amit",Some("Kumar"),"Singh","amit@12","amit1234","9790681651","male",34,"reading",isAdmin = false)))

      val request = FakeRequest(POST, "/login").withFormUrlEncodedBody("csrfToken"
        -> "9c48f081724087b31f","username"->"amit@12","password"->"amit1234")
        .withCSRFToken

      val result = controller.loginController.handleLogin().apply(request)
      status(result) mustBe SEE_OTHER
    }

    "not match invalid credentials" in {
      when(controller.userInfoRepo.isUserEnabled("amit@12")) thenReturn Future.successful(true)
      when(controller.userInfoRepo.validateUser("amit@12","amitt12")) thenReturn Future.successful(None)

      val request = FakeRequest(POST, "/login").withFormUrlEncodedBody("csrfToken"
        -> "9c48f081724087b31f","username"->"amit@12","password"->"amitt12")
        .withCSRFToken

      val result = controller.loginController.handleLogin().apply(request)
      status(result) mustBe SEE_OTHER
    }

    "not login if user is not enabled" in {
      when(controller.userInfoRepo.isUserEnabled("amit@12")) thenReturn Future.successful(false)

      val request = FakeRequest(POST, "/login").withFormUrlEncodedBody("csrfToken"
        -> "9c48f081724087b31f","username"->"amit@12","password"->"amit1234")
        .withCSRFToken

      val result = controller.loginController.handleLogin().apply(request)
      status(result) mustBe SEE_OTHER
    }

    "show forget password form" in {

      controller.loginController.showForgetPasswordForm().apply(FakeRequest(GET, "/").withFormUrlEncodedBody("csrfToken"
        -> "9c48f081724087b31fcf6099b7eaf6a276834cd9")
        .withCSRFToken)
    }

    "update password of user" in {
      when(controller.userInfoRepo.checkUserExists("amit@12")) thenReturn Future.successful(true)
      when(controller.userInfoRepo.updatePassword("amit@12","amit2403")) thenReturn Future.successful(true)

      val request = FakeRequest(POST, "/updatePassword").withFormUrlEncodedBody("csrfToken"
        -> "9c48f081724087b31f","username"->"amit@12","newPassword"->"amit2403","confirmPassword"->"amit2403")
        .withCSRFToken

      val result = controller.loginController.changePassword().apply(request)
      status(result) mustBe SEE_OTHER

    }

    "not update password if user does not exist" in {
      when(controller.userInfoRepo.checkUserExists("amit@12")) thenReturn Future.successful(false)

      val request = FakeRequest(POST, "/updatePassword").withFormUrlEncodedBody("csrfToken"
        -> "9c48f081724087b31f","username"->"amit@12","newPassword"->"amit2403","confirmPassword"->"amit2403")
        .withCSRFToken

      val result = controller.loginController.changePassword().apply(request)
      status(result) mustBe SEE_OTHER
    }

    "not update when new password and confirm password don't match" in {

      val request = FakeRequest(POST, "/updatePassword").withFormUrlEncodedBody("csrfToken"
        -> "9c48f081724087b31f","username"->"amit@12","newPassword"->"amit2403","confirmPassword"->"amitt2401")
        .withCSRFToken

      val result = controller.loginController.changePassword().apply(request)
      status(result) mustBe 400

    }
  }

  def getMockedObject: TestObjects = {
    val mockedUserFormRepo = mock[UserForms]
    val mockedUserUserInfoRepo = mock[UserRepository]

    val controller = new LoginController(stubControllerComponents(), mockedUserUserInfoRepo,mockedUserFormRepo)

    TestObjects(stubControllerComponents(), mockedUserFormRepo, mockedUserUserInfoRepo, controller)
  }

  case class TestObjects(controllerComponent: ControllerComponents,
                         userForm: UserForms,
                         userInfoRepo: UserRepository,
                         loginController: LoginController)
}
