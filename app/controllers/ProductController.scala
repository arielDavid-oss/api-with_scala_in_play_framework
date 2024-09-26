package controllers

import javax.inject._
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}
import models.ProductDAO
import models.Product
import play.api.libs.json._

@Singleton
class ProductController @Inject()(cc: ControllerComponents, productDAO: ProductDAO)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def getProducts: Action[AnyContent] = Action.async {
    productDAO.all().map { products =>
      Ok(Json.toJson(products))
    }.recover {
      case ex: Exception => InternalServerError(Json.obj("error" -> "Error al obtener los productos"))
    }
  }

  def getProduct(id: Int): Action[AnyContent] = Action.async {
    productDAO.getById(id).map {
      case Some(product) => Ok(Json.toJson(product))
      case None => NotFound(Json.obj("error" -> s"Producto con id $id no encontrado"))
    }.recover {
      case ex: Exception => InternalServerError(Json.obj("error" -> "Error al obtener el producto"))
    }
  }

  def createProduct: Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[Product].fold(
      errors => Future.successful(BadRequest(JsError.toJson(errors))),
      product => {
        productDAO.create(product).map { _ =>
          Created(Json.toJson(product))
        }
      }
    )
  }

  def updateProduct(id: Int): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[Product].fold(
      errors => Future.successful(BadRequest(JsError.toJson(errors))),
      product => {
        productDAO.getById(id).flatMap {
          case Some(_) =>
            productDAO.update(id, product).map { updatedCount =>
              if (updatedCount > 0) NoContent else NotFound
            }
          case None => Future.successful(NotFound(Json.obj("error" -> s"Producto con id $id no encontrado")))
        }
      }
    )
  }

  def deleteProduct(id: Int): Action[AnyContent] = Action.async {
    productDAO.delete(id).map { deletedCount =>
      if (deletedCount > 0) NoContent else NotFound
    }
  }
}