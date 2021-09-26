package domain

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import org.scalatest.matchers.should.Matchers

import java.util.UUID

class UserTest extends AnyFlatSpec with Matchers {
  behavior of "User model"

  it should "instantiate with correct fields and defaults" in {
    val user = User(
      username = "test_username",
      email = "test@test.com",
      passwordHash = "test_hash"
    )

    user.uuid mustBe a[UUID]
    user.username shouldEqual "test_username"
    user.email shouldEqual "test@test.com"
    user.passwordHash shouldEqual "test_hash"
    user.created.isBeforeNow mustBe true
    user.active shouldEqual true
  }

}
