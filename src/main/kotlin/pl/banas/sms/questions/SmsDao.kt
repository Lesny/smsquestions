package pl.banas.sms.questions

import com.rethinkdb.RethinkDB.r
import com.rethinkdb.net.Connection
import com.rethinkdb.net.Cursor
import java.time.Instant

class SmsDao(private val conn: Connection) {
    fun save(sms: Sms) {
        r.table("sms").insert(r.hashMap()
                .with("sender", sms.sender)
                .with("to", sms.to)
                .with("text", sms.text)
                .with("time", sms.timestamp.toEpochMilli())
        ).runNoReply(conn)
    }

    fun list(): List<Message> = r.table("sms").run<Cursor<Message>>(conn).toList()

    fun listFromNumber(nr: String): List<Message> = r.table("sms")
            .filter(r.hashMap("to", nr))
            .run<Cursor<Message>>(conn).toList()

    data class Message(val sender: String, val to: String, val text: String, val time: Instant)
}