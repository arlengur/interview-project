package db

import models._
import org.flywaydb.core.Flyway
import zio.ZIO
import zio.ZLayer

class DBMigrationService(conf: AppConfig) {

  def migrate(): Unit = {
    val flyway: Flyway = Flyway
      .configure()
      .mixed(true)
      .dataSource(conf.db.url, conf.db.user, conf.db.password)
      .defaultSchema(conf.db.schema.getOrElse("public"))
      .loggers("slf4j")
      .load();

    flyway.migrate();
  }
}

object DBMigrationService {
  val layer = ZLayer.fromZIO {
    for {
      conf <- ZIO.service[AppConfig]
    } yield new DBMigrationService(conf)
  }
}
