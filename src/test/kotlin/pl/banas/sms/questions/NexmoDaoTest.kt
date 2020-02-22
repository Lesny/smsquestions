package pl.banas.sms.questions

import kotlinx.coroutines.runBlocking
import org.asynchttpclient.Dsl
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

//@Tag("integration")
internal class NexmoDaoTest {
    @Test
    fun shouldListNumbers() {
        val nexmoDao = NexmoDao(Dsl.asyncHttpClient(), Main.MAPPER, "74242587", "BsZoe7bXJWz7W7PJ")
        runBlocking {
            val numbers = nexmoDao.listNumbers()
            println("$numbers")
            assertTrue(numbers.numbers.any { it.id == "48732100626" }, "48732100626 not found in $numbers")
        }
    }
}