package http

import cats.data.Validated.Invalid
import cats.data.Validated.Valid
import models._
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.dsl.Http4sDsl
import services._
import zio.Task
import zio.ZIO
import zio.ZLayer
import zio.interop.catz._
import JsonCodecs._

class Routes(userService: UserService[Task]) extends Http4sDsl[Task] {

  object LimitParam  extends OptionalQueryParamDecoderMatcher[Int]("limit")
  object OffsetParam extends OptionalQueryParamDecoderMatcher[Int]("offset")

  val routes: HttpRoutes[Task] =
    HttpRoutes.of[Task] {

      case GET -> Root / "healthCheck" =>
        Ok("I'm ok")

      case GET -> Root / "users" :? LimitParam(limit) :? OffsetParam(offset) =>
        userService.list(limit.getOrElse(10), offset.getOrElse(0)).flatMap(users => Ok(users))

      case GET -> Root / "user" / LongVar(id) =>
        userService.get(id).flatMap {
          case Some(user) => Ok(user)
          case _          => NotFound(ErrorResponse("User not found"))
        }

      case req @ POST -> Root / "user" =>
        for {
          user <- req.as[User]
          response <- userService.validate(user).flatMap {
            case Valid(user)  => userService.create(user).flatMap(id => Ok(UserCreatedMessage(id)))
            case Invalid(err) => BadRequest(err)
          }
        } yield response

      case req @ PUT -> Root / "user" / LongVar(id) =>
        for {
          user <- req.as[User]
          response <- userService.validate(user).flatMap {
            case Valid(user)  => userService.update(id, user).flatMap(_ => NoContent())
            case Invalid(err) => BadRequest(err)
          }
        } yield response

      case DELETE -> Root / "user" / LongVar(id) =>
        userService.delete(id).flatMap(_ => NoContent())

    }
}

object Routes {
  val layer = ZLayer {
    for {
      userService <- ZIO.service[UserService[Task]]
    } yield new Routes(userService)
  }
}
