# Scala User Service

## Running & Testing

Run:

`API_KEY=foo sbt run`

Test:

`API_KEY=foo sbt test`

## Using

### Create
```bash
curl -X POST -H "Content-type: application/json" -d '{ "username": "username", "email": "test@test.com", "plainTextPassword": "password123"  }' 'http://localhost:8080/users'
```

### Fetch
```bash
curl -X GET 'http://localhost:8080/users/{uuid}'
```

### Delete
```bash
curl -X DELETE -H "X-API-Key: admin" 'http://localhost:8080/users/uuid'
```

## More info

The service follows [hexagonal architecture](https://medium.com/the-software-architecture-chronicles/ddd-hexagonal-onion-clean-cqrs-how-i-put-it-all-together-f2590c0aa7f6).
In short, that means that business logic/"the domain" (user management) is separate from infrastructure (DBs, HTTP APIs, etc)
(The benefit being that infrastructure is decoupled from business logic).

In the case of this project, the infrastructure consists of an RESTful HTTP API, and a simple in-memory store (not production-worthy!)
