package controllers

import javax.inject.Inject

import forms.UserForms
import models.AssignmentRepository
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, ControllerComponents}

class AdminController @Inject()(cc: ControllerComponents,
                                assignmentRepository: AssignmentRepository,
                                userForms: UserForms)
  extends AbstractController(cc) with I18nSupport {


  def showAssignmentForm() = Action {
    implicit  request =>
      Ok(views.html.assignment(userForms.assignmentForm))
  }

  def addAssignment() = Action {
    implicit  request =>
      Ok("added")
  }
}
