package infrastructure.secondary.persistence

import domain.{User, UserStore}

import java.util.UUID
import scala.collection.mutable.ListBuffer

class InMemoryRepoAdaptor extends UserStore {
  var users = new ListBuffer[User]()

  override def create(user: User): User = {
    users += user
    user
  }

  override def fetch(userUUID: UUID): Option[User] = {
    users.filter(_.uuid == userUUID).headOption
  }

  override def delete(userUUID: UUID): Option[User] = {
    fetch(userUUID) match {
      case Some(user: User) => {
        users -= user
        Some(user)
      }
      case _ => None
    }
  }
}
