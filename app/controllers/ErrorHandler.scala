package controllers

import play.api.http.HttpErrorHandler
import play.api.mvc._
import play.api.mvc.Results._
import scala.concurrent._
import javax.inject.Singleton

@Singleton
class ErrorHandler extends HttpErrorHandler {

  /**
    * displays error message on client error.
    * @param request : RequestHeader
    * @param statusCode : Code of error
    * @param message : error message
    * @return
    */
  def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    Future.successful(
      Status(statusCode)("A client error occurred: " + statusCode)
    )
  }

  /**
    * displays error message on server error.
    * @param request : RequestHeader
    * @param exception : Throwable Exception
    * @return
    */
  def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    Future.successful(
      InternalServerError("A server error occurred: " + exception.getMessage)
    )
  }
}
