package pl.banas.sms.questions

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.logging.LoggerFactory
import kotlin.system.exitProcess

object Main {
    private val LOGGER =  LoggerFactory.getLogger(Main::class.java)!!
    @JvmStatic
    fun main(args: Array<String>) = try {
        val vertx: Vertx = Vertx.vertx().also {
            addShutdownHook("stop Vertx", it::close)
        }

        vertx.deployVerticle(
                { ReceivingVerticle() },
                DeploymentOptions().setInstances(Runtime.getRuntime().availableProcessors())
        ) { res ->
            if (res.succeeded()) {
                 LOGGER.info("Successfully deployed.")
            } else {
                logErrorAndExit("Failed to deploy.", res.cause())
            }
        }
    } catch (e: Exception) {
        logErrorAndExit("Unexpected error happened in Main!", e)
    }

    private fun logErrorAndExit(msg: String, t: Throwable? = null): Nothing {
        LOGGER.error(msg, t)
        exitProcess(-1)
    }

    val MAPPER = ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
            .registerModule(Jdk8Module())
            .registerModule(JavaTimeModule())
            .registerModule(KotlinModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true)
            .configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
}
