package models

final case class AppConfig(
  http: HttpConfig,
  db: DatabaseConfig
)

final case class HttpConfig(
  host: String,
  port: Int
)

final case class DatabaseConfig(
  driver: String,
  url: String,
  user: String,
  password: String,
  poolName: String,
  maxPoolSize: Int,
  threadPoolSize: Int,
  schema: Option[String] = None
)
