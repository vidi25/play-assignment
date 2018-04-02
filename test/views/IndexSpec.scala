package views

import org.scalatestplus.play.PlaySpec
import org.specs2.mock.Mockito
import play.api.test.Helpers._


class IndexSpec extends PlaySpec with Mockito {

  "Rending landing / index page" in {
    val html = views.html.index()
    contentAsString(html) must include("Welcome users..!!")
  }

}