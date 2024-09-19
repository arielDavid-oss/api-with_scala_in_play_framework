package models

import play.api.libs.json._
import slick.jdbc.PostgresProfile.api._
import slick.lifted.{ProvenShape, Tag}


case class Product(
                    id: Int,
                    title: String,
                    price: Double,
                    description: String,
                    category: String,
                    image: String
                  )
object Product {
  implicit val productFormat: OFormat[Product] = Json.format[Product]

}

class Products(tag: Tag) extends Table[Product](tag, "products") {
  def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def title: Rep[String] = column[String]("title")
  def price: Rep[Double] = column[Double]("price")
  def description: Rep[String] = column[String]("description")
  def category: Rep[String] = column[String]("category")
  def image: Rep[String] = column[String]("image")


  def * : ProvenShape[Product] = (id, title, price, description, category, image).mapTo[Product]

}

