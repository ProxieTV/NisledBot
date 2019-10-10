package de.proxietv.nisledbot.core

import com.google.gson.JsonObject
import com.google.gson.stream.JsonReader
import com.zaxxer.hikari.HikariDataSource
import de.proxietv.nisledbot.command.CommandManager
import de.proxietv.nisledbot.database.model.User
import de.proxietv.nisledbot.database.table.UserGameStats
import de.proxietv.nisledbot.database.table.UserRoles
import de.proxietv.nisledbot.database.table.Users
import de.proxietv.nisledbot.listener.guild.MessageReceiveListener
import de.proxietv.nisledbot.utils.Constants
import de.proxietv.nisledbot.utils.InviteTracker
import net.dv8tion.jda.api.JDA
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import net.dv8tion.jda.api.events.guild.GuildReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.AccountType
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.entities.Guild
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.yaml.snakeyaml.Yaml
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.file.Paths


class NisledBot(private val logger: Logger = LoggerFactory.getLogger(NisledBot::class.java)) {
    val jda: JDA
    val commandManager: CommandManager
    var guild: Guild? = null
    var messageObject: JsonObject = Constants.GSON.fromJson(InputStreamReader(this.javaClass.classLoader.getResourceAsStream("messages.json")!!), JsonObject::class.java)

    init {
        val startTime = System.currentTimeMillis()
        logger.info("Starting Nisled Bot")

        initializeDatabase()
        logger.info("Database set up!")

        jda = initializeJDA()
        logger.info("JDA set up!")

        commandManager = CommandManager(this)
        logger.info("Command-Manager set up!")

        jda.addEventListener(MessageReceiveListener(this))

        Runtime.getRuntime().addShutdownHook(Thread { jda.shutdown() })

        logger.info(String.format("Startup finished in %dms!", System.currentTimeMillis() - startTime))
    }

    private fun initializeDatabase() {
        val inputStream = this.javaClass.classLoader.getResourceAsStream("database.yml")
        val config = Yaml().load<Map<String, String>>(inputStream)
        Database.connect(HikariDataSource().apply {
            driverClassName = config.getOrDefault("driver", "org.mariadb.jdbc.Driver")
            jdbcUrl = config.getOrDefault("url", "jdbc:sqlite:\$dataFolder/database")
                .replace("\$dataFolder", Paths.get(".").toAbsolutePath().toString())
            username = config.getOrDefault("username", "root")
            password = config.getOrDefault("password", "admin")
            transactionIsolation = "TRANSACTION_READ_COMMITTED"
        })
        transaction {
            SchemaUtils.createMissingTablesAndColumns(Users, UserRoles, UserGameStats)
        }
    }

    @Throws(Exception::class)
    private fun initializeJDA(): JDA {
        try {
            val jdaBuilder = JDABuilder(AccountType.BOT)
            jdaBuilder.setToken(System.getenv("DISCORD_TOKEN"))
            jdaBuilder.setActivity(Activity.watching("Nisled neues Video"))
            jdaBuilder.setStatus(OnlineStatus.DO_NOT_DISTURB)
            jdaBuilder.addEventListeners(object : ListenerAdapter() {
                override fun onGuildReady(event: GuildReadyEvent) {
                    guild = event.guild
                    println(guild!!.getEmotesByName("animated_bell", true).get(0).idLong)
                    InviteTracker()
                }
            })
            return jdaBuilder.build().awaitReady()
        } catch (exception: Exception) {
            logger.error("Encountered exception while initializing ShardManager!")
            throw exception
        }
    }

}