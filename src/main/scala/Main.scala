import http.Routes
import http.HttpServer
import models._
import services._
import db._
import zio._
import zio.config.magnolia._
import zio.config.typesafe.TypesafeConfigProvider
import zio.logging.backend.SLF4J

object Main extends ZIOAppDefault {

  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] = zio.Runtime.removeDefaultLoggers >>> SLF4J.slf4j

  val confLayer = ZLayer(TypesafeConfigProvider.fromResourcePath().load(deriveConfig[AppConfig]))

  override def run: ZIO[Environment with ZIOAppArgs with Scope, Any, Any] =
    (for {
      _ <- ZIO.logInfo("Let's go!")
      h2server <- ZIO.acquireRelease(
        ZIO.succeedBlocking(org.h2.tools.Server.createWebServer().start())
      )(s => ZIO.succeedBlocking(s.stop()))
      _ <- ZIO.logInfo(s"h2 console ${h2server.getURL()}")
      _ <- ZIO.service[DBMigrationService].map(_.migrate())
      _ <- ZIO.service[HttpServer].flatMap(_.start())
      _ <- ZIO.never

    } yield {}).provide(
      zio.Scope.default,
      confLayer,
      HttpServer.layer,
      Routes.layer,
      UserService.layer,
      DatabaseService.layer,
      DBMigrationService.layer
      // ,ZLayer.Debug.tree // uncomment to see layers graph
    )

}
