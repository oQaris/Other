package telegram.parser

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import telegram.Chat
import java.io.File

abstract class Parser(file: File) {

    protected val chat = Json.parseToJsonElement(file.readText())

    abstract fun parseChat(): Chat

    protected fun JsonElement.getContent(name: String) = jsonObject[name]?.jsonPrimitive?.content

    protected fun JsonElement.isObject() = try {
        jsonObject; true
    } catch (e: IllegalArgumentException) {
        false
    }
}
