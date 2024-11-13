package controllers

import javax.inject._
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}
import models.{ProductDAO, Product}
import play.api.libs.json._
import io.swagger.v3.oas.annotations.{Operation, Parameter}
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.{Content, Schema}
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.ws.rs.{Consumes, DELETE, GET, POST, PUT, Path, Produces}
import jakarta.ws.rs.core.MediaType

@Singleton
@Path("/products")
@Produces(Array(MediaType.APPLICATION_JSON))
@Consumes(Array(MediaType.APPLICATION_JSON))
class ProductController @Inject()(cc: ControllerComponents, productDAO: ProductDAO)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  @GET
  @Path("/")
  @Operation(
    summary = "Get all products",
    tags = Array("Productos"),
    responses = Array(
      new ApiResponse(
        responseCode = "200",
        description = "List of products",
        content = Array(new Content(schema = new Schema(implementation = classOf[Seq[Product]])))
      ),
      new ApiResponse(responseCode = "500", description = "Internal Server Error")
    )
  )
  def getProducts: Action[AnyContent] = Action.async {
    productDAO.all().map { products =>
      Ok(Json.toJson(products))
    }.recover {
      case ex: Exception => InternalServerError(Json.obj("error" -> "Error al obtener los productos"))
    }
  }

  @GET
  @Path("/{id}")
  @Operation(
    summary = "Get a product by ID",
    tags = Array("Productos"),
    parameters = Array(
      new Parameter(
        name = "id",
        in = ParameterIn.PATH,
        required = true,
        description = "ID of the product to retrieve",
        schema = new Schema(implementation = classOf[Int])
      )
    ),
    responses = Array(
      new ApiResponse(
        responseCode = "200",
        description = "Product details",
        content = Array(new Content(schema = new Schema(implementation = classOf[Product])))
      ),
      new ApiResponse(responseCode = "404", description = "Product not found"),
      new ApiResponse(responseCode = "500", description = "Internal Server Error")
    )
  )
  def getProduct(id: Int): Action[AnyContent] = Action.async {
    productDAO.getById(id).map {
      case Some(product) => Ok(Json.toJson(product))
      case None => NotFound(Json.obj("error" -> s"Producto con id $id no encontrado"))
    }.recover {
      case ex: Exception => InternalServerError(Json.obj("error" -> "Error al obtener el producto"))
    }
  }

  @POST
  @Path("/")
  @Operation(
    summary = "Create a new product",
    tags = Array("Productos"),
    requestBody = new RequestBody(
      required = true,
      content = Array(
        new Content(schema = new Schema(implementation = classOf[Product]))
      )
    ),
    responses = Array(
      new ApiResponse(
        responseCode = "201",
        description = "Product created",
        content = Array(new Content(schema = new Schema(implementation = classOf[Product])))
      ),
      new ApiResponse(responseCode = "400", description = "Invalid input"),
      new ApiResponse(responseCode = "500", description = "Internal Server Error")
    )
  )
  def createProduct: Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[Product].fold(
      errors => Future.successful(BadRequest(JsError.toJson(errors))),
      product => {
        productDAO.create(product).map { _ =>
          Created(Json.toJson(product))
        }.recover {
          case ex: Exception => InternalServerError(Json.obj("error" -> "Error al crear el producto"))
        }
      }
    )
  }

  @PUT
  @Path("/{id}")
  @Operation(
    summary = "Update a product by ID",
    tags = Array("Productos"),
    parameters = Array(
      new Parameter(
        name = "id",
        in = ParameterIn.PATH,
        required = true,
        description = "ID of the product to update",
        schema = new Schema(implementation = classOf[Int])
      )
    ),
    requestBody = new RequestBody(
      required = true,
      content = Array(
        new Content(schema = new Schema(implementation = classOf[Product]))
      )
    ),
    responses = Array(
      new ApiResponse(responseCode = "204", description = "Product updated"),
      new ApiResponse(responseCode = "404", description = "Product not found"),
      new ApiResponse(responseCode = "400", description = "Invalid input"),
      new ApiResponse(responseCode = "500", description = "Internal Server Error")
    )
  )
  def updateProduct(id: Int): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[Product].fold(
      errors => Future.successful(BadRequest(JsError.toJson(errors))),
      product => {
        productDAO.getById(id).flatMap {
          case Some(_) =>
            productDAO.update(id, product).map { updatedCount =>
              if (updatedCount > 0) NoContent else NotFound
            }.recover {
              case ex: Exception => InternalServerError(Json.obj("error" -> "Error al actualizar el producto"))
            }
          case None => Future.successful(NotFound(Json.obj("error" -> s"Producto con id $id no encontrado")))
        }
      }
    )
  }

  @DELETE
  @Path("/{id}")
  @Operation(
    summary = "Delete a product by ID",
    tags = Array("Productos"),
    parameters = Array(
      new Parameter(
        name = "id",
        in = ParameterIn.PATH,
        required = true,
        description = "ID of the product to delete",
        schema = new Schema(implementation = classOf[Int])
      )
    ),
    responses = Array(
      new ApiResponse(responseCode = "204", description = "Product deleted"),
      new ApiResponse(responseCode = "404", description = "Product not found"),
      new ApiResponse(responseCode = "500", description = "Internal Server Error")
    )
  )
  def deleteProduct(id: Int): Action[AnyContent] = Action.async {
    productDAO.delete(id).map { deletedCount =>
      if (deletedCount > 0) NoContent else NotFound
    }.recover {
      case ex: Exception => InternalServerError(Json.obj("error" -> "Error al eliminar el producto"))
    }
  }
}
