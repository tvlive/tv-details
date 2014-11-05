package controllers

import play.api.mvc.{Action, Controller}

import scala.concurrent.Future

object DummyController extends Controller {

  def dummy() = Action.async {
    Future.successful(Ok("dummy controller"))
  }

}
