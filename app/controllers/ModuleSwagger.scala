package controllers

import com.google.inject
import com.google.inject.AbstractModule
import controllers.swagger.{ApiSwaggerController, ApiSwaggerControllerImpl}

class ModuleSwagger extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[ApiSwaggerController]).to(classOf[ApiSwaggerControllerImpl]).in(classOf[inject.Singleton])
  }
}
