package controllers

import forms.UserForms
import models.{Assignment, AssignmentRepository, UserData, UserRepository}
import org.mockito.Mockito.when
import org.scalatestplus.play.PlaySpec
import org.specs2.mock.Mockito
import play.api.mvc.ControllerComponents
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.test.CSRFTokenHelper._

import scala.concurrent.Future

class AdminControllerSpec extends PlaySpec with Mockito {

  val controller: TestObjects = getMockedObject
  when(controller.userForm.assignmentForm) thenReturn new UserForms().assignmentForm

  "Admin Controller" should {
    "render assignment form" in {

      controller.adminController.showAssignmentForm().apply(FakeRequest(GET, "/").withFormUrlEncodedBody("csrfToken"
        -> "9c48f081724087b31f")
        .withCSRFToken)
    }

    "add assignment in database" in {

      val assignment = Assignment(0,"Spark Streaming Assignment","Develop an application which gets tweets from Twitter api")
      when(controller.assignmentRepo.addAssignment(assignment)) thenReturn Future.successful(true)

      val request = FakeRequest(POST, "/addAssignment").withFormUrlEncodedBody("csrfToken"
        -> "9c48f081724087b31fcf6099b7ea","title"->"Spark Streaming Assignment","description"->"Develop an application which gets tweets from Twitter api")
        .withCSRFToken

      val result = controller.adminController.addAssignment().apply(request)
      status(result) mustBe  SEE_OTHER
    }

    "return a bad request if assignment form is not filled properly" in {

      val request = FakeRequest(POST, "/addAssignment").withFormUrlEncodedBody("csrfToken"
        -> "9c48f081724087b31fcf6099b7ea","title"->"Spark Streaming Assignment","description"->"Develop an application which gets tweets from Twitter api")
        .withCSRFToken

      val result = controller.adminController.addAssignment().apply(request)
      status(result) mustBe  SEE_OTHER

    }

    "display list of all registered users" in {

      when(controller.userInfoRepo.getAllUsers) thenReturn
        Future.successful(List(UserData(1,"Amit",Some("Kumar"),"Singh","amit@12","amit1234","9790681651","male",34,"reading",isAdmin = false)))

      val request = FakeRequest(GET, "/viewUsers")

      val result = controller.adminController.displayUsers().apply(request)
      status(result) must equal(OK)
    }

    "enable or disable user successfully" in {

      when(controller.userInfoRepo.enableDisableUser("amit@12",newValue=false)) thenReturn Future.successful(true)

      val request = FakeRequest(GET, "/enableOrDisableUser")

      val result = controller.adminController.enableOrDisableUser("amit@12",updatedValue = false).apply(request)
      status(result) mustBe SEE_OTHER

    }

    "display a list of assignments" in {

      when(controller.assignmentRepo.getListOfAssignments) thenReturn
         Future.successful(List(Assignment(1,"Spark Streaming Assignment","Develop an application which gets tweets from Twitter Api")))
      val request = FakeRequest(GET, "/viewAssignments").withSession("userName"->"ankur@23","isAdmin"->"true")

      val result = controller.adminController.viewAssignments().apply(request)
      status(result) must equal(OK)
    }

    "delete assignment successfully" in {

      when(controller.assignmentRepo.deleteAssignment(1)) thenReturn Future.successful(true)
      val request = FakeRequest(GET, "/deleteAssignments")

      val result = controller.adminController.deleteAssignment(1).apply(request)
      status(result) mustBe SEE_OTHER
    }
  }

  def getMockedObject: TestObjects = {
    val mockedUserFormRepo = mock[UserForms]
    val mockedUserUserInfoRepo = mock[UserRepository]
    val mockedAssignmentRepo = mock[AssignmentRepository]

    val controller = new AdminController(stubControllerComponents(), mockedAssignmentRepo,mockedUserUserInfoRepo, mockedUserFormRepo)

    TestObjects(stubControllerComponents(),mockedUserFormRepo,mockedUserUserInfoRepo,mockedAssignmentRepo, controller)
  }

  case class TestObjects(controllerComponent: ControllerComponents,
                         userForm: UserForms,
                         userInfoRepo: UserRepository,
                         assignmentRepo: AssignmentRepository,
                         adminController: AdminController)

}
