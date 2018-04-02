package views

import akka.util.Timeout
import models.Assignment
import org.scalatestplus.play.PlaySpec
import org.specs2.mock.Mockito
import play.api.mvc.RequestHeader
import play.api.test.Helpers.contentAsString
import scala.concurrent.duration._

class ShowAssignmentsToUserSpec extends PlaySpec with Mockito {


  "should render show assignments to user page" in new App {

    implicit val timeout: Timeout = 10 seconds
    val mockedListOfAssignment = mock[List[Assignment]]
    val requestHeader = mock[RequestHeader]
    val html = views.html.showAssignmentsToUser(mockedListOfAssignment)(requestHeader)
    contentAsString(html) must include("List of Assignments")

  }
}
