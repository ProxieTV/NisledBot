package de.proxietv.nisledbot.core

import io.sentry.Sentry
import org.slf4j.LoggerFactory
import kotlin.system.exitProcess


fun main() {
    if (System.getenv("SENTRY_DSN") != null || System.getProperty("sentry.properties") != null) {
        Sentry.init()
    }
    try {
        NisledBot()
    } catch (exception: Exception) {
        LoggerFactory.getLogger(NisledBot::class.java)
            .error("Encountered exception while initializing the bot!", exception)
        exitProcess(1)
    }

}
