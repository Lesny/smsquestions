package pl.banas.sms.questions

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.rethinkdb.RethinkDB
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.logging.LoggerFactory
import org.asynchttpclient.Dsl.asyncHttpClient
import kotlin.system.exitProcess

object Main {
    private val LOGGER = LoggerFactory.getLogger(Main::class.java)!!
    @JvmStatic
    fun main(args: Array<String>) = try {
        val vertx: Vertx = Vertx.vertx().also {
            addShutdownHook("stop Vertx", it::close)
        }
        val conn = RethinkDB.r.connection().hostname("localhost").port(28015).connect().also {
            addShutdownHook("stop rethinkDb connection", it::close)
        }
        val smsDao = SmsDao(conn)

        val nexmoDao = NexmoDao(asyncHttpClient(), MAPPER, "74242587", "BsZoe7bXJWz7W7PJ")

        vertx.deployVerticle({ ReceivingVerticle(MAPPER, smsDao, nexmoDao) },
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

    val MAPPER: ObjectMapper = ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
            .registerModule(Jdk8Module())
            .registerModule(JavaTimeModule())
            .registerModule(KotlinModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true)
            .configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
}
