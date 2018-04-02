package views

import akka.util.Timeout
import models.UserData
import org.scalatestplus.play.PlaySpec
import org.specs2.mock.Mockito
import play.api.test.Helpers.contentAsString

import scala.concurrent.duration._

class ShowUsersSpec extends PlaySpec with Mockito {

  "should render show users page" in new App {

    implicit val timeout: Timeout = 10 seconds
    val mockedListOfUsers = mock[List[UserData]]
    val html = views.html.showUsers(mockedListOfUsers)
    contentAsString(html) must include("List of Users")

  }
}
