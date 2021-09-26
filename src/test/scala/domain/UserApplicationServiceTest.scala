package domain

import infrastructure.secondary.persistence.InMemoryRepoAdaptor
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import com.github.t3hnar.bcrypt._

import java.util.UUID

class UserApplicationServiceTest extends AnyFlatSpec with Matchers {
  behavior of "User application service"

  val store = new InMemoryRepoAdaptor()
  val service = new UserApplicationService(store)

  val input = CreateUserInput(
    username = "test_username",
    email = "test@test.com",
    plainTextPassword = "password123"
  )

  it should "create and fetch a user" in {
    val createdUser = service.create(input)
    val fetchedUser = service.fetch(createdUser.uuid).get

    fetchedUser mustBe a [UserDTO]
    fetchedUser.username shouldEqual input.username
    fetchedUser.email shouldEqual input.email
    fetchedUser.uuid mustBe a[UUID]
    fetchedUser.created.isBeforeNow mustBe true
    fetchedUser.active mustBe true
  }

  it should "hash password when creating user" in {
    val createdUser = service.create(input)
    val storedUser = store.users
      .filter(_.uuid == createdUser.uuid).headOption.get
    assert(storedUser.passwordHash != "password123")
    assert("password123".isBcryptedSafeBounded(storedUser.passwordHash).get)
  }

  it should "delete a user" in {
    val createdUser = service.create(input)
    val fetchedUser = service.fetch(createdUser.uuid).get
    val deletedUser = service.delete(fetchedUser.uuid).get

    fetchedUser mustBe a [UserDTO]
    service.fetch(deletedUser.uuid) shouldEqual None
  }
}