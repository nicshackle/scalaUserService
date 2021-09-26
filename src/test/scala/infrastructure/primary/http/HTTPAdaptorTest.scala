package infrastructure.primary.http

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.server._
import akka.http.scaladsl.model.headers.RawHeader
import domain.{CreateUserInput, UserApplicationService, UserDTO}
import infrastructure.secondary.persistence.InMemoryRepoAdaptor

import java.util.UUID

class HTTPAdaptorTest
  extends AnyFlatSpec
    with Matchers
    with ScalatestRouteTest
    with JsonSupport {
  behavior of "HTTP Adaptor"

  val store = new InMemoryRepoAdaptor()
  val service = new UserApplicationService(store)
  val route = new HTTPAdaptor(service).route

  val username = "test_username"
  val email = "test@test.com"
  val password = "password123"

  val apiKey = sys.env.get("API_KEY").getOrElse(
    throw new RuntimeException("HTTP tests require an API_KEY to be set")
  )

  def createTestUser: UserDTO = service.create(CreateUserInput(
    username, email, password
  ))

  it should "create a user" in {
    val createUserJson =
      s"""{
      "username": "$username",
      "email": "$email",
      "plainTextPassword": "$password"
      }"""

    Post("/users",
      HttpEntity(ContentTypes.`application/json`, createUserJson)) ~>
      route ~> check {
      status shouldEqual StatusCodes.OK
      val response = responseAs[UserDTO]
      assert(response.uuid.isInstanceOf[UUID])
      response.username shouldEqual username
      response.email shouldEqual email
    }
  }

  it should "fetch a created user" in {
    val uuid = createTestUser.uuid
    Get(s"/users/$uuid") ~> route ~> check {
      status shouldEqual StatusCodes.OK
      val response = responseAs[UserDTO]
      assert(response.uuid.isInstanceOf[UUID])
      response.username shouldEqual username
      response.email shouldEqual email
      response.uuid shouldEqual uuid
    }
  }

  it should "delete created user" in {
    val uuid = createTestUser.uuid
    Delete(s"/users/$uuid") ~>
      RawHeader("X-API-Key", apiKey) ~>
      route ~> check {
      status shouldEqual StatusCodes.OK
      val response = responseAs[UserDTO]
      assert(response.uuid.isInstanceOf[UUID])
      response.username shouldEqual username
      response.email shouldEqual email
      response.uuid shouldEqual uuid
    }
  }

  it should "fail to find or delete non-existent user" in {
    Get(s"/users/${UUID.randomUUID()}") ~> route ~> check {
      status shouldEqual StatusCodes.NotFound
    }
    Delete(s"/users/${UUID.randomUUID()}") ~>
      RawHeader("X-API-Key", apiKey) ~>
      route ~> check {
      status shouldEqual StatusCodes.NotFound
    }
  }

  it should "reject un-authorized 'delete' request" in {
    Delete(s"/users/${UUID.randomUUID()}") ~>
      route ~> check {
      rejection shouldEqual MissingHeaderRejection("X-API-Key")
    }

    Delete(s"/users/${UUID.randomUUID()}") ~>
      RawHeader("X-API-Key", "not-the-right-key!") ~>
      route ~> check {
      rejection shouldEqual AuthorizationFailedRejection
    }
  }

}
