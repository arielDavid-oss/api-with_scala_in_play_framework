package controllers.swagger

import com.qrsof.core.swagger
import com.qrsof.core.swagger.Info
import com.qrsof.core.swagger.SwaggerSupport
import controllers.{ProductController, UserController}

class SwaggerHttpServiceImpl extends SwaggerSupport {
  override def apiClasses: Set[Class[_]] = Set(
    classOf[ProductController],
    classOf[UserController]
  )

  override val swaggerHost: String = "localhost:9000"

  // swaggerInfo espera un String, no un Option[String]
  override val swaggerInfo: Info = Info(
    version = "1.0",
    title = "API de Productos y Usuarios",
    description = "API para gestionar productos y usuarios",
    termsOfService = "termsOfService",
    contact = Some(
      swagger.Contact(
        name = "name",
        url = "url",
        email = "email"
      )
    ),
    license = Some(
      swagger.License(
        name = "name",
        url = "url"
      )
    ),
    vendorExtensions = Map("key" -> "value")
  )

  override def swaggerSchemes: List[String] = List("http", "https")
}
