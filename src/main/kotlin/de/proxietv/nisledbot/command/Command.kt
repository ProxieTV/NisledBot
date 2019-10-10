package de.proxietv.nisledbot.command

import de.proxietv.nisledbot.core.NisledBot
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User

abstract class Command(val command: String, val description: String, val category: Category, vararg val aliases: String) {
    var bot: NisledBot? = null
        protected set

    abstract fun execute(args: Array<String>, message: Message)

    protected fun getEmbed(guild: Guild, requester: User): EmbedBuilder {
        return EmbedBuilder().setFooter(
            "@" + requester.name + "#" + requester.discriminator,
            requester.effectiveAvatarUrl
        ).setColor(guild.selfMember.color)
    }

    open fun lateInit() { }

    fun setInstance(instance: NisledBot) {
        check(bot == null) { "Can only initialize once!" }
        bot = instance
    }

    enum class Category private constructor(val catName: String, val emote: String, val description: String) {
        GENERAL("General", "one", "Öffentliche Commands"),
        COIN("Coin", "two", "Commands für Coins"),
        MUSIC("Music", "two", "Commands für Musik"),
        STAFF("Team", "three", "Commands für das Team")
    }
}

