package telegram

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.*
import java.io.File

class Parser(file: File) {

    private val dialog = Json.parseToJsonElement(file.readText())

    fun parseMessages() = dialog.jsonObject["messages"]!!.jsonArray.map { toMessage(it) }

    private fun toMessage(json: JsonElement) = Message(
        json.getContent("id")!!.toLong(),
        json.getContent("type")!!,
        LocalDateTime.parse(json.getContent("date")!!),
        json.getContent("from") ?: "Unknown",
        json.getContent("from_id") ?: "Unknown",
        getTextMessage(json),
        json.getContent("media_type"),
        json.getContent("sticker_emoji"),
        json.getContent("file"),
        json.getContent("photo"),
    )

    private fun getTextMessage(json: JsonElement): Text {
        val textObj = json.jsonObject["text"]!!
        return Text(listOf(TextItem(
            text = try {
                textObj.jsonPrimitive.content
            } catch (e: IllegalArgumentException) {
                textObj.jsonArray
                    .filter { it.isObject() }
                    .joinToString {
                        it.jsonObject["text"]!!
                            .jsonPrimitive.content
                    }
            },
            type = "null"
        )))
    }

    private fun JsonElement.getContent(name: String) = jsonObject[name]?.jsonPrimitive?.content

    private fun JsonElement.isPrimitive() = try {
        jsonPrimitive; true
    } catch (e: IllegalArgumentException) {
        false
    }

    private fun JsonElement.isArray() = try {
        jsonArray; true
    } catch (e: IllegalArgumentException) {
        false
    }

    private fun JsonElement.isObject() = try {
        jsonObject; true
    } catch (e: IllegalArgumentException) {
        false
    }
}