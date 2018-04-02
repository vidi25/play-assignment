package views

import akka.util.Timeout
import forms.UserForms
import org.scalatestplus.play.PlaySpec
import org.specs2.mock.Mockito
import play.api.i18n.MessagesProvider
import play.api.mvc.{Flash, RequestHeader}
import play.api.test.Helpers.contentAsString
import scala.concurrent.duration._

class AssignmentSpec extends PlaySpec with Mockito{

  "should render assignment page successfully" in new App {

    implicit val timeout: Timeout = 10 seconds
    val mockedForm: UserForms = mock[UserForms]
    val messageProvider: MessagesProvider = mock[MessagesProvider]
    val requestHeader: RequestHeader = mock[RequestHeader]
    val flash = mock[Flash]
    val html = views.html.assignment(mockedForm.assignmentForm)(messageProvider,requestHeader,flash)
    contentAsString(html) must include("Add Assignment")

  }
}
