package views

import akka.util.Timeout
import forms.UserForms
import org.scalatestplus.play.PlaySpec
import org.specs2.mock.Mockito
import play.api.i18n.MessagesProvider
import play.api.mvc.RequestHeader
import play.api.test.Helpers.contentAsString

import scala.concurrent.duration._

class RegisterSpec extends PlaySpec with Mockito {

  "should render registration page" in new App {

    implicit val timeout: Timeout = 10 seconds
    val mockedForm: UserForms = mock[UserForms]
    val messageProvider: MessagesProvider = mock[MessagesProvider]
    val requestHeader: RequestHeader = mock[RequestHeader]
    val html = views.html.register(mockedForm.registrationForm)(requestHeader, messageProvider)
    contentAsString(html) must include("Registration Form")

  }
}
