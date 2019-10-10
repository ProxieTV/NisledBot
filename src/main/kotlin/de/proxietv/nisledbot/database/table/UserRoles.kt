package de.proxietv.nisledbot.database.table

import de.proxietv.nisledbot.database.model.UserRole
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

object UserRoles : IntIdTable("user_roles") {
    val user = reference("user", Users).primaryKey(2)
    val role = long("roleId")

    fun forUserRole(userId: Long, roleId: Long) = transaction {
        return@transaction UserRole.find { user eq userId and (role eq roleId) }
    }
}