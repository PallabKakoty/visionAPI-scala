package dao

import com.google.cloud.vision.v1.AnnotateImageRequest
import com.google.cloud.vision.v1.AnnotateImageResponse
import com.google.cloud.vision.v1.Feature
import com.google.cloud.vision.v1.Feature.Type
import com.google.cloud.vision.v1.Image
import com.google.cloud.vision.v1.ImageAnnotatorClient
import com.google.protobuf.ByteString
import scala.collection.JavaConversions._

case class ImageDetails(Mid: String, Description: String, Score: String, Topicality: String)

class ImageProcessorDao {
  def imageProcess(imageData: Array[Byte]): List[ImageDetails] = {
    val imageDetailBuff = scala.collection.mutable.ListBuffer.empty[ImageDetails]
    val vision = ImageAnnotatorClient.create
    val imgBytes = ByteString.copyFrom(imageData)
    // Builds the image annotation request
    val requests: java.util.ArrayList[AnnotateImageRequest] = new java.util.ArrayList[AnnotateImageRequest]()
    val img = Image.newBuilder.setContent(imgBytes).build
    val feat = Feature.newBuilder.setType(Type.LABEL_DETECTION).build
    val request: AnnotateImageRequest = AnnotateImageRequest.newBuilder.addFeatures(feat).setImage(img).build
    requests.add(request)
    // Performs label detection on the image file
    val response = vision.batchAnnotateImages(requests)
    val responses: List[AnnotateImageResponse] = response.getResponsesList.toList

    for (res <- responses) {
      if (res.hasError) {
        System.out.printf("Error: %s\n", res.getError.getMessage)
      }
      import scala.collection.JavaConversions._
      for (annotation <- res.getLabelAnnotationsList) {
        imageDetailBuff += ImageDetails(annotation.getMid, annotation.getDescription, annotation.getScore.toString, annotation.getTopicality.toString)
      }
    }
    if (vision != null) vision.close()
    imageDetailBuff.toList
  }
}
