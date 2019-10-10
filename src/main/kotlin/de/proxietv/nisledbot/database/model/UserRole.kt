package de.proxietv.nisledbot.database.model

import de.proxietv.nisledbot.database.table.UserRoles
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class UserRole(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserRole>(UserRoles)

    var user by User referencedOn UserRoles.user
    var role by UserRoles.role
}