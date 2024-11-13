package controllers

import io.swagger.v3.oas.annotations.Operation
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.{Consumes, DELETE, GET, POST, PUT, Path, Produces}
import models.usuarios.{Usuario, UsuarioDAO}
import org.mindrot.jbcrypt.BCrypt
import play.api.libs.json._
import play.api.mvc._
import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.media.{Content, Schema}
import io.swagger.v3.oas.annotations.parameters.RequestBody

@Singleton
@Path("/usuarios")
@Produces(Array(MediaType.APPLICATION_JSON))
@Consumes(Array(MediaType.APPLICATION_JSON))
class UserController @Inject()(cc: ControllerComponents, usuarioDAO: UsuarioDAO)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  // Método para obtener todos los usuarios
  @GET
  @Path("/")
  @Operation(
    summary = "Get all users",
    tags = Array("Usuarios"),
    responses = Array(
      new ApiResponse(
        responseCode = "200",
        description = "List of users",
        content = Array(new Content(schema = new Schema(implementation = classOf[Seq[Usuario]])))
      ),
      new ApiResponse(responseCode = "500", description = "Internal Server Error")
    )
  )
  def getAllUsuarios: Action[AnyContent] = Action.async {
    usuarioDAO.all().map { usuarios =>
      Ok(Json.toJson(usuarios))
    }.recover {
      case _: Exception => InternalServerError(Json.obj("error" -> "Error al obtener los usuarios"))
    }
  }

  // Método para crear un nuevo usuario
  @POST
  @Path("/")
  @Operation(
    summary = "Create a new user",
    tags = Array("Usuarios"),
    requestBody = new RequestBody(
      content = Array(new Content(schema = new Schema(implementation = classOf[Usuario]))
      )
    ),
    responses = Array(
      new ApiResponse(
        responseCode = "201",
        description = "User created",
        content = Array(new Content(schema = new Schema(implementation = classOf[Usuario]))
        )
      ),
      new ApiResponse(responseCode = "500", description = "Internal Server Error")
    )
  )
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
          case _: Exception =>
            InternalServerError(Json.obj("status" -> "error", "message" -> "Error creando usuario"))
        }
      }
    )
  }

  // Método para verificar las credenciales de un usuario
  @POST
  @Path("/verify")
  @Operation(
    summary = "Verify user credentials",
    tags = Array("Usuarios"),
    requestBody = new RequestBody(
      required = true,
      content = Array(new Content(schema = new Schema(implementation = classOf[JsValue]))
      )
    ),
    responses = Array(
      new ApiResponse(
        responseCode = "200",
        description = "Login successful"
      ),
      new ApiResponse(responseCode = "401", description = "Invalid credentials"),
      new ApiResponse(responseCode = "404", description = "User not found"),
      new ApiResponse(responseCode = "400", description = "Missing username or password")
    )
  )
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
