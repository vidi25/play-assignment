package models

import javax.inject.Inject

import forms.UserProfile
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import utils.PasswordHashing._


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
                    isAdmin: Boolean)

class UserRepository @Inject() (protected val dbConfigProvider: DatabaseConfigProvider) extends UserRepositoryTable with UserRepoFunctions {

  import profile.api._

  /**
    * adds user to the database.
    * @param user : data of user to be added
    * @return Future of Boolean
    */
  def store(user: UserData): Future[Boolean] = {

    val newUser = user.copy(password = encryptPassword(user.password))
    db.run(userQuery += newUser) map (_ > 0)
  }


  /**
    * this method checks if a user exists or not.
    * @param userName : username of user to be checked
    * @return Future of Boolean
    */
  def checkUserExists(userName: String): Future[Boolean] =
    db.run(userQuery.filter(user => user.userName === userName).to[List].result.map(user => user.nonEmpty))

  /**
    * checks the username and password entered by user against the database.
    * @param userName : username of user to be validated
    * @param password : password of user to be validated
    * @return Future of Option Of Userdata
    */
  def validateUser(userName: String,password: String): Future[Option[UserData]] =
    db.run(userQuery.filter(user => user.userName === userName && user.password === encryptPassword(password)).result.headOption)

  /**
    * displays list of all users in the database.
    * @return Future of List of Userdata
    */
  def getAllUsers: Future[List[UserData]] =
    db.run(userQuery.filter(user => user.isAdmin === false).to[List].result)

  /**
    * this method updates the user profile.
    * @param userName : username of user to be updated
    * @param updatedUserData : updated data of user
    * @return Future of Boolean
    */
  def updateProfile(userName: String,updatedUserData: UserProfile): Future[Boolean] =
    db.run(userQuery.filter(user => user.userName === userName).map(
      user => (user.firstName,user.middleName,user.lastName,user.mobileNo,user.gender,user.age,user.hobbies))
      .update(
        updatedUserData.firstName,updatedUserData.middleName,updatedUserData.lastName,updatedUserData.mobileNo
      ,updatedUserData.gender,updatedUserData.age,updatedUserData.hobbies))
      .map(_ > 0)

  /**
    * this method returns details of a particular user.
    * @param userName : username of user whose details need to be found
    * @return Future of Userprofile
    */
  def getUserDetails(userName: String): Future[UserProfile] =
    db.run(userQuery.filter(user => user.userName === userName).result.head.map(
      user => UserProfile(user.firstName,user.middleName,user.lastName,user.mobileNo,user.gender,user.age,user.hobbies)))

  /**
    * this method updates the password of user.
    * @param userName : username of user whose password to be updated
    * @param newPassword : new value of password
    * @return Future of Boolean
    */
  def updatePassword(userName: String,newPassword: String): Future[Boolean] =
    db.run(userQuery.filter(user => user.userName === userName)
      .map(user => user.password).update(encryptPassword(newPassword))).map(_>0)

  /**
    * this method checks if user is enabled or not.
    * @param userName : username of user to be checked
    * @return Future of Boolean
    */
  def isUserEnabled(userName: String): Future[Boolean] =
    db.run(userQuery.filter(user => user.userName === userName ).map(user => user.isEnabled).result.head)

  /**
    * this method enables or disables the user.
    * @param userName : username of user to be enabled or disabled
    * @param newValue : updated value of isEnabled attribute
    * @return Future of Boolean
    */
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

    def * : ProvenShape[UserData] = (id,firstName,middleName,lastName
      ,userName,password,mobileNo,gender,age,hobbies,isEnabled,isAdmin) <>(UserData.tupled, UserData.unapply)
  }

}

trait UserRepoFunctions {

  def store(user: UserData): Future[Boolean]
  def validateUser(userName: String,password: String): Future[Option[UserData]]
  def checkUserExists(userName: String): Future[Boolean]
  def getAllUsers: Future[List[UserData]]
  def updateProfile(userName: String,updatedUserData: UserProfile): Future[Boolean]
  def getUserDetails(userName: String): Future[UserProfile]
  def updatePassword(userName: String,newPassword: String): Future[Boolean]
  def isUserEnabled(userName: String): Future[Boolean]
  def enableDisableUser(userName: String,newValue: Boolean): Future[Boolean]

}

