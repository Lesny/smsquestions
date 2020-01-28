package pl.banas.sms.questions

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.http.HttpServer
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import java.util.logging.Logger

@Suppress("UnstableApiUsage") // Guava MediaType
class ReceivingVerticle() : AbstractVerticle() {
    companion object {
        private val LOGGER = Logger.getLogger(ReceivingVerticle::class.java.simpleName)!!
    }

    override fun start(deployed: Future<Void?>) {
        val router = Router.router(vertx)
        router.post("/sms").handler(this::smsReceived)

        val httpDeployed: Future<HttpServer> = Future.future()

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(80, httpDeployed.completer())

        httpDeployed.map<Void?> { null }
                .setHandler(deployed.completer())
    }

    private fun smsReceived(context: RoutingContext) {
        LOGGER.info("Receiving SMS !")
        context.response()
                .setStatusCode(HttpResponseStatus.OK.code()).end("")
    }
}
