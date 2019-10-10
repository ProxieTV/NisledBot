package de.proxietv.nisledbot.database.table

import de.proxietv.nisledbot.database.model.UserGameStat
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

object UserGameStats : IntIdTable("user_game_stats") {
    val game = varchar("game", 25).index()
    val user = reference("user", Users).index()
    val lastTime = long("last_time").default(0L)
    val sessions = integer("sessions").default(0)

    fun forGameUser(game: String, userId: Long, init: UserGameStat.() -> Unit = { }) = transaction {
        val result = UserGameStat.find { (this@UserGameStats.game eq game) and (user eq userId) }
        return@transaction if (result.count() > 0) result.first() else UserGameStat.new {
            this.user = Users.forId(userId)
            this.game = game
            init()
        }
    }
}