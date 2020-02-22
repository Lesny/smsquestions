package pl.banas.sms.questions

import io.vertx.kotlin.coroutines.awaitEvent
import java.util.concurrent.CompletableFuture

suspend fun <T> CompletableFuture<T>.coAwait(): T = awaitEvent { handler ->
    this.thenAcceptAsync(handler::handle)
}