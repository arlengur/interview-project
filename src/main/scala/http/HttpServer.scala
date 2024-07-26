package http

import com.comcast.ip4s.Host
import com.comcast.ip4s.Port
import models.JsonCodecs._
import models._
import org.http4s.MessageFailure
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.dsl.Http4sDsl
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import zio._
import zio.interop.catz._

class HttpServer(conf: AppConfig, routes: Routes) extends Http4sDsl[Task] {
 
  private val server = EmberServerBuilder
    .default[Task]
    .withHost(Host.fromString(conf.http.host).get)
    .withPort(Port.fromInt(conf.http.port).get)
    .withHttpApp(Router("/" -> routes.routes).orNotFound)
    .withErrorHandler {
      case err: MessageFailure => BadRequest(ErrorResponse(err.getMessage))
      case err =>
        ZIO.logErrorCause(Cause.fail(err)) *>
          InternalServerError(ErrorResponse(err.getMessage))
    }
    .build

  def start() = for {
    task <- server.use { s =>
      ZIO.logInfo(s"Starting server at ${s.address}") *>
        ZIO.never
    }
  } yield task

}

object HttpServer {
  val layer = ZLayer {
    for {
      routes <- ZIO.service[Routes]
      conf   <- ZIO.service[AppConfig]
    } yield new HttpServer(conf, routes)
  }
}
