package domain

import org.joda.time.DateTime
import com.github.t3hnar.bcrypt._
import java.util.UUID

class UserApplicationService(userStore: UserStore) {
  def create(input: CreateUserInput): UserDTO = {

    val user = User(
      username = input.username,
      email = input.email,
      passwordHash = input.plainTextPassword.bcryptSafeBounded.getOrElse(
        throw new IllegalArgumentException("invalid password")
      )
    )

    toDto(userStore.create(user))
  }

  def fetch(userUUID: UUID): Option[UserDTO] = {
    userStore.fetch(userUUID).map(toDto(_))
  }

  def delete(userUUID: UUID): Option[UserDTO] = {
    userStore.delete(userUUID).map(toDto(_))
  }

  private def toDto(user: User): UserDTO = UserDTO(
    uuid = user.uuid,
    username = user.username,
    email = user.email,
    created = user.created,
    active = user.active
  )
}

case class CreateUserInput(
                          username: String,
                          email: String,
                          plainTextPassword: String
                          )

case class UserDTO(
                    uuid: UUID,
                    username: String,
                    email: String,
                    created: DateTime,
                    active: Boolean
                  )