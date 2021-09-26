package domain

import java.util.UUID

trait UserStore {
  def create(user: User): User
  def fetch(userUUID: UUID): Option[User]
  def delete(userUUID: UUID): Option[User]
}