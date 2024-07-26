package models
 
trait ResponseMessage

final case class ErrorResponse(error: String, details: List[String] = Nil) extends ResponseMessage
final case class UserCreatedMessage(id: Long) extends ResponseMessage
 
