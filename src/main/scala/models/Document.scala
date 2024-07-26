package models

import java.time.LocalDateTime

case class Document(
                     id: Option[Long],
                     title: String,
                     body: String,
                     createdAt: LocalDateTime,
                     updatedAt: LocalDateTime,
                     authorId: Long
                   )
