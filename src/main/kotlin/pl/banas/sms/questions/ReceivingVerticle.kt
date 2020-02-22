package pl.banas.sms.questions

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE
import io.netty.handler.codec.http.HttpHeaderValues.APPLICATION_JSON
import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.kotlin.core.http.listenAwait
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.awaitBlocking
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.launch

class ReceivingVerticle(private val mapper: ObjectMapper, private val smsDao: SmsDao,
                        private val nexmoDao: NexmoDao) : CoroutineVerticle() {

    override suspend fun start() {
        val router = Router.router(vertx).also { it.route().handler(BodyHandler.create()) }

        router.post("/sms").coroutineHandler(this::smsReceived)
        router.get("/messages").coroutineHandler(this::listMessages)
        router.get("/messages/:number").coroutineHandler(this::listMessagesByNr)
        router.get("/numbers").coroutineHandler(this::listNumbers)
        router.errorHandler(INTERNAL_SERVER_ERROR.code()) { context ->
            if (context.failed()) {
                LOGGER.error("Exception in verticle", context.failure())
            }
            context.response()
                    .setStatusCode(INTERNAL_SERVER_ERROR.code())
                    .putHeader(CONTENT_TYPE, APPLICATION_JSON)
                    .end()
        }
        vertx.createHttpServer()
                .requestHandler(router)
                .listenAwait(80)
    }

    private suspend fun smsReceived(context: RoutingContext) {
        val sms = mapper.readValue<Sms>(context.body.bytes)
        awaitBlocking { smsDao.save(sms) }
        context.response()
                .setStatusCode(HttpResponseStatus.OK.code())
                .putHeader(CONTENT_TYPE, APPLICATION_JSON)
                .end()
    }

    private suspend fun listMessages(context: RoutingContext) {
        val messages = awaitBlocking { smsDao.list().toList() }
        context.response()
                .setStatusCode(HttpResponseStatus.OK.code())
                .putHeader(CONTENT_TYPE, APPLICATION_JSON)
                .end(awaitBlocking { mapper.writeValueAsString(messages) })
    }

    private suspend fun listNumbers(context: RoutingContext) {
        val numbers = nexmoDao.listNumbers()
        context.response()
                .setStatusCode(HttpResponseStatus.OK.code())
                .putHeader(CONTENT_TYPE, APPLICATION_JSON)
                .end(awaitBlocking { mapper.writeValueAsString(numbers) })
    }

    private suspend fun listMessagesByNr(context: RoutingContext) {
        val nr = context.request().getParam("number")
        val messages = awaitBlocking { smsDao.listFromNumber(nr).toList() }
        context.response()
                .setStatusCode(HttpResponseStatus.OK.code())
                .putHeader(CONTENT_TYPE, APPLICATION_JSON)
                .end(awaitBlocking { mapper.writeValueAsString(messages) })
    }

    private fun Route.coroutineHandler(fn: suspend (RoutingContext) -> Unit) {
        handler { ctx ->
            launch(ctx.vertx().dispatcher()) {
                try {
                    fn(ctx)
                } catch (e: Exception) {
                    ctx.fail(e)
                }
            }
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(ReceivingVerticle::class.java.simpleName)!!
    }
}
