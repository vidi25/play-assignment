package models

import forms.UserProfile
import org.specs2.mutable.Specification

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class UserRepositorySpec extends Specification{

val userRepo = new ModelsTest[UserRepository]

  val user = UserData(1,"Amit",Some("Kumar"),"Singh","amit@12","amit1234","9987645321","male",34,"Dancing",isAdmin = false)
  val updatedUser = UserProfile("Amit",Some("Kumar"),"Singh","9960823410","male",34,"Singing")
  "User repository"should {
    "add user to database" in {
      val userAddedResult = Await.result(userRepo.repository.store(user),Duration.Inf)
      userAddedResult must equalTo(true)
    }

    "validate user and return user data" in {
      val validatedUser = Await.result(userRepo.repository.validateUser("amit@12","amit1234"),Duration.Inf)
      validatedUser must beSome(UserData(1,"Amit",Some("Kumar"),"Singh","amit@12","amit1234","9987645321","male",34,"Dancing",isAdmin = false))
    }

    "return None when credentials do not match" in {
      val validatedUser = Await.result(userRepo.repository.validateUser("ankur@45","ankur90"),Duration.Inf)
      validatedUser must beNone
    }

    "check if user exists" in {
      val userFound = Await.result(userRepo.repository.checkUserExists("amit@12"),Duration.Inf)
      userFound must equalTo(true)
    }

    "return false if user does not exist" in {
      val userFound = Await.result(userRepo.repository.checkUserExists("ankur@45"),Duration.Inf)
      userFound must equalTo(false)
    }

    "get all users list registered" in {
      val usersList = Await.result(userRepo.repository.getAllUsers,Duration.Inf)
      usersList must equalTo(List(UserData(1,"Amit",Some("Kumar"),"Singh","amit@12","amit1234","9987645321","male",34,"Dancing",isAdmin = false)))
    }

    "get user details of a particular user" in {
      val userdata = Await.result(userRepo.repository.getUserDetails("amit@12"),Duration.Inf)
      userdata must equalTo(UserProfile("Amit",Some("Kumar"),"Singh","9987645321","male",34,"Dancing"))
    }

    "return true when profile gets updated" in {
      val userUpdated = Await.result(userRepo.repository.updateProfile("amit@12",updatedUser),Duration.Inf)
      userUpdated must equalTo(true)
    }

    "update password of a user and return true" in {
      val passwordUpdated = Await.result(userRepo.repository.updatePassword("amit@12","amit2301"),Duration.Inf)
      passwordUpdated must equalTo(true)
    }

    "checks if user is enabled" in {
      val enabledUser = Await.result(userRepo.repository.isUserEnabled("amit@12"),Duration.Inf)
      enabledUser must equalTo(true)
    }

    "enable or disable a user" in {
      val user = Await.result(userRepo.repository.enableDisableUser("amit@12",newValue = false),Duration.Inf)
      user must equalTo(true)
    }

  }
}
