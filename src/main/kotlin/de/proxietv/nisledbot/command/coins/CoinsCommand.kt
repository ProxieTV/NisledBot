package de.proxietv.nisledbot.command.coins

import de.proxietv.nisledbot.command.Command
import de.proxietv.nisledbot.database.table.Users
import net.dv8tion.jda.api.entities.Message
import java.awt.Color

class CoinsCommand : Command("coins", "Show your coins", Category.GENERAL, "money", "bal", "bank", "c") {
    override fun execute(args: Array<String>, message: Message) {
        val user = Users.forId(message.member!!.idLong)
        val embedBuilder = getEmbed(message.guild, message.author)
            .setColor(Color.YELLOW)
            .addField("Kontostand", String.format("%d <a:coin:629226445088227349>", user.coins), true)
        message.channel.sendMessage(embedBuilder.build()).queue()
    }
}