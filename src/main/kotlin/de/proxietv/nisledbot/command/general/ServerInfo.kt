package de.proxietv.nisledbot.command.general

import de.proxietv.nisledbot.command.Command
import de.proxietv.nisledbot.utils.StringUtils
import net.dv8tion.jda.api.entities.Message
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


class ServerinfoCommand : Command("serverinfo", "Listet einige Informationen über den Server", Category.GENERAL) {

    override fun execute(args: Array<String>, message: Message) {
        val embedBuilder = getEmbed(message.guild, message.author)
        embedBuilder.setTitle(message.guild.name, "https://c0debase.de")
        embedBuilder.setThumbnail(message.guild.iconUrl)
        embedBuilder.addField("Erstellt am", message.guild.timeCreated.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")), true)
        embedBuilder.addField("Region", message.guild.regionRaw, true)
        embedBuilder.addField("Mitglieder", message.guild.members.size.toString(), true)
        embedBuilder.addField("Text Channels", message.guild.textChannels.size.toString(), true)
        embedBuilder.addField("Voice Channels", message.guild.voiceChannels.size.toString(), true)
        embedBuilder.addField("Rollen", message.guild.roles.size.toString(), true)
        embedBuilder.addField("Owner", StringUtils.replaceCharacter(message.guild.owner!!.user.name) + "#" + message.guild.owner!!.user.discriminator, true)
        embedBuilder.addField("Erstellt vor", ChronoUnit.DAYS.between(message.guild.timeCreated, LocalDateTime.now().atOffset(ZoneOffset.UTC)).toString() + " Tagen", true)

        message.textChannel.sendMessage(embedBuilder.build()).queue()
    }
}
