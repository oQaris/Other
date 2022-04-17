package telegram

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.*
import java.io.File

class Parser(file: File) {

    private val chat = Json.parseToJsonElement(file.readText())

    fun parseChat() = Chat(
        chat.jsonObject["id"]!!.jsonPrimitive.long,
        chat.getContent("name")!!,
        chat.getContent("type")!!,
        parseMessages()
    )

    private fun parseMessages() = chat.jsonObject["messages"]!!.jsonArray.map { toMessage(it) }

    private fun toMessage(json: JsonElement) = Message(
        id = json.getContent("id")!!.toLong(),
        type = json.getContent("type")!!,
        date = LocalDateTime.parse(json.getContent("date")!!),
        from = json.getContent("from") ?: "System",
        from_id = json.getContent("from_id") ?: "System",
        reply_to_message_id = json.getContent("reply_to_message_id")?.toLong(),
        text = getTextMessage(json),
        file = json.getContent("file"),
        photo = json.getContent("photo"),
        media_type = json.getContent("media_type"),
        sticker_emoji = json.getContent("sticker_emoji"),
        mime_type = json.getContent("mime_type"),
        duration_seconds = json.getContent("duration_seconds")?.toInt(),
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

    private fun JsonElement.getContent(name: String) = jsonObject[name]?.jsonPrimitive?.content

    private fun JsonElement.isObject() = try {
        jsonObject; true
    } catch (e: IllegalArgumentException) {
        false
    }
}
