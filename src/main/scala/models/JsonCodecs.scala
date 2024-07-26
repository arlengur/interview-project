package models

import io.circe.Codec
import io.circe.Decoder
import io.circe.generic.semiauto.deriveCodec
import models.ErrorResponse
import models.User
import models.UserCreatedMessage
import org.http4s.circe._
import zio.Task
import zio.interop.catz._

object JsonCodecs {
  implicit val codec: Codec[User]                          = deriveCodec[User]
  implicit val codecD: Codec[Document]                          = deriveCodec[Document]
  implicit val errorMessageCodec: Codec[ErrorResponse]     = deriveCodec[ErrorResponse]
  implicit val userCreatedCodec: Codec[UserCreatedMessage] = deriveCodec[UserCreatedMessage]
  implicit val docCreatedCodec: Codec[DocCreatedMessage] = deriveCodec[DocCreatedMessage]

  implicit val userEntityDecoder         = jsonOf[Task, User]
  implicit val userEntityEncoder         = jsonEncoderOf[Task, User]

  implicit val docEntityDecoder         = jsonOf[Task, Document]
  implicit val docEntityEncoder         = jsonEncoderOf[Task, Document]

  implicit val userCreatedMessageDecoder = jsonOf[Task, UserCreatedMessage]

  implicit def listEntityDecoder[T: Decoder] = jsonOf[Task, List[T]]
}
