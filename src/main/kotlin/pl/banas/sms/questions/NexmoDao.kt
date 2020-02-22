package pl.banas.sms.questions

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioDatagramChannel
import io.netty.handler.codec.http.HttpResponseStatus.OK
import io.netty.resolver.dns.DnsNameResolver
import io.netty.resolver.dns.DnsNameResolverBuilder
import org.asynchttpclient.AsyncHttpClient
import java.io.IOException
import java.time.Duration


class NexmoDao(client: AsyncHttpClient, private val mapper: ObjectMapper, private val apiKey: String,
               private val apiSecret: String) {

    private val listNumbers = client.prepareGet("$NEXMO_URL/account/numbers")
            .setNameResolver(DEFAULT_NAME_RESOLVER)

    suspend fun listNumbers(): PhoneNumbers {
        val response = listNumbers
                .setQueryParams(mapOf(
                        "api_key" to listOf(apiKey),
                        "api_secret" to listOf(apiSecret),
                        "size" to listOf("100"),
                        "index" to listOf("1")
                ))
                .executeCoAwait()
        return if (response.statusCode == OK.code()) {
            mapper.readValue(response.responseBodyAsBytes)
        } else {
            throw IOException("Unexpected response from nexmo when listing nrs, ${response.uri} $response")
        }
    }

    companion object {
        private val NIO_EVENT_LOOP = NioEventLoopGroup()
        private val DEFAULT_DNS_TIMEOUT = Duration.ofSeconds(5)
        private val DEFAULT_NAME_RESOLVER: DnsNameResolver = DnsNameResolverBuilder(NIO_EVENT_LOOP.next())
                .channelType(NioDatagramChannel::class.java)
                .queryTimeoutMillis(DEFAULT_DNS_TIMEOUT.toMillis())
                .build()

        private const val NEXMO_URL = "https://rest.nexmo.com"
    }
}