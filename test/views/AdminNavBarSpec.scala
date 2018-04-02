package views

import akka.util.Timeout
import org.scalatestplus.play.PlaySpec
import org.specs2.mock.Mockito
import play.api.test.Helpers.contentAsString

import scala.concurrent.duration._

class AdminNavBarSpec extends PlaySpec with Mockito {

  "should render admin nav bar page successfully" in new App {

    implicit val timeout: Timeout = 10 seconds
    val html = views.html.adminNavBar()
    contentAsString(html) must include("Admin Navbar")

  }
}
