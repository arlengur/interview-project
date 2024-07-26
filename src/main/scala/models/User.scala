package models

final case class User(
  id: Option[Long],
  name: String,
  title: String
)

 