package services

import cats.data.Validated
import cats.implicits._
import db.DatabaseService
import io.getquill.Delete
import io.getquill.Insert
import io.getquill.Query
import io.getquill.Update
import models._
import zio._

import DatabaseService.QuillH2Ctx

trait UserService[F[_]] {
  def get(id: Long): F[Option[User]]
  def create(user: User): F[Long]
  def update(id: Long, user: User): F[Unit]
  def delete(id: Long): F[Unit]
  def list(limit: Int, offset: Int): F[List[User]]
  def validate(user: User): F[Validated[ErrorResponse, User]]

}

class UserServiceImpl(quillCtx: QuillH2Ctx) extends UserService[Task] {
  import quillCtx._

  override def get(id: Long): Task[Option[User]] =
    run(sql"SELECT id, name, title FROM user WHERE id = ${lift(id)}".as[Query[User]]).map(_.headOption)

  override def create(user: User): Task[Long] =
    run(
      sql"INSERT INTO user (name, title) VALUES (${lift(user.name)}, ${lift(user.title)})"
        .as[Insert[User]].returningGenerated(_.id)
    ).map(_.get)

  override def update(id: Long, user: User): Task[Unit] =
    run(
      sql"UPDATE user SET (name, title) = (${lift(user.name)}, ${lift(user.title)}) WHERE id=${lift(id)}"
        .as[Update[Unit]]
    ).unit

  override def delete(id: Long): Task[Unit] =
    run(sql"DELETE FROM user WHERE id=${lift(id)}".as[Delete[Unit]]).unit

  override def list(limit: Int, offset: Int): Task[List[User]] =
    run(sql"SELECT id, name, title FROM user ORDER BY id LIMIT ${lift(limit)} OFFSET ${lift(offset)}".as[Query[User]])

  override def validate(user: User): Task[Validated[ErrorResponse, User]] =
    if (user.name.nonEmpty)
      ZIO.succeed(user.valid)
    else
      ZIO.succeed(ErrorResponse("User should not be empty").invalid)

}

object UserService {
  val layer = ZLayer {
    for {
      db <- ZIO.service[DatabaseService]
    } yield new UserServiceImpl(db.quillCtx)
  }
}
