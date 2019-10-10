package de.proxietv.nisledbot.listener.guild

import de.proxietv.nisledbot.core.NisledBot
import de.proxietv.nisledbot.database.table.Users
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.internal.utils.PermissionUtil
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.format.DateTimeFormatter

class GuildMemberJoinListener(private val bot: NisledBot) : ListenerAdapter() {

    override fun onGuildMemberJoin(event: GuildMemberJoinEvent) {
        val member = event.member
        val guild = event.guild

        println("test")

        event.guild.getTextChannelsByName("log", true).forEach { channel ->
            val logBuilder = EmbedBuilder()
            logBuilder.setFooter(
                "@" + member.user.name + "#" + member.user.discriminator,
                member.user.effectiveAvatarUrl
            )
            logBuilder.setThumbnail(member.user.effectiveAvatarUrl)
            logBuilder.appendDescription("Erstelldatum: " + event.user.timeCreated.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")) + "\n")
            logBuilder.appendDescription("Standart Avatar: " + (member.user.avatarUrl == null) + "\n")
            channel.sendMessage(logBuilder.build()).queue()
        }

        val user = Users.forId(member.idLong)
        val roles = mutableListOf<Role>()
        transaction {
            user.roles.forEach {
                val role = guild.getRoleById(it.role)
                if (role != null && PermissionUtil.canInteract(guild.selfMember, role)) {
                    roles.add(role)
                }
            }
        }
        guild.modifyMemberRoles(member, roles).queue()
    }
}
