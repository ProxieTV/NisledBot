package de.proxietv.nisledbot.database.table

import org.jetbrains.exposed.dao.IntIdTable

object UserRoles : IntIdTable("user_roles") {
    val user = reference("user", Users).primaryKey(2)
    val role = long("roleId")
}