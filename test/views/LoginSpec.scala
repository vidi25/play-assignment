package views

import forms.UserForms
import org.scalatestplus.play.PlaySpec
import org.specs2.mock.Mockito
import play.api.i18n.MessagesProvider
import play.api.mvc.{Flash, RequestHeader}
import play.api.test.Helpers._

class LoginSpec extends PlaySpec with Mockito {

  "should render login page" in new App {
    val mockedForm: UserForms = mock[UserForms]
    val flash: Flash = mock[Flash]
    val messageProvider: MessagesProvider = mock[MessagesProvider]
    val requestHeader: RequestHeader = mock[RequestHeader]
    val html = views.html.login(mockedForm.loginForm)(messageProvider, requestHeader, flash)
    contentAsString(html) must include("Login Form")
  }
}
