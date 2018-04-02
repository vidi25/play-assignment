package views

import akka.util.Timeout
import forms.UserForms
import org.scalatestplus.play.PlaySpec
import org.specs2.mock.Mockito
import play.api.i18n.MessagesProvider
import play.api.mvc.{Flash, RequestHeader}
import scala.concurrent.duration._
import play.api.test.Helpers.contentAsString

class ProfileDisplaySpec extends PlaySpec with Mockito{

  "should render profile display page" in new App {

    implicit val timeout: Timeout = 10 seconds
    val mockedForm: UserForms = mock[UserForms]
    val flash: Flash = mock[Flash]
    val messageProvider: MessagesProvider = mock[MessagesProvider]
    val requestHeader: RequestHeader = mock[RequestHeader]
    val html = views.html.profileDisplay(mockedForm.profileForm)(flash, requestHeader,messageProvider)
    contentAsString(html) must include("User Profile Page")

  }
}
