package de.proxietv.nisledbot.listener.guild

import de.proxietv.nisledbot.core.NisledBot
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.concurrent.TimeUnit
import com.google.common.cache.CacheLoader
import com.google.common.cache.CacheBuilder
import com.google.common.cache.LoadingCache
import de.proxietv.nisledbot.database.table.Users
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.Color


class MessageReceiveListener(private val bot: NisledBot) : ListenerAdapter() {

    private val MEMBER_ROLE_ID = 409048653647314955L


    var messages: LoadingCache<Member, List<String>> = CacheBuilder.newBuilder().maximumSize(20)
        .expireAfterWrite(10, TimeUnit.SECONDS)
        .build(
            object : CacheLoader<Member, List<String>>() {
                @Throws(Exception::class)
                override fun load(key: Member): List<String> {
                    return ArrayList(3)
                }
            })

    override fun onPrivateMessageReceived(event: PrivateMessageReceivedEvent) {
        if (!event.author.isBot) {
            val embedBuilder = EmbedBuilder()
            embedBuilder.setColor(Color.RED)
            embedBuilder.appendDescription("Private Nachrichten sind deaktiviert")
            event.channel.sendMessage(embedBuilder.build()).queue()
        }
    }

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (event.author.isBot) return

        val member = event.member
        val message = event.message

        if (member != null) {
            val messagePack = messages.get(member)
            if ((message.contentRaw.length <= 1 || !message.contentRaw.startsWith(".")) &&
                (messagePack.contains(message.contentRaw) || (messagePack.filterIndexed { index, it -> index == 0  && (it.contains(message.contentRaw, true) || message.contentRaw.contains(it, true)) }.isNotEmpty()))
            ) {
                message.delete().queue()
                return
            }
            event.channel.history.retrievePast(20).queue {
                val history = it.filter { msg -> msg.timeCreated.toEpochSecond() > message.timeCreated.toEpochSecond() - 60 }
                val memberHistory = history.filter { msg -> msg.member == message.member }
                if (memberHistory.size > 8 && memberHistory.size.toDouble() / history.size.toDouble() > 0.7) {
                    message.delete().queue()
                    println("deleted")
                    return@queue
                }
            }
            messages.put(
                member,
                mutableListOf(messagePack.getOrElse(1) { "empty" },
                    messagePack.getOrElse(2) { "empty" },
                    message.contentRaw
                )
            )
        }

        updateXP(message)
    }

    private fun updateXP(message: Message) {
        val member = message.member
        if (member != null) {
            val user = Users.forId(member.idLong)
            val time = (System.currentTimeMillis() - user.lastMessage) / 1000
            if (time >= 50.0f) {
                if (user.addXP(50)) {
                    val levelUpEmbed = EmbedBuilder()
                    val newLevel = user.level
                    levelUpEmbed.appendDescription(message.author.asMention + " ist nun Level " + newLevel)
                    message.textChannel.sendMessage(levelUpEmbed.build()).queue()
                    if (newLevel > 2 && !message.member!!.roles.contains(message.jda.getRoleById(MEMBER_ROLE_ID))) {
                        message.guild.addRoleToMember(message.member!!, message.jda.getRoleById(MEMBER_ROLE_ID)!!)
                            .queue()
                    }
                }
                transaction {
                    user.lastMessage = System.currentTimeMillis()
                }
            }
        }
    }
}