package service.rest.api

import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import io.circe.CursorOp.DownField
import io.circe._
import service.rest.JsonSupport

import scala.util.Try


trait CoinatorApiResponse {
  def _success: Boolean
  def _statusCode: Int

  def _payload: Json

  def getStatusCode: StatusCode = int2StatusCode(_statusCode)

  protected def int2StatusCode(code: Int): StatusCode = StatusCodes.getForKey(code).getOrElse(StatusCodes.InternalServerError)

  def isSuccess: Boolean = _success
}

object CoinatorApiResponse extends JsonSupport {
  final val empty: CoinatorApiResponse = new CoinatorApiResponse {
    val _payload: Json = Json.Null
    val _statusCode: Int = 200
    val _success: Boolean = true
  }
  implicit val encodeApiResponse: Encoder[CoinatorApiResponse] = (a: CoinatorApiResponse) => Json.obj(
    "success" -> Json.fromBoolean(a._success),
    "statusCode" -> Json.fromInt(a._statusCode),
    (if (a.isSuccess) "data" else "error") -> a._payload
  )
}