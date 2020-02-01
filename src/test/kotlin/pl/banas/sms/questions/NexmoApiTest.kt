package pl.banas.sms.questions

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pl.banas.sms.questions.Main.MAPPER
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC
import java.time.format.DateTimeFormatter

internal class NexmoApiTest {
    @Test
    fun shouldSerializeType() {
        assertEquals("""{"type":"unicode"}""", MAPPER.writeValueAsString(TestEnum(SmsType.UNICODE)))
        assertEquals("""{"type":"binary"}""", MAPPER.writeValueAsString(TestEnum(SmsType.BINARY)))
        assertEquals("""{"type":"text"}""", MAPPER.writeValueAsString(TestEnum(SmsType.ASCI)))
    }

    @Test
    fun shouldDeSerializeType() {
        assertEquals(TestEnum(SmsType.UNICODE), MAPPER.readValue<TestEnum>("""{"type":"unicode"}"""))
        assertEquals(TestEnum(SmsType.BINARY), MAPPER.readValue<TestEnum>("""{"type":"binary"}"""))
        assertEquals(TestEnum(SmsType.ASCI), MAPPER.readValue<TestEnum>("""{"type":"text"}"""))
    }

    @Test
    fun shouldDeSerializeUnicodeMessage() {
        assertEquals(
                Sms(sender = "48694645700", to = "48732100626", id = "17000002677E17D8", firstWordUppercase = "JAŹNI,",
                        text = "Jaźni, łaska, że laską",
                        timestamp = LocalDateTime.parse("2020-02-01 17:48:35", TIME_FORMAT).toInstant(UTC),
                        type = SmsType.UNICODE, apiKey = "74242587"),
                MAPPER.readValue<Sms>("""
                    {"msisdn":"48694645700","to":"48732100626","messageId":"17000002677E17D8",
                    "text":"Jaźni, łaska, że laską","type":"unicode","keyword":"JAŹNI,","api-key":"74242587",
                    "message-timestamp":"2020-02-01 17:48:35"}
                """.trimMargin()
                ))
    }

    @Test
    fun shouldDeSerializeASCIMessage() {
        assertEquals(
                Sms(sender = "48518600071", to = "48732100626", id = "160000029D9343A4", firstWordUppercase = "JAZN",
                        text = "Jazn",
                        timestamp = LocalDateTime.parse("2020-02-01 17:35:57", TIME_FORMAT).toInstant(UTC),
                        type = SmsType.ASCI, apiKey = "74242587"),
                MAPPER.readValue<Sms>(""" 
                    {"msisdn":"48518600071","to":"48732100626","messageId":"160000029D9343A4","text":"Jazn",
                    "type":"text","keyword":"JAZN","api-key":"74242587","message-timestamp":"2020-02-01 17:35:57"}
                """.trimMargin()
                ))
    }

    data class TestEnum(@JsonProperty val type: SmsType)
    companion object{
        private val TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    }
}