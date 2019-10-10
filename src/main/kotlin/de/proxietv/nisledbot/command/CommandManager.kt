package de.proxietv.nisledbot.command

import de.proxietv.nisledbot.core.NisledBot
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.reflections.Reflections
import org.slf4j.LoggerFactory
import java.util.*
import java.util.regex.Pattern

class CommandManager(bot: NisledBot) : ListenerAdapter() {

    private val availableCommands: MutableSet<Command>

    init {
        this.availableCommands = HashSet()
        val classes = Reflections("de.proxietv.nisledbot.command")
            .getSubTypesOf(Command::class.java)
        for (cmdClass in classes) {
            try {
                val command = cmdClass.getDeclaredConstructor().newInstance()
                command.setInstance(bot)
                command.lateInit()
                if (availableCommands.add(command)) {
                    logger.info("Registered " + command.command + " Command")
                }
            } catch (exception: Exception) {
                logger.error("Error while registering Command!", exception)
            }

        }
        bot.jda.addEventListener(this)
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot) {
            return
        }
        if (!event.isFromType(ChannelType.TEXT)) {
            return
        }
        val content = event.message.contentRaw

        val botChannels = listOf(594624581793218560, 628976458571972609, 613070147338633226, 629290746012499988)
        val globalCommands = listOf(".clear")
        if (content.startsWith(".") && (botChannels.contains(event.channel.idLong)
                    || globalCommands.stream().anyMatch { content.startsWith(it) } )) {
            val arguments = content.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val input = arguments[0].replaceFirst(".".toRegex(), "")
            var executed = false
            for (command in this.availableCommands) {
                if (command.category.equals(Command.Category.STAFF) && (!event.member!!.roles
                        .contains(event.jda.getRoleById(TEAM_ROLE_ID)) || !event.member!!.hasPermission(Permission.ADMINISTRATOR))
                ) {
                    continue
                }

                if (command.command.equals(input, true)) {
                    command.execute(arguments.copyOfRange(1, arguments.size), event.message)
                    executed = true
                } else {
                    for (alias in command.aliases) {
                        if (alias.equals(input, ignoreCase = true)) {
                            command.execute(arguments.copyOfRange(1, arguments.size), event.message)
                            executed = true
                        }
                    }
                }
            }
            if (!executed) {
                if (content.length > 1 && Pattern.compile("[a-zA-Z]*").matcher(content.substring(1)).matches()) {
                    event.message.delete().queue()
                }
            }
        }
    }

    fun getAvailableCommands(): Set<Command> {
        return Collections.unmodifiableSet(availableCommands)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(CommandManager::class.java)

        private val TEAM_ROLE_ID = 631578299461664768
    }
}
