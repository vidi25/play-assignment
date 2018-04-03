package controllers

import javax.inject.Inject

import forms.UserForms
import models.{Assignment, AssignmentRepository, UserData, UserRepository}
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AdminController @Inject()(cc: ControllerComponents,
                                assignmentRepository: AssignmentRepository,
                                userRepository: UserRepository,
                                userForms: UserForms)
  extends AbstractController(cc) with I18nSupport {


  def showAssignmentForm(): Action[AnyContent] = Action {
    implicit request =>
      Ok(views.html.assignment(userForms.assignmentForm))
  }

  def addAssignment(): Action[AnyContent] = Action.async {
    implicit request =>
      userForms.assignmentForm.bindFromRequest().fold(
        formWithError => {
          Future.successful(BadRequest(views.html.assignment(formWithError)))
        },
        data => {
          val assignment = Assignment(0,data.title,data.description)
          assignmentRepository.addAssignment(assignment).map{
            case true => Redirect(routes.AdminController.viewAssignments())
            case false => InternalServerError("couldn't add assignment")
          }
        }
      )
  }

  def displayUsers(): Action[AnyContent] = Action.async {
    implicit request =>
      userRepository.getAllUsers.map {
        usersList => Ok(views.html.showUsers(usersList))
      }
  }

  def enableOrDisableUser(username: String,updatedValue: Boolean): Action[AnyContent] = Action.async {
    implicit request =>
      userRepository.enableDisableUser(username,updatedValue).map{
        case true => Redirect(routes.AdminController.displayUsers())
        case false => InternalServerError("couldn't enable or disable user")
      }
  }

  def viewAssignments(): Action[AnyContent] = Action.async {
    implicit request =>
      assignmentRepository.getListOfAssignments.map {
        assignmentsList =>
          request.session.get("isAdmin") match{
          case Some(admin) => if (admin == "false"){
            Ok(views.html.showAssignmentsToUser(assignmentsList))
          }
          else {
            Ok(views.html.showAssignments(assignmentsList))
          }
          case None => InternalServerError("session expired")
        }
      }
  }

  def deleteAssignment(id: Int): Action[AnyContent] = Action.async {
    implicit request =>
      assignmentRepository.deleteAssignment(id).map {
        case true => Redirect(routes.AdminController.viewAssignments())
        case false => InternalServerError("couldn't delete assignment from database")
      }
  }
}
