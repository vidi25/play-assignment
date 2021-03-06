package forms

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}

import scala.util.matching.Regex


class UserForms {

  val allNumbers: Regex = """\d*""".r
  val allLetters: Regex = """[A-Za-z]*""".r
  val maxAge = 75
  val minAge = 18

  val usernameCheckConstraint: Constraint[String] = {
    Constraint("constraints.checkUsername")({
      userName =>
        if (userName matches "[a-zA-Z0-9._^%$#!~@,-]+") {
          Valid
        }
        else {
          Invalid(Seq(ValidationError("Username should have only alphabets and special characters")))
        }
    })
  }

  val allLettersCheckConstraint: Constraint[String] = {
    Constraint("constraints.checkAllLetters")({
      name =>
        if (name matches """[A-Za-z]*""") {
          Valid
        }
        else {
          Invalid(Seq(ValidationError("Name should have only alphabets")))
        }
    })
  }

  val passwordCheckConstraint: Constraint[String] = {
    Constraint("constraints.checkPassword")({
      password =>
        val errors = password match {
          case allNumbers() => Seq(ValidationError("Password should be alphanumeric"))
          case allLetters() => Seq(ValidationError("Password should be alphanumeric"))
          case user_password if user_password.length < 8 => Seq(ValidationError("Password should be of length greater than 8"))
          case _ => Nil
        }
        if (errors.isEmpty) {
          Valid
        } else {
          Invalid(errors)
        }
    })
  }

  val mobileNumberCheckConstraint: Constraint[String] = Constraint("constraints.checkMobileNumber")({
    mobileNo =>
      if (mobileNo.length == 10) {
        mobileNo match {
          case allNumbers() => Valid
        }
      }
      else {
        Invalid(Seq(ValidationError("Mobile number should be of 10 digits and numbers")))
      }
  })

  val registrationForm: Form[UserInformation] = {
    Form(mapping(
      "firstName" -> nonEmptyText.verifying(allLettersCheckConstraint),
      "middleName" -> optional(text),
      "lastName" -> nonEmptyText.verifying(allLettersCheckConstraint),
      "userName" -> nonEmptyText.verifying(usernameCheckConstraint),
      "password" -> nonEmptyText.verifying(passwordCheckConstraint),
      "reEnterPassword" -> nonEmptyText.verifying(passwordCheckConstraint),
      "mobileNumber" -> text.verifying(mobileNumberCheckConstraint),
      "gender" -> nonEmptyText,
      "age" -> number(min = minAge, max = maxAge),
      "hobbies" -> nonEmptyText
    )(UserInformation.apply)(UserInformation.unapply)
      verifying("Password fields do not match ", user => user.password == user.reEnterPassword))
  }

  val loginForm: Form[LoginUser] = {
    Form(mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText
    )(LoginUser.apply)(LoginUser.unapply))
  }

  val profileForm: Form[UserProfile] = {
    Form(mapping(
      "firstName" -> nonEmptyText.verifying(allLettersCheckConstraint),
      "middleName" -> optional(text),
      "lastName" -> nonEmptyText.verifying(allLettersCheckConstraint),
      "mobileNumber" -> text.verifying(mobileNumberCheckConstraint),
      "gender" -> nonEmptyText,
      "age" -> number(min = minAge, max = maxAge),
      "hobbies" -> nonEmptyText
    )(UserProfile.apply)(UserProfile.unapply))
  }

  val assignmentForm: Form[AssignmentData] = {
    Form(mapping(
      "title" -> text.verifying("Title is required",title => title.nonEmpty),
      "description" -> text.verifying("Description is required and length " +
        "should not be more than 100 characters",
        description => if (description.nonEmpty && description.length <=100) true else false)
    )(AssignmentData.apply)(AssignmentData.unapply))
  }

  val forgetPasswordForm: Form[ForgetPassword] = {
    Form(mapping(
      "username" -> nonEmptyText.verifying(usernameCheckConstraint),
      "newPassword" -> nonEmptyText.verifying(passwordCheckConstraint),
      "confirmPassword" -> nonEmptyText.verifying(passwordCheckConstraint)
    )(ForgetPassword.apply)(ForgetPassword.unapply)
      verifying("Password fields do not match ", user => user.newPassword == user.confirmPassword))
  }

}
