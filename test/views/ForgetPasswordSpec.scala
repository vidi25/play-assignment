package views

import akka.util.Timeout
import forms.UserForms
import org.scalatestplus.play.PlaySpec
import org.specs2.mock.Mockito
import play.api.i18n.MessagesProvider
import play.api.mvc.{Flash, RequestHeader}
import play.api.test.Helpers.contentAsString

import scala.concurrent.duration._

class ForgetPasswordSpec extends PlaySpec with Mockito{

  "should render forgot password page" in new App {

    implicit val timeout: Timeout = 10 seconds
    val mockedForm: UserForms = mock[UserForms]
    val messageProvider: MessagesProvider = mock[MessagesProvider]
    val requestHeader: RequestHeader = mock[RequestHeader]
    val flash = mock[Flash]
    val html = views.html.forgetPassword(mockedForm.forgetPasswordForm)(flash,messageProvider,requestHeader)
    contentAsString(html) must include("Forgot Password")

  }
}
