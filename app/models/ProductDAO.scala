package models


import javax.inject.{Inject, Singleton}
import models.{Product, Products}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ProductDAO @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  private val products = TableQuery[Products]

  def all(): Future[Seq[Product]] = db.run(products.result)

  def getById(id: Int): Future[Option[Product]] = db.run(products.filter(_.id === id).result.headOption)

  def create(product: Product): Future[Int] = db.run(products += product)

  def update(id: Int, product: Product): Future[Int] = db.run(products.filter(_.id === id).update(product))

  def delete(id: Int): Future[Int] = db.run(products.filter(_.id === id).delete)
}