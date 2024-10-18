package controllers.swagger

import play.api.mvc.{Action, AnyContent}

trait ApiSwaggerController {
  def swaggerSpec(): Action[AnyContent]
  def swaggerUi: Action[AnyContent]
}
