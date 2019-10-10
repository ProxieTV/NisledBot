package de.proxietv.nisledbot.listener.guild

import de.proxietv.nisledbot.core.NisledBot
import de.proxietv.nisledbot.database.table.Users
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.jetbrains.exposed.sql.transactions.transaction

class GuildMemberRoleListener(private val bot: NisledBot) : ListenerAdapter() {

    override fun onGuildMemberRoleAdd(event: GuildMemberRoleAddEvent) {
        val user = Users.forId(event.member.idLong)

        event.roles.forEach { role ->
            if (role != null && transaction { user.roles.filter { it.role == role.idLong }.none() }) {
                user.addRole(role.idLong)
            }
        }
    }

    override fun onGuildMemberRoleRemove(event: GuildMemberRoleRemoveEvent) {
        val user = Users.forId(event.member.idLong)

        event.roles.forEach { role ->
            if (role != null) {
                user.removeRole(role.idLong)
            }
        }
    }
}