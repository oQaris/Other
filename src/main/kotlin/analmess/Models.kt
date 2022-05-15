package analmess

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Chat(
    val id: Long,
    val name: String,
    val type: String,
    val messages: List<Message>
)

@Serializable
data class Message(
    val id: Long,
    val type: String,
    val date: LocalDateTime,

    val from: String,
    val fromId: String,

    val replyTo: Long?,
    val isForwarded: Boolean,

    val text: Text,

    val attachments: List<Media> = emptyList(),
    val durationSeconds: Int? = null
)

// Текст сообщения
@Serializable
data class Text(val parts: List<TextItem>) {
    fun simpleText() = parts.joinToString("") { it.text }
}

@Serializable
data class TextItem(val text: String, val type: String? = null)

// Элемент вложения
@Serializable
data class Media(val name: String)
