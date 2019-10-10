package de.proxietv.nisledbot.command.staff

import de.proxietv.nisledbot.command.Command
import de.proxietv.nisledbot.database.table.Users
import de.proxietv.nisledbot.utils.Constants
import de.proxietv.nisledbot.utils.DateParser
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.Color
import java.util.concurrent.TimeUnit

class MuteCommand : Command("mute", "Disallow players to write and talk", Category.STAFF, "stumm") {
    override fun execute(args: Array<String>, message: Message) {
        val sender = message.member
        val embedBuilder = getEmbed(message.guild, message.author).setTitle("Mute")
        if (sender != null && args.isNotEmpty()) {
            val target = message.mentionedMembers[0]
            val time = DateParser(args[1]).parse()
            if (isPermittedTime(sender, time)) {
                message.guild.modifyMemberRoles(target, message.guild.getRoleById(Constants.MUT_ROLE_ID)).queue()
                val user = Users.forId(target.idLong)
                transaction {
                    val dif = user.muteTime - System.currentTimeMillis()
                    val timestamp = time + System.currentTimeMillis()
                    user.muteTime = if (dif > 0) timestamp + dif else timestamp
                }

                message.channel.sendMessage(embedBuilder.setColor(Color.GREEN).setDescription("Der User " + target.effectiveName + " wurde für " + TimeUnit.MILLISECONDS.toMinutes(time) + " Minuten gebannt \n(Bessere Angabe der Zeit kommt bald :D)").build()).queue {
                    it.delete().queueAfter(8, TimeUnit.SECONDS)
                }
            } else {
                message.channel.sendMessage(embedBuilder.setColor(Color.RED).setDescription("Du darfst einen User nicht so lange muten").build()).queue {
                    it.delete().queueAfter(6, TimeUnit.SECONDS)
                }
            }
        }
    }

    private fun isPermittedTime(sender: Member, time: Long): Boolean  {
        return (sender.isOwner || sender.hasPermission(Permission.ADMINISTRATOR, Permission.BAN_MEMBERS)) ||
                (sender.roles.contains(sender.jda.getRoleById(Constants.MOD_ROLE_ID)) && time <= LIMIT_MOD) ||
                (sender.roles.contains(sender.jda.getRoleById(Constants.SUP_ROLE_ID)) && time <= LIMIT_SUP)
    }

    companion object {
        const val LIMIT_MOD = 1000L * 60 * 60 * 24 * 5
        const val LIMIT_SUP = 1000L * 60 * 60 * 24 * 1
    }
}