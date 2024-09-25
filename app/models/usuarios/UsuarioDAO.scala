package models.usuarios

import javax.inject.{Inject, Singleton}
import models.{Product, Products}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UsuarioDAO @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  private val usuarios = TableQuery[Usuarios]

  def all(): Future[Seq[Usuario]] = db.run(usuarios.result)

  def getById(id: Int): Future[Option[Usuario]] = db.run(usuarios.filter(_.id === id).result.headOption)

  def create(usuario: Usuario): Future[Int] = db.run(usuarios += usuario)

  def delete(id: Int): Future[Int] = db.run(usuarios.filter(_.id === id).delete)

  def findByUsername(username: String): Future[Option[Usuario]]
  = db.run(usuarios.filter(_.username === username).result.headOption)


}
