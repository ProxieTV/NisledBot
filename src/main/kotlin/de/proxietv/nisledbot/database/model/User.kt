package de.proxietv.nisledbot.database.model

import de.proxietv.nisledbot.database.table.UserRoles
import de.proxietv.nisledbot.database.table.Users
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.ThreadLocalRandom

class User(id: EntityID<Long>) : Entity<Long>(id) {
    companion object : EntityClass<Long, User>(Users)

    var level by Users.level
    var xp by Users.xp
    var coins by Users.coins
    var lastMessage by Users.lastMessage
    var muteTime by Users.muteTime

    val roles by UserRole referrersOn UserRoles.user

    fun addXP(count: Int): Boolean = transaction {
        this@User.xp = ThreadLocalRandom.current().nextInt(count) + count
        val reach = 1000 * level * 1.2
        if (this@User.xp >= reach && reach != 0.0) {
            this@User.xp = 0
            this@User.level += 1
            return@transaction true
        } else if (this@User.xp >= 1000 && level == 0) {
            level += 1
            return@transaction true
        }
        return@transaction false
    }

    fun addRole(roleId: Long) = transaction {
        UserRole.new {
            this.user = this@User
            this.role = roleId
        }
    }

    fun removeRole(roleId: Long) = transaction {
        UserRoles.deleteWhere {
            UserRoles.user eq this@User.id and (UserRoles.role eq roleId)
        }
    }
}