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


  /**
    * an action which renders a page to add assignment.
    * @return response in form of OK
    */
  def showAssignmentForm(): Action[AnyContent] = Action {
    implicit request =>
      Ok(views.html.assignment(userForms.assignmentForm))
  }

  /**
    * an action which adds assignment to the database.
    * @return BadRequest: if form has errors
    *         Redirect: if assignment added successfully
    *         InternalServerError: if assignment doesn't get added
    */
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

  /**
    * an action which display's users list to admin.
    * @return OK: to the page which display's list
    */
  def displayUsers(): Action[AnyContent] = Action.async {
    implicit request =>
      userRepository.getAllUsers.map {
        usersList => Ok(views.html.showUsers(usersList))
      }
  }

  /**
    * an action which enables or disables user.
    * @return Redirect: if user is enabled or disabled successfully
    *         InternalServerError: if user not enabled or disabled
    */
  def enableOrDisableUser(username: String,updatedValue: Boolean): Action[AnyContent] = Action.async {
    implicit request =>
      userRepository.enableDisableUser(username,updatedValue).map{
        case true => Redirect(routes.AdminController.displayUsers())
        case false => InternalServerError("couldn't enable or disable user")
      }
  }

  /**
    * an action which display's list of assignment to admin and user.
    * @return Ok: if session of user has not expired then renders
    *         page with assignments list
    *         InternalServerError: if session expired
    */
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

  /**
    * an action which deletes assignment from the database.
    * @return Redirect: if assignment deleted successfully
    *         InternalServerError: if assignment doesn't get deleted
    */
  def deleteAssignment(id: Int): Action[AnyContent] = Action.async {
    implicit request =>
      assignmentRepository.deleteAssignment(id).map {
        case true => Redirect(routes.AdminController.viewAssignments())
        case false => InternalServerError("couldn't delete assignment from database")
      }
  }
}
