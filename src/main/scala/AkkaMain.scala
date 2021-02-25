import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer

import java.net.URLEncoder
import scala.concurrent.Future
import scala.concurrent.duration._

object AkkaMain {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  import system.dispatcher

  val source =
    """
      |object SimpleApp {
      |  val aField = 2
      |
      |  def aMethod(x: Int) = x + 1
      |
      |  def main(args: Array[String]) = {
      |    println(aMethod(aField))
      |  }
      |}
""".stripMargin

  val request = HttpRequest(
    method = HttpMethods.POST,
    uri = "https://api.mocki.io/v1/b043df5a",
    entity = HttpEntity(
      ContentTypes.`application/x-www-form-urlencoded`,
      s"source=${URLEncoder.encode(source.trim, "UTF-8")}&language=Scala&theme=Sunburst"
    )
  )

  def sendRequest(): Future[String] = {
    val responseFuture: Future[HttpResponse] = Http().singleRequest(request)
    val entityFuture: Future[HttpEntity.Strict] = responseFuture.flatMap(response =>
      response.entity.toStrict(2.seconds))
    entityFuture.map(entity => entity.data.utf8String)
  }

  def main(args: Array[String]): Unit = {
    sendRequest().foreach(println)
  }
}