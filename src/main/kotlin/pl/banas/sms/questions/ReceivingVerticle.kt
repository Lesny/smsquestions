package pl.banas.sms.questions

import com.fasterxml.jackson.module.kotlin.readValue
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.http.HttpServer
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import pl.banas.sms.questions.Main.MAPPER

@Suppress("UnstableApiUsage") // Guava MediaType
class ReceivingVerticle : AbstractVerticle() {

    override fun start(deployed: Future<Void?>) {
        val router = Router.router(vertx).also {
            it.route().handler(BodyHandler.create())
        }

        router.post("/sms").handler(this::smsReceived)

        val httpDeployed: Future<HttpServer> = Future.future()

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(80, httpDeployed.completer())

        httpDeployed.map<Void?> { null }
                .setHandler(deployed.completer())
    }

    private fun smsReceived(context: RoutingContext) {
        val sms = MAPPER.readValue<Sms>(context.body.bytes)
        LOGGER.info("Received SMS: $sms")
        context.response()
                .setStatusCode(HttpResponseStatus.OK.code()).end("")
    }

    companion object{
        private val LOGGER = LoggerFactory.getLogger(ReceivingVerticle::class.java.simpleName)!!
    }

}
