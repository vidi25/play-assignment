package models

import javax.inject.Inject

import forms.UserProfile
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


case class UserData(id: Int,
                    firstName: String,
                    middleName: Option[String],
                    lastName: String,
                    userName: String,
                    password: String,
                    mobileNo: String,
                    gender: String,
                    age: Int,
                    hobbies: String,
                    isEnabled: Boolean = true,
                    isAdmin: Boolean = false)

class UserRepository @Inject() (protected val dbConfigProvider: DatabaseConfigProvider) extends UserRepositoryTable with UserFunctions {

  import profile.api._

  def store(user: UserData): Future[Boolean] =
    db.run(userQuery += user) map (_ > 0)

  def checkUserExists(userName: String): Future[Boolean] =
    db.run(userQuery.filter(user => user.userName === userName).to[List].result.map(user => user.nonEmpty))

  def validateUser(userName: String,password: String): Future[Option[UserData]] =
    db.run(userQuery.filter(user => user.userName === userName && user.password === password ).result.headOption)

  def checkIsAdmin(userName: String): Future[Boolean] =
    db.run(userQuery.filter(user => user.userName === userName).map(user => user.isAdmin).result.head)


  def getAllUsers: Future[List[UserData]] =
    db.run(userQuery.filter(user => user.isAdmin === false).to[List].result)

  def updateProfile(userName: String,updatedUserData: UserProfile): Future[Boolean] =
    db.run(userQuery.filter(user => user.userName === userName).map(
      user => (user.firstName,user.middleName,user.lastName,user.mobileNo,user.gender,user.age,user.hobbies))
      .update(
        updatedUserData.firstName,updatedUserData.middleName,updatedUserData.lastName,updatedUserData.mobileNo
      ,updatedUserData.gender,updatedUserData.age,updatedUserData.hobbies))
      .map(_ > 0)

  def getUserDetails(userName: String): Future[UserProfile] =
    db.run(userQuery.filter(user => user.userName === userName).result.head.map(
      user => UserProfile(user.firstName,user.middleName,user.lastName,user.mobileNo,user.gender,user.age,user.hobbies)))

  def updatePassword(userName: String,newPassword: String): Future[Boolean] =
    db.run(userQuery.filter(user => user.userName === userName)
      .map(user => user.password).update(newPassword)).map(_>0)

  def isUserEnabled(userName: String): Future[Boolean] =
    db.run(userQuery.filter(user => user.userName === userName ).map(user => user.isEnabled).result.head)

  def enableDisableUser(userName: String,newValue: Boolean): Future[Boolean] =
    db.run(userQuery.filter(user => user.userName === userName).map(user => user.isEnabled).update(newValue)).map(_>0)

}


trait UserRepositoryTable extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val userQuery: TableQuery[UserTable] = TableQuery[UserTable]

  class UserTable(tag: Tag) extends Table[UserData](tag, "UserData") {

    def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def firstName: Rep[String] = column[String]("firstname")

    def middleName: Rep[Option[String]] = column[Option[String]]("middlename")

    def lastName: Rep[String] = column[String]("lastname")

    def userName: Rep[String] = column[String]("username")

    def password: Rep[String] = column[String]("password")

    def mobileNo: Rep[String] = column[String]("mobileNo")

    def gender: Rep[String] = column[String]("gender")

    def age: Rep[Int] = column[Int]("age")

    def hobbies: Rep[String] = column[String]("hobbies")

    def isEnabled: Rep[Boolean] = column[Boolean]("isEnabled")

    def isAdmin: Rep[Boolean] = column[Boolean]("isAdmin")

    def * : ProvenShape[UserData] = (id,firstName,middleName,lastName,userName,password,mobileNo,gender,age,hobbies,isEnabled,isAdmin) <>(UserData.tupled, UserData.unapply)
  }

}

trait UserFunctions {

  def store(user: UserData): Future[Boolean]
  def validateUser(userName: String,password: String): Future[Option[UserData]]
  def checkIsAdmin(userName: String): Future[Boolean]
  def checkUserExists(userName: String): Future[Boolean]
  def getAllUsers: Future[List[UserData]]
  def updateProfile(userName: String,updatedUserData: UserProfile): Future[Boolean]
  def getUserDetails(userName: String): Future[UserProfile]
  def updatePassword(userName: String,newPassword: String): Future[Boolean]
  def isUserEnabled(userName: String): Future[Boolean]
  def enableDisableUser(userName: String,newValue: Boolean): Future[Boolean]

}

