package analmess.loader.parser

import analmess.*
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.*
import java.io.File

class VkParser(file: File) : Parser(file) {

    override fun parseChat(): Chat {
        val peer = chat.jsonObject["response"]!!.jsonObject["conversations"]!!.jsonArray[0].jsonObject["peer"]!!
        return Chat(
            peer.jsonObject["id"]!!.jsonPrimitive.long,
            peer.getContent("local_id")!!,
            peer.getContent("type")!!,
            parseMessages()
        )
    }

    private fun parseMessages() = chat.jsonObject["response"]!!.jsonObject["items"]!!.jsonArray.map { toMessage(it) }

    //TODO - разворачивать пересланные сообщения
    private fun toMessage(json: JsonElement) = Message(
        id = json.getContent("id")?.toLong() ?: 0,
        type = "message",
        date = Instant.fromEpochSeconds(json.getContent("date")!!.toLong()).toLocalDateTime(TimeZone.UTC),
        from = json.getContent("from_id") ?: "System", //todo
        fromId = json.getContent("from_id") ?: "System",
        replyTo = json.jsonObject["reply_message"]
            ?.jsonObject?.get("id")
            ?.jsonPrimitive?.long,
        isForwarded = json.jsonObject["fwd_messages"]!!.jsonArray.isNotEmpty(),
        text = Text(listOf(TextItem(json.getContent("text")!!))),
        attachments = getAttachments(json),
        durationSeconds = getDurations(json)
    )

    private fun getAttachments(json: JsonElement): List<Media> {
        val atts = json.jsonObject["attachments"]!!.jsonArray
        return atts.map {
            Media(it.getContent("type")!!)
        }
    }

    private fun getDurations(json: JsonElement): Int {
        val atts = json.jsonObject["attachments"]!!.jsonArray
        return atts.sumOf {
            it.jsonObject["audio_message"]
                ?.jsonObject?.get("duration")
                ?.jsonPrimitive?.int ?: 0
        }
    }
}
