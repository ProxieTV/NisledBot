package de.proxietv.nisledbot.command.general

import de.proxietv.nisledbot.command.Command
import net.dv8tion.jda.api.entities.Message

class HelpCommand : Command("help", "Zeigt diese Hilfe", Category.GENERAL, "hilfe") {
    override fun execute(args: Array<String>, message: Message) {
        val embedBuilder = getEmbed(message.guild, message.author)
        val commands = bot?.commandManager?.getAvailableCommands() ?: emptySet()

        embedBuilder.setTitle("Command Übersicht")
        embedBuilder.setDescription("Klicke dich mit den Emotes durch die Kategorien")

        embedBuilder.addField("General Commands", "",true)

        commands.map { category == Category.GENERAL }

        message.channel.sendMessage(embedBuilder.build()).queue()
    }
}