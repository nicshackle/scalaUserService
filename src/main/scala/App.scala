import domain.UserApplicationService
import infrastructure.primary.http.HTTPAdaptor
import infrastructure.secondary.persistence.InMemoryRepoAdaptor

object ScalaUsers extends App {
  val store = new InMemoryRepoAdaptor()
  val service = new UserApplicationService(store)
  val facade = new HTTPAdaptor(service)

  facade.start
}