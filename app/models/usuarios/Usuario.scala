package models.usuarios

import play.api.libs.json._
import slick.jdbc.PostgresProfile.api._
import slick.lifted.{ProvenShape, Tag}

// Definición del modelo Usuario
case class Usuario(
                    id: Option[Int],    // ID del usuario (opcional, para autoincremento)
                    username: String,   // Campo para el nombre de usuario
                    email: String,      // Campo para el correo electrónico
                    password: String,   // Campo para la contraseña
                  )

// Implicit conversion para serialización/deserialización JSON
object Usuario {
  implicit val usuarioFormat: OFormat[Usuario] = Json.format[Usuario]
}

// Definición de la tabla Usuarios
class Usuarios(tag: Tag) extends Table[Usuario](tag, "users") {
  def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc) // ID
  def username: Rep[String] = column[String]("username") // Nombre de usuario
  def email: Rep[String] = column[String]("email") // Correo electrónico
  def password: Rep[String] = column[String]("password") // Contraseña

  // Mapeo de la tabla a la clase Usuario
  def * : ProvenShape[Usuario] = (id.?, username, email, password).mapTo[Usuario]
}
