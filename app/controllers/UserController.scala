package controllers

import models.usuarios.{Usuario, UsuarioDAO}
import org.mindrot.jbcrypt.BCrypt
import play.api.libs.json._
import play.api.mvc._

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}


@Singleton
class UserController @Inject()(cc: ControllerComponents, usuarioDAO: UsuarioDAO)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def getAllUsuarios: Action[AnyContent] = Action.async {
    usuarioDAO.all().map { usuarios =>
      Ok(Json.toJson(usuarios))
    }.recover {
      case ex: Exception => InternalServerError(Json.obj("error" -> "Error al obtener los usuarios"))
    }
  }
  // Método para crear un nuevo usuario
  def createUsuario: Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[Usuario].fold(
      errors => Future.successful(BadRequest(JsError.toJson(errors))),
      usuario => {
        // Encriptar la contraseña antes de guardarla
        val hashedPassword = BCrypt.hashpw(usuario.password, BCrypt.gensalt())
        val usuarioConHash = usuario.copy(password = hashedPassword)

        usuarioDAO.create(usuarioConHash).map { _ =>
          Created(Json.toJson(usuarioConHash))
        }.recover {
          case ex: Exception =>
            InternalServerError(Json.obj("status" -> "error", "message" -> "Error creando usuario"))
        }
      }
    )
  }

  // Método para verificar las credenciales de un usuario
  def verifyUsuario: Action[JsValue] = Action.async(parse.json) { request =>
    val json = request.body
    (json \ "username").asOpt[String].zip((json \ "password").asOpt[String]) match {
      case Some((username, password)) =>
        usuarioDAO.findByUsername(username).flatMap {
          case Some(usuario) =>
            if (BCrypt.checkpw(password, usuario.password)) {
              Future.successful(Ok(Json.obj("status" -> "success", "message" -> "Login successful")))
            } else {
              Future.successful(Unauthorized(Json.obj("status" -> "error", "message" -> "Invalid credentials")))
            }
          case None =>
            Future.successful(NotFound(Json.obj("status" -> "error", "message" -> "User not found")))
        }
      case _ =>
        Future.successful(BadRequest(Json.obj("status" -> "error", "message" -> "Missing username or password")))
    }
  }


}
