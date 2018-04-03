package forms

case class UserInformation(firstName: String,
                           middleName: Option[String],
                           lastName: String,
                           userName: String,
                           password: String,
                           reEnterPassword: String,
                           mobileNo: String,
                           gender: String,
                           age: Int,
                           hobbies: String)

case class LoginUser(userName: String,password: String)

case class UserProfile(firstName: String,
                           middleName: Option[String],
                           lastName: String,
                           mobileNo: String,
                           gender: String,
                           age: Int,
                           hobbies: String)

case class AssignmentData(title: String,
                     description: String)

case class ForgetPassword(username: String,
                          newPassword: String,
                          confirmPassword: String)
