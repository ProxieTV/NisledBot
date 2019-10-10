package de.proxietv.nisledbot.command.staff

import de.proxietv.nisledbot.command.Command
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Message
import org.apache.commons.lang3.math.NumberUtils

class PurgeCommand : Command("purge", "Delete messages from history", Category.STAFF, "delete", "clear") {
    override fun execute(args: Array<String>, message: Message) {
        if (message.member!!.hasPermission(Permission.MESSAGE_MANAGE)) {
            if (args.isNotEmpty()) {
                if (NumberUtils.isParsable(args[0])) {
                    val mentions = message.getMentions().map { member -> member.idLong }.toMutableList()
                    if (args.size > 1 && NumberUtils.isParsable(args[1])) mentions.add(args[1].toLong())

                    message.channel.history.retrievePast(args[0].toInt()).queue {
                        it.filter { msg -> if (mentions.isNotEmpty()) mentions.contains(msg.author.idLong) else true }.forEach { msg -> msg.delete().queue() }
                    }
                }
            }
            message.delete().queue()
        }
    }
}