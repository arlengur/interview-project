package services

import cats.implicits._
import cats.data.Validated
import db.DatabaseService
import db.DatabaseService.QuillH2Ctx
import io.getquill.{Delete, Insert, Query, Update}
import models.{Document, ErrorResponse, User}
import zio.interop.catz._
import zio.{Task, ZIO, ZLayer}

trait DocumentService[F[_]] {
    def get(id: Long): F[Option[Document]]
    def create(doc: Document): F[Long]
    def update(id: Long, doc: Document): F[Unit]
    def delete(id: Long): F[Unit]
    def list(limit: Int, offset: Int): F[List[Document]]
    def listU(userId: Long): F[List[Document]]
    def validate(doc: Document): F[Validated[ErrorResponse, Document]]


  }

  class DocumentServiceImpl(quillCtx: QuillH2Ctx) extends DocumentService[Task] {
    import quillCtx._

    override def get(id: Long): Task[Option[Document]] =
      run(sql"SELECT id, title, body, createdAt, updatedAt, authorId FROM document WHERE id = ${lift(id)}".as[Query[Document]]).map(_.headOption)

    override def create(doc: Document): Task[Long] =
      run(
            sql"""INSERT INTO document (title, body, createdAt, updatedAt, authorId)
             VALUES (${lift(doc.title)}, ${lift(doc.body)}, ${lift(doc.createdAt)}, ${lift(doc.updatedAt)}, ${lift(doc.authorId)})"""
              .as[Insert[Document]].returningGenerated(_.id)
          ).map(_.get)

    override def update(id: Long, doc: Document): Task[Unit] =
          run(
            sql"""UPDATE document SET (title, body, createdAt, updatedAt, authorId) =
                 (${lift(doc.title)}, ${lift(doc.body)}, ${lift(doc.createdAt)}, ${lift(doc.updatedAt)}, ${lift(doc.authorId)})
                 WHERE id=${lift(id)}"""
              .as[Update[Unit]]
          ).unit

    override def delete(id: Long): Task[Unit] =
      run(sql"DELETE FROM document WHERE id=${lift(id)}".as[Delete[Unit]]).unit

    override def list(limit: Index, offset: Index): Task[List[Document]] =
      run(sql"SELECT id, title, body, createdAt, updatedAt, authorId FROM document ORDER BY id LIMIT ${lift(limit)} OFFSET ${lift(offset)}".as[Query[Document]])

    override def listU(userId: Long): Task[List[Document]] =
      run(sql"SELECT id, title, body, createdAt, updatedAt, authorId FROM document WHERE authorId=${lift(userId)}".as[Query[Document]])

    override def validate(doc: Document): Task[Validated[ErrorResponse, Document]] =
          if (doc.title.nonEmpty)
            ZIO.succeed(doc.valid)
          else
            ZIO.succeed(ErrorResponse("Doc title should not be empty").invalid)
  }

  object DocumentService {
    val layer = ZLayer {
      for {
        db <- ZIO.service[DatabaseService]
      } yield new DocumentServiceImpl(db.quillCtx)
    }
  }

