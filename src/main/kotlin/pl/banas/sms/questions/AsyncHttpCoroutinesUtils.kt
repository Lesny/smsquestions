package pl.banas.sms.questions

import org.asynchttpclient.BoundRequestBuilder
import org.asynchttpclient.Response

suspend fun BoundRequestBuilder.executeCoAwait(): Response = this.execute()
            .toCompletableFuture()
            .coAwait()