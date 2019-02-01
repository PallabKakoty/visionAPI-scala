package controllers

import javax.inject._
import play.api.Logger
import java.util.Base64
import globals.Configurations
import play.api.mvc._
import java.io.File
import java.nio.file.{Files, Paths}
import dao.{ImageDetails, ImageProcessorDao}
import play.api.libs.json.{JsValue, Json}
import utils.Tools

@Singleton
class HomeController @Inject()(configurations: Configurations, imageProcessorDao: ImageProcessorDao, tools: Tools) extends Controller {

  implicit val logAddress = "controllers.HomeController"
  val csvType = Array("text/csv","application/csv","application/vnd.ms-excel","application/x-filler","text/comma-separated-values")

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def upload = Action(parse.multipartFormData) { request =>
    lazy val imgName: String = "image/upload/"+tools.TokenGenerateBySize(6) + ".jpg"
    val res: Seq[ImageDetails] = request.body.dataParts("imgData").flatMap { img =>
      val decode = Base64.getDecoder.decode(img.split(",")(1))
      val currentDirectory: File = new File(new java.io.File(".").getCanonicalPath + "/public/" + imgName)
      val destinationFile = Paths.get(currentDirectory.getAbsolutePath)
      Files.write(destinationFile, decode)
      imageProcessorDao.imageProcess(Files.readAllBytes(destinationFile))
    }
    implicit val img = play.api.libs.json.Json.format[ImageDetails]
    if (res.nonEmpty) {
      val jsonData: JsValue = Json.toJson(res)
      Ok(views.html.viewdetails(jsonData, imgName, res.toList))
    } else NotFound(Json.obj("err"->"Something wrong. Unable to process"))
  }
}
