package de.proxietv.nisledbot.listener.guild

import de.proxietv.nisledbot.core.NisledBot
import de.proxietv.nisledbot.database.model.User
import de.proxietv.nisledbot.database.table.Users
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.jetbrains.exposed.sql.transactions.transaction

class GuildMemberLeaveListener(bot: NisledBot) : ListenerAdapter() {

    override fun onGuildMemberLeave(event: GuildMemberLeaveEvent) {
        val user = Users.forId(event.member.idLong)
        event.member.roles.forEach { role ->
            if (role != null && transaction { user.roles.filter { it.role == role.idLong }.none() }) {
                user.addRole(role.idLong)
            }
        }

        event.guild.getTextChannelsByName("log", true).forEach { channel ->
            val embedBuilder = EmbedBuilder()
            embedBuilder.setFooter(
                "@" + event.member.user.name + "#" + event.member.user.discriminator,
                event.member.user.effectiveAvatarUrl
            )
            embedBuilder.appendDescription(event.member.effectiveName + " hat den Server verlassen")
            channel.sendMessage(embedBuilder.build()).queue()
        }
    }

}
