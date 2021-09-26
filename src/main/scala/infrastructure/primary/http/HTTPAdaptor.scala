package infrastructure.primary.http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes.{BadRequest, NotFound, OK}
import akka.http.scaladsl.server.Directives
import akka.stream.ActorMaterializer
import domain.{CreateUserInput, UserApplicationService, UserDTO}
import scala.io.StdIn


class HTTPAdaptor(userApplicationService: UserApplicationService)
  extends Directives with JsonSupport {

  implicit val system = ActorSystem("http-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val route = concat(
    path("users") {
      post {
        entity(as[CreateUserInput]) { input =>
          userApplicationService.create(input) match {
            case user: UserDTO => complete(OK, user)
            case _ => complete(BadRequest)
          }
        }
      }
    },
    path("users" / JavaUUID) { userUUID =>
      get {
        userApplicationService.fetch(userUUID) match {
          case Some(user: UserDTO) => complete(OK, user)
          case _ => complete(NotFound)
        }
      }
    },
    path("users" / JavaUUID) { userUUID =>
      delete {
        headerValueByName("X-API-Key") { apiKey =>
          authorize(superficialAuthorization(apiKey)) {
            userApplicationService.delete(userUUID) match {
              case Some(deletedUser: UserDTO) => complete(OK, deletedUser)
              case _ => complete(NotFound)
            }
          }
        }
      }
    }
  )

  def start = {
    val bindingFuture = Http()
      .newServerAt("localhost", 8080)
      .bind(route)

    println(s"Server online at http://localhost:8080/")
    println("Press RETURN to stop...")

    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}

