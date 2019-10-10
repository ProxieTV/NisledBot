package de.proxietv.nisledbot.database.model

import de.proxietv.nisledbot.database.table.UserGameStats
import de.proxietv.nisledbot.database.table.Users
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class UserGameStat(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserGameStat>(UserGameStats)

    var game by UserGameStats.game
    var user by User referencedOn UserGameStats.user
    var lastTime by UserGameStats.lastTime
    var sessions by UserGameStats.sessions
}