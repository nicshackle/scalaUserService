package domain

import org.joda.time.DateTime

import java.util.UUID

case class User(
                 uuid: UUID = UUID.randomUUID(),
                 username: String,
                 email: String,
                 passwordHash: String,
                 active: Boolean = true,
                 created: DateTime = DateTime.now()
               )
