package models

import javax.inject.Inject

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class Assignment(id: Int,
                      title: String,
                      description: String)

class AssignmentRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends AssignmentRepositoryTable with AssignmentRepoFunctions {

  import profile.api._

  /**
    * this method adds assignment to the database.
    * @param assignment : contains assignment data
    * @return Future of Boolean
    */
  override def addAssignment(assignment: Assignment): Future[Boolean] =
    db.run(assignmentQuery += assignment) map (_ > 0)

  /**
    * deletes assignment from database.
    * @param id: assignment id to be deleted
    * @return Future of Boolean
    */
  override def deleteAssignment(id: Int): Future[Boolean] = {
    db.run(assignmentQuery.filter(assignment => assignment.id === id).delete).map(_ > 0)
  }

  /**
    * returns a list of assignments from database.
    * @return Future of List of Assignment
    */
  override def getListOfAssignments: Future[List[Assignment]] =
    db.run(assignmentQuery.to[List].result)

}

trait AssignmentRepositoryTable extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val assignmentQuery: TableQuery[AssignmentTable] = TableQuery[AssignmentTable]

  class AssignmentTable(tag: Tag) extends Table[Assignment](tag, "Assignment") {

    def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def title: Rep[String] = column[String]("title")

    def description: Rep[String] = column[String]("description")

    def * : ProvenShape[Assignment] = (id, title, description) <> (Assignment.tupled, Assignment.unapply)
  }

}

trait AssignmentRepoFunctions {
  def addAssignment(assignment: Assignment): Future[Boolean]

  def deleteAssignment(id: Int): Future[Boolean]

  def getListOfAssignments: Future[List[Assignment]]
}
