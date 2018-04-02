package models

import org.apache.log4j.Logger
import org.specs2.mutable.Specification

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class AssignmentRepositorySpec extends Specification {

  val assignmentRepo = new ModelsTest[AssignmentRepository]

  val assignment = Assignment(1,"Play CRUD Application","Develop a CRUD Application for an employee")
  "assignment repository" should {

    "store assignment in database" in {
      val storeResult = Await.result(assignmentRepo.repository.addAssignment(assignment),Duration.Inf)
      storeResult must equalTo(true)
    }

    "get list of assignments from database" in {
      val listOfAssignments = Await.result(assignmentRepo.repository.getListOfAssignments,Duration.Inf)
      listOfAssignments must equalTo(List(Assignment(1,"Play CRUD Application","Develop a CRUD Application for an employee")))
    }

//    "delete assignment from database" in {
//      val deleteResult = Await.result(assignmentRepo.repository.deleteAssignment(1),Duration.Inf)
//      Logger.getLogger(this.getClass).info(deleteResult)
//      deleteResult must equalTo(true)
//    }

  }
}
