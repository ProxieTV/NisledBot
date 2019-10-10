package de.proxietv.nisledbot.command.staff

import de.proxietv.nisledbot.command.Command
import de.proxietv.nisledbot.utils.Constants
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message

class MuteCommand : Command("mute", "Disallow players to write and talk", Category.STAFF, "stumm") {
    override fun execute(args: Array<String>, message: Message) {
        val sender = message.member
        if (sender != null && args.isNotEmpty()) {
            val target = message.mentionedMembers[0]
/*            val time =
            if (sender.roles.contains(message.jda.getRoleById(Constants.MOD_ROLE_ID))) {

            }*/
        }
    }

/*    private fun isPermittedTime(sender: Member, time: Int): Boolean  {
        if (sender.roles.contains(sender.jda.getRoleById(Constants.MOD_ROLE_ID)) && time >= )
    }*/
}