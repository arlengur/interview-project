package test

import models.JsonCodecs._
import models._
import org.http4s.Method
import org.http4s.Request
import org.http4s.Status._
import org.http4s.Uri
import services._
import zio._
import zio.interop.catz._
import zio.test._
import http.Routes

object UserAPITest extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] = (
    suite("User API")(
      test("Find user by id")(
        for {
          ids <- Fixtures.prepareUsers()
          testId = ids(0)
          routes <- ZIO.service[Routes].map(_.routes)
          req = Request[Task](
            method = Method.GET,
            uri = Uri.unsafeFromString(s"user/${testId}")
          )
          response <- routes.run(req).value.map(_.get)
          result   <- response.as[User]
        } yield assertTrue(
          response.status == Ok,
          result.id.contains(testId)
        )
      ),
      test("Get all users")(
        for {
          ids    <- Fixtures.prepareUsers()
          routes <- ZIO.service[Routes].map(_.routes)
          req = Request[Task](
            method = Method.GET,
            uri = Uri.unsafeFromString("/users")
          )
          response <- routes.run(req).value.map(_.get)
          result   <- response.as[List[User]]
        } yield assertTrue(
          response.status == Ok,
          result.flatMap(_.id).toSet == ids.toSet
        )
      ),
      test("Create new user")(
        for {
          routes <- ZIO.service[Routes].map(_.routes)
          newUser = User(None, "Test user", "Test title")
          req = Request[Task](
            method = Method.POST,
            uri = Uri.unsafeFromString("/user"),
            body = userEntityEncoder.toEntity(newUser).body
          )
          response <- routes.run(req).value.map(_.get)
          result   <- response.as[UserCreatedMessage]
          newId = result.id
          userService <- ZIO.service[UserService[Task]]
          savedUser   <- userService.get(newId)
        } yield assertTrue(
          response.status == Ok,
          savedUser.contains(newUser.copy(id = Some(newId)))
        )
      ),
      test("Update user")(
        for {
          ids         <- Fixtures.prepareUsers()
          userService <- ZIO.service[UserService[Task]]
          testId = ids(0)
          userBefore <- userService.get(testId).map(_.get)
          routes     <- ZIO.service[Routes].map(_.routes)
          userToUpdate = userBefore.copy(name = "UPDATED USER")
          req = Request[Task](
            method = Method.PUT,
            uri = Uri.unsafeFromString(s"/user/${testId}"),
            body = userEntityEncoder.toEntity(userToUpdate).body
          )
          response  <- routes.run(req).value.map(_.get)
          userAfter <- userService.get(testId)
        } yield assertTrue(
          response.status == NoContent,
          userAfter.contains(userToUpdate)
        )
      ),
      test("Delete user")(
        for {
          ids         <- Fixtures.prepareUsers()
          routes      <- ZIO.service[Routes].map(_.routes)
          userService <- ZIO.service[UserService[Task]]
          id = ids(0)
          req = Request[Task](
            method = Method.DELETE,
            uri = Uri.unsafeFromString(s"/user/$id")
          )
          response   <- routes.run(req).value.map(_.get)
          usersAfter <- userService.list(1000, 0)
        } yield assertTrue(
          response.status == NoContent,
          usersAfter.filter(_.id.contains(id)).isEmpty
        )
      )
    ) @@ TestAspect.before(Fixtures.refreshDB())
      @@ TestAspect.sequential
  ).provideLayer(Fixtures.testLayers)
}
