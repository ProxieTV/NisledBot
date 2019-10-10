package de.proxietv.nisledbot.utils

import de.proxietv.nisledbot.core.NisledBot
import java.util.concurrent.TimeUnit
import net.dv8tion.jda.api.EmbedBuilder
import java.util.concurrent.Executors
import java.util.concurrent.ConcurrentHashMap
import net.dv8tion.jda.api.entities.Invite
import org.jetbrains.exposed.sql.Database


class InviteTracker {
    inner class InviteTracker(private val bot: NisledBot) {
        private val inviteHashMap: ConcurrentHashMap<String, Invite> = ConcurrentHashMap()

        /**
         * See which invite has been used by a new member of a guild
         */
        fun start() {
            Executors.newScheduledThreadPool(1).scheduleAtFixedRate({
                bot.jda.guilds[0].retrieveInvites().queue { inviteList ->
                    inviteList.forEach { invite ->
                        if (inviteHashMap.containsKey(invite.code) && invite.uses > inviteHashMap[invite.code]!!.uses) {
                            //val levelUser =
                                //Database.getUserData(invite.getGuild().getId(), invite.getInviter().getId())

                            val embedBuilder = EmbedBuilder()
                            embedBuilder.setDescription(invite.getInviter()!!.getAsMention() + " vielen Dank das du jemand neues auf c0debase gebracht hast [" + invite.getCode() + "]")

                            bot.jda.getTextChannelById(System.getenv("BOTCHANNEL"))!!.sendMessage(embedBuilder.build()).queue()

                            //if (levelUser.addXP(100)) {
                            //    val levelUpEmbed = EmbedBuilder()
                            //    levelUpEmbed.setDescription(invite.getInviter().getAsMention() + " ist nun Level " + levelUser.getLevel())
                            //    bot.getJDA().getTextChannelById(System.getenv("BOTCHANNEL"))
                            //        .sendMessage(levelUpEmbed.build()).queue()
                            //}
                            //  bot.getDataManager().updateUserData(levelUser)
                        }
                        inviteHashMap.put(invite.getCode(), invite)
                    }
                }
            }, 5, 5, TimeUnit.SECONDS)
        }
    }
}