package test

import db.DBMigrationService
import db.DatabaseService
import http.Routes
import io.getquill.Update
import models.AppConfig
import models.User
import services.UserService
import zio._
import zio.config.magnolia._
import zio.config.typesafe.TypesafeConfigProvider

object Fixtures {
  val confLayer = ZLayer(TypesafeConfigProvider.fromResourcePath().load(deriveConfig[AppConfig]))

  val testLayers =
    confLayer >>>
      (DatabaseService.layer >+>
        DBMigrationService.layer >+>
        UserService.layer >+>
        Routes.layer)

  val testUsers = List(
    User(None, "USER1", "TITLE1"),
    User(None, "USER2", "TITLE2"),
    User(None, "USER3", "TITLE3"),
    User(None, "USER4", "TITLE4")
  )

  def prepareUsers() =
    for {
      userService <- ZIO.service[UserService[Task]]
      ids         <- ZIO.foreach(testUsers)(u => userService.create(u))
    } yield ids

  def refreshDB() = {
    for {
      db <- ZIO.service[DatabaseService]
      _ <- {
        import db.quillCtx._
        run(sql"DROP ALL OBJECTS".as[Update[Unit]])
      }
      _ <- ZIO.service[DBMigrationService].map(_.migrate())
     
    } yield ()
  }

}
