package globals

import java.nio.file.{Files, Paths}
import com.google.cloud.vision.v1.AnnotateImageRequest
import com.google.cloud.vision.v1.AnnotateImageResponse
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse
import com.google.cloud.vision.v1.EntityAnnotation
import com.google.cloud.vision.v1.Feature
import com.google.cloud.vision.v1.Feature.Type
import com.google.cloud.vision.v1.Image
import com.google.cloud.vision.v1.ImageAnnotatorClient
import com.google.protobuf.ByteString
import java.nio.file.{Files, Paths}
import scala.collection.JavaConversions._
import com.google.cloud.vision.v1.Feature.Type
import com.google.cloud.vision.v1._
import com.google.protobuf.ByteString

class ImageProcessor {
  def imageProcess(args: String*): List[AnnotateImageResponse] = {
    val vision = ImageAnnotatorClient.create
    val fileName = "wakeupcat.jpg"
    // Reads the image file into memory
    val path = Paths.get(fileName)
    val data = Files.readAllBytes(path)
    val imgBytes = ByteString.copyFrom(data)
    // Builds the image annotation request
    val requests: java.util.ArrayList[AnnotateImageRequest] = new java.util.ArrayList[AnnotateImageRequest]()
    val img = Image.newBuilder.setContent(imgBytes).build
    val feat = Feature.newBuilder.setType(Type.LABEL_DETECTION).build
    val request: AnnotateImageRequest = AnnotateImageRequest.newBuilder.addFeatures(feat).setImage(img).build
    val res = requests.add(request)
    // Performs label detection on the image file
    val response = vision.batchAnnotateImages(requests)
    val responses: List[AnnotateImageResponse] = response.getResponsesList.toList
    responses
  }

  @throws[Exception]
  def imageProcessNew(args: String*): Unit = { // Instantiates a client
    try {
      val vision = ImageAnnotatorClient.create
      try { // The path to the image file to annotate
        val fileName = "wakeupcat.jpg"
        // Reads the image file into memory
        val path = Paths.get(fileName)
        val data = Files.readAllBytes(path)
        val imgBytes = ByteString.copyFrom(data)
        // Builds the image annotation request
        val requests: java.util.ArrayList[AnnotateImageRequest] = new java.util.ArrayList[AnnotateImageRequest]()
        val img = Image.newBuilder.setContent(imgBytes).build
        val feat = Feature.newBuilder.setType(Type.LABEL_DETECTION).build
        val request: AnnotateImageRequest = AnnotateImageRequest.newBuilder.addFeatures(feat).setImage(img).build
        requests.add(request)
        // Performs label detection on the image file
        val response = vision.batchAnnotateImages(requests)
        val responses: List[AnnotateImageResponse] = response.getResponsesList.toList

        import scala.collection.JavaConversions._
        for (res <- responses) {
          if (res.hasError) {
            System.out.printf("Error: %s\n", res.getError.getMessage)
            return
          }
          import scala.collection.JavaConversions._

          for (annotation <- res.getLabelAnnotationsList) {
            println(annotation)
            //annotation.getAllFields.forEach((k, v) => System.out.printf("%s : %s\n", k, v.toString))
          }
        }
      } finally if (vision != null) vision.close()
    }
  }

}



