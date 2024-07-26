package db

import com.zaxxer.hikari.HikariDataSource
import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import models._
import zio.ZIO
import zio.ZLayer

trait DatabaseService {
  val quillCtx: DatabaseService.QuillH2Ctx
}

object DatabaseService {
  type QuillH2Ctx = Quill.H2[SnakeCase]

  val layer = ZLayer {
    for {
      conf     <- ZIO.service[AppConfig]
      hikariDS <- ZIO.fromAutoCloseable(ZIO.succeedBlocking(makeHikariDataSource(conf.db)))
    } yield new DatabaseService {
      val quillCtx: QuillH2Ctx = Quill.H2(SnakeCase, hikariDS)
    }
  }

  def makeHikariDataSource(conf: DatabaseConfig): HikariDataSource = {
    val hikariDS = new HikariDataSource

    hikariDS.setDriverClassName(conf.driver)
    hikariDS.setJdbcUrl(conf.url)
    conf.schema.foreach(hikariDS.setSchema)
    hikariDS.setMaximumPoolSize(conf.maxPoolSize)
    hikariDS.setUsername(conf.user)
    hikariDS.setPassword(conf.password)
    hikariDS.setConnectionTestQuery("SELECT 1")
    hikariDS.setPoolName(conf.poolName)

    hikariDS

  }

}
