package de.proxietv.nisledbot.command.coins

import com.google.gson.JsonArray
import de.proxietv.nisledbot.command.Command
import de.proxietv.nisledbot.database.table.UserGameStats
import de.proxietv.nisledbot.database.table.Users
import de.proxietv.nisledbot.utils.Constants
import net.dv8tion.jda.api.entities.Message
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.Color
import java.util.concurrent.TimeUnit

class CrimeCommand : Command("crime", "Begehe Verbrechen um Geld zu erhalten/verlieren", Category.COIN, "cr", "verbrechen") {

    lateinit var crimes: JsonArray

    override fun lateInit() {
        this.crimes = bot!!.messageObject.getAsJsonArray("crimes")
    }

    override fun execute(args: Array<String>, message: Message) {
        val user = Users.forId(message.member!!.idLong)
        val gameStat = UserGameStats.forGameUser("crime", user.id.value) { sessions = 1 }
        val embedBuilder = getEmbed(message.guild, message.author).setTitle("Verbrechen")
        val timeLeft = (gameStat.lastTime + 60 * 1000 * 60) - System.currentTimeMillis()
        if (timeLeft >= 0) {
            embedBuilder.setColor(Color.RED).setDescription(
                String.format(
                    "Du kannst erst in ⏰ %d Minuten und %d Sekunden ein neues Verbrechen begehen!",
                    TimeUnit.MILLISECONDS.toMinutes(timeLeft),
                    TimeUnit.MILLISECONDS.toSeconds(timeLeft) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(timeLeft)))
            )
            message.channel.sendMessage(embedBuilder.build()).queue {
                it.delete().queueAfter(7, TimeUnit.SECONDS) {
                    message.delete().queue()
                }
            }
        }  else {
            val crime = crimes.get(Constants.RANDOM.nextInt(crimes.size())).asJsonObject
            var coins = crime.get("max_coins").asInt
            val random = Constants.RANDOM.nextInt(100) + 1
            coins -= Constants.RANDOM.nextInt(coins / 100 * 40 + 1)
            if (user.coins < 2000 || random > user.coins / 1500 + 35 || random > 60 ) {
                embedBuilder.setColor(Color(44, 186, 67)).setDescription(crime.get("message").asString + " \nDu bist ungestraft davon gekommen!").addField("Du erhälst:","<:positive:629378199515561994> $coins <a:coin:629226445088227349>", true)
            } else {
                coins = (coins / 100) * 85
                embedBuilder.setColor(Color(209, 13, 13)).setDescription(crime.get("message").asString + " \nLeider wurdest du erwischt und verurteilt! <a:WeeWoo:629356616247935018>").addField("Du verlierst:","<:negative:629378239428689921> $coins <a:coin:629226445088227349>", true)
                coins *= (-1)
            }

            message.channel.sendMessage(embedBuilder.build()).queue()

            transaction {
                user.coins += coins

                gameStat.lastTime = System.currentTimeMillis()
/*                if (gameStat.sessions == 0) {
                    if (timeLeft < (-60 * 1000 * 60)) {
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