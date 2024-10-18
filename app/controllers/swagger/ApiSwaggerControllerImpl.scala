package controllers.swagger

import jakarta.inject.{Inject, Singleton}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}

import scala.io.Source

@Singleton
class ApiSwaggerControllerImpl @Inject()(val controllerComponents: ControllerComponents,
                                         swaggerHttpServiceImpl: SwaggerHttpServiceImpl
                                        ) extends BaseController with ApiSwaggerController {

  def swaggerSpec(): Action[AnyContent] = Action {
    Ok(swaggerHttpServiceImpl.getJsonSwagger()).as(JSON)
    val jsonContent = Source.fromFile("conf/swagger.json").getLines().mkString("\n")
    Ok(jsonContent).as("application/json")
  }

  def swaggerUi: Action[AnyContent] = Action {
    Redirect("http://petstore.swagger.io/?url=http://localhost:9000/api-docs/swagger.json")
  }
}
