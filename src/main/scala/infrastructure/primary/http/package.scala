package infrastructure.primary

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import domain.{CreateUserInput, UserDTO}
import org.joda.time.DateTime
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat}

import java.util.UUID

package object http {
  trait CustomJsonProtocol extends DefaultJsonProtocol {
    implicit val jodaFormat: JsonFormat[DateTime] =
      new JsonFormat[DateTime] {
      override def read(json: JsValue): DateTime =
        json match {
          case JsString(str) => DateTime.parse(str)
          case _ =>
            throw new IllegalArgumentException(
              s"$json is not a valid DateTime"
            )
        }

      override def write(obj: DateTime): JsValue =
        JsString(obj.toDateTimeISO.toString)
    }

    implicit val uuidFormat: JsonFormat[UUID] = new JsonFormat[UUID] {
      override def read(json: JsValue): UUID = json match {
        case JsString(str) => UUID.fromString(str)
        case _ =>
          throw new IllegalArgumentException(s"$json is not a valid UUID")
      }

      override def write(obj: UUID): JsValue = JsString(obj.toString)
    }
  }

  trait JsonSupport extends SprayJsonSupport with CustomJsonProtocol {
    implicit val userDTOFormat = jsonFormat5(UserDTO)
    implicit val createUserInputFormat = jsonFormat3(CreateUserInput)
  }

  def superficialAuthorization(key: String): Boolean =
    (key == sys.env.get("API_KEY").getOrElse(
      throw new RuntimeException("API key is missing")
    ))

}
