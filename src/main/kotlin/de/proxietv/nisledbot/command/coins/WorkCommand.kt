package de.proxietv.nisledbot.command.coins

import com.google.gson.JsonArray
import de.proxietv.nisledbot.command.Command
import de.proxietv.nisledbot.database.table.UserGameStats
import de.proxietv.nisledbot.database.table.Users
import de.proxietv.nisledbot.utils.Constants
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.Color
import java.util.concurrent.TimeUnit

class WorkCommand : Command("work", "Arbeite, um Coins zu erhalten", Category.COIN, "arbeiten", "arbeit") {

    lateinit var works: JsonArray

    override fun lateInit() {
        this.works = bot!!.messageObject.getAsJsonArray("works")
    }

    override fun execute(args: Array<String>, message: Message) {
        val user = Users.forId(message.member!!.idLong)
        val gameStat = UserGameStats.forGameUser("work", user.id.value) { sessions = 1 }

        val embedBuilder = getEmbed(message.guild, message.author).setTitle("Arbeiten")

        val timeLeft = (gameStat.lastTime + 30 * 1000 * 60) - System.currentTimeMillis()
        if (timeLeft >= 0) {
            embedBuilder.setColor(Color.RED).setDescription(String.format("Du kannst erst in ⏰ %d Minuten und %d Sekunden wieder arbeiten!",
                TimeUnit.MILLISECONDS.toMinutes(timeLeft),
                TimeUnit.MILLISECONDS.toSeconds(timeLeft) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeLeft)) ))
            message.channel.sendMessage(embedBuilder.build()).queue {
                it.delete().queueAfter(5, TimeUnit.SECONDS) {
                    message.delete().queue()
                }
            }
        } else {
            val work = works.get(Constants.RANDOM.nextInt(works.size())).asJsonObject
            var coins = work.get("max_coins").asInt
            coins -= Constants.RANDOM.nextInt(coins / 100 * 40 + 1)
            embedBuilder.setColor(Color(44, 186, 67)).setDescription(work.get("message").asString).addField("Du erhälst:","<:positive:629378199515561994> $coins <a:coin:629226445088227349>", true)

            message.channel.sendMessage(embedBuilder.build()).queue()

            transaction {
                user.coins += coins

                gameStat.lastTime = System.currentTimeMillis()

/*                if (gameStat.sessions == 0) {
                    if (timeLeft < -30 * 1000 * 60) {
                        println(timeLeft)
                        println(-30 * 1000 * 60)
                        gameStat.sessions = 1
                        val infoEmbed = EmbedBuilder()
                            .setColor(Color.ORANGE)
                            .setDescription(message.member!!.asMention + " <a:animated_bell:629700446293852160> " +
                                    "Du kannst noch " + gameStat.sessions + " mal arbeiten!")
                        message.channel.sendMessage(infoEmbed.build()).queue {
                            it.delete().queueAfter(5, TimeUnit.SECONDS)
                        }
                    }
                    gameStat.lastTime = System.currentTimeMillis() + timeLeft
                } else {
                    gameStat.sessions -= 1
                }*/
            }
        }
    }
}