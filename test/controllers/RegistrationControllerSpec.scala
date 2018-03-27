package controllers

import forms.UserForms
import models.UserRepository
import org.scalatestplus.play.PlaySpec
import org.specs2.mock.Mockito
import play.api.mvc.ControllerComponents

class RegistrationControllerSpec extends PlaySpec with Mockito{

  "showRegistrationForm method" should  {
    "render the form for user" in {

    }
  }

  def getMockedObject: TestObjects = {
    val mockedUserFormRepo = mock[UserForms]
    val mockedUserUserInfoRepo= mock[UserRepository]

    val controller = new RegistrationController(mockedUserFormRepo, mockedUserUserInfoRepo, stubControllerComponents())

    TestObjects(stubControllerComponents(), mockedUserFormRepo, mockedUserUserInfoRepo, controller)
  }

  case class TestObjects(controllerComponent: ControllerComponents,
                         userForm: UserForms,
                         userInfoRepo: UserRepository,
                         registrationController: RegistrationController)

}
