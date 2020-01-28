package pl.banas.sms.questions

import io.vertx.core.Vertx
import kotlin.concurrent.thread

fun addShutdownHook(name: String, block: () -> Unit) =
        Runtime.getRuntime().addShutdownHook(thread(start = false, name = name, block = block))
