package pl.banas.sms.questions

import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import java.util.logging.Logger
import kotlin.system.exitProcess

object Main {
    private val LOGGER = Logger.getLogger(Main::class.java.simpleName)!!
    private const val MAPPER_WORKER_POOL_NAME = "mapperWorkerPool"

    @JvmStatic
    fun main(args: Array<String>) = try {
        val vertx = Vertx.vertx()!!

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
        LOGGER.severe(msg)
        exitProcess(-1)
    }
}
