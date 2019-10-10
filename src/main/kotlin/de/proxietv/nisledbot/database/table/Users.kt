package de.proxietv.nisledbot.database.table

import de.proxietv.nisledbot.database.model.User
import org.jetbrains.exposed.dao.IdTable
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

object Users : IdTable<Long>("user") {
    override val id = long("userId").primaryKey().entityId()

    val level = integer("level").default(0)
    val xp = integer("xp").default(0)
    val coins = integer("coins").default(1000)
    val lastMessage = long("lastMessage").default(Instant.EPOCH.epochSecond)
    val muteTime = long("muteTime").default(Instant.EPOCH.epochSecond)

    fun forId(id: Long, init: User.() -> Unit = { }) = transaction {
        return@transaction User.findById(id)?: User.new(id, init)
    }
}