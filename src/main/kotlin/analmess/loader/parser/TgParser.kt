package analmess.loader.parser

import analmess.*
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.*
import java.io.File

class TgParser(file: File) : Parser(file) {

    override fun parseChat() = Chat(
        chat.jsonObject["id"]!!.jsonPrimitive.long,
        chat.getContent("name")!!,
        chat.getContent("type")!!,
        parseMessages()
    )

    private fun parseMessages() = chat.jsonObject["messages"]!!.jsonArray.map { toMessage(it) }.asReversed()

    private fun toMessage(json: JsonElement) = Message(
        id = json.getContent("id")!!.toLong(),
        type = json.getContent("type")!!,
        date = LocalDateTime.parse(json.getContent("date")!!),
        from = json.getContent("from") ?: "System",
        fromId = json.getContent("from_id") ?: "System",
        replyTo = json.getContent("reply_to_message_id")?.toLong(),
        isForwarded = json.getContent("forwarded_from") != null,
        text = getTextMessage(json),
        attachments = getAttachments(json),
        durationSeconds = json.getContent("duration_seconds")?.toInt(),
    )

    private fun getTextMessage(json: JsonElement): Text {
        val textObj = json.jsonObject["text"]!!
        return Text(buildList {
            try {
                add(TextItem(textObj.jsonPrimitive.content))
            } catch (e: IllegalArgumentException) {
                textObj.jsonArray.forEach {
                    var type: String? = null
                    val curJsonObj =
                        if (!it.isObject()) it
                        else {
                            type = it.jsonObject["type"]!!.jsonPrimitive.content
                            it.jsonObject["text"]!!
                        }
                    add(TextItem(curJsonObj.jsonPrimitive.content, type))
                }
            }
        })
    }

    private fun getAttachments(json: JsonElement): List<Media> {
        val str = json.getContent("media_type")
        //ToDo
        if (str == null)
            return emptyList()
        return listOf(Media(str))
    }
}
