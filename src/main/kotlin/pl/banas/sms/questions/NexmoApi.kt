package pl.banas.sms.questions

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import java.time.Instant

data class PhoneNumbers(@JsonProperty("count") val count: Int,
                        @JsonProperty("numbers") val numbers: List<PhoneNumber>)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PhoneNumber(@JsonProperty("country") val country: String,
                       @JsonProperty("msisdn") val id: String,
                       @JsonProperty("type") val type: String,
                       @JsonProperty("moHttpUrl") val whbhookUrl: String?,
                       @JsonProperty("features") val features: List<String>)

enum class SmsType(@get:JsonValue val value: String) {
    ASCI("text"),
    UNICODE("unicode"),
    BINARY("binary")
}

/**
 * Data format of nexmo incoming sms webhook
 */
data class Sms(
        @JsonProperty("msisdn") val sender: String, @JsonProperty("to") val to: String,
        @JsonProperty("messageId") val id: String, @JsonProperty("text") val text: String,
        @JsonProperty("type") val type: SmsType,
        @JsonProperty("keyword") val firstWordUppercase: String,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
        @JsonProperty("message-timestamp") val timestamp: Instant,
        @JsonProperty("concat") val multiMessage: Boolean = false,
        //not in docs, always come anyway
        @JsonProperty("api-key") val apiKey: String? = null,
        //required for binary messages
        @JsonProperty("timestamp") val unixTimestamp: Instant? = null,
        @JsonProperty("nonce") val nonce: String? = null,
        @JsonProperty("data") val binaryData: String? = null,
        @JsonProperty("udh") val binaryDataHeaders: String? = null,
        //required for multi chunk messages*/
        @JsonProperty("concat-ref") val concatenatedRef: String? = null,
        @JsonProperty("concat-total") val nrOfConcatenatedMessages: Int? = null,
        @JsonProperty("concat-part") val concatNr: Int? = null
)




