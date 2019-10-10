package de.proxietv.nisledbot.command.general

import de.proxietv.nisledbot.command.Command
import net.dv8tion.jda.api.JDAInfo
import net.dv8tion.jda.api.entities.Message
import java.lang.management.ManagementFactory
import java.util.concurrent.TimeUnit


class StatsCommand : Command("stats", "Zeigt dir einige Informationen über den Bot", Category.GENERAL, "info") {

    override fun execute(args: Array<String>, message: Message) {
        val jda = message.jda
        val uptime = ManagementFactory.getRuntimeMXBean().uptime

        val embedBuilder = getEmbed(message.guild, message.author)
        embedBuilder.addField("JDA Version", JDAInfo.VERSION, true)
        embedBuilder.addField("Ping", (jda.gatewayPing).toString() + "ms", true)
        embedBuilder.addField(
            "Uptime",
            ((TimeUnit.MILLISECONDS.toDays(uptime)).toString() + "d " + TimeUnit.MILLISECONDS.toHours(uptime) % 24 + "h " +
                    TimeUnit.MILLISECONDS.toMinutes(uptime) % 60 + "m " +
                    TimeUnit.MILLISECONDS.toSeconds(uptime) % 60 + "s"),
            true
        )
        embedBuilder.addField("Commands", bot!!.commandManager.getAvailableCommands().size.toString(), true)
        embedBuilder.addField("Java Version", System.getProperty("java.runtime.version").replace("+", "_"), true)
        embedBuilder.addField("Betriebssystem", ManagementFactory.getOperatingSystemMXBean().name, true)
        embedBuilder.addField(
            "CPU Threads",
            ManagementFactory.getOperatingSystemMXBean().availableProcessors.toString(), true
        )
        embedBuilder.addField(
            "RAM Usage",
            ((((ManagementFactory.getMemoryMXBean().heapMemoryUsage.used + ManagementFactory.getMemoryMXBean().nonHeapMemoryUsage.getUsed())) / 1000000)).toString() + " / " +
                    (ManagementFactory.getMemoryMXBean().heapMemoryUsage.max + ManagementFactory.getMemoryMXBean().nonHeapMemoryUsage.max) / 1000000 + " MB",
            true
        )
        embedBuilder.addField("Threads", (Thread.activeCount()).toString(), true)
        message.channel.sendMessage(embedBuilder.build()).queue()
    }
}
