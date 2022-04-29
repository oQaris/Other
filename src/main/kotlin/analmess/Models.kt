package analmess

import kotlinx.datetime.LocalDateTime

data class Chat(
    val id: Long,
    val name: String,
    val type: String,
    val messages: List<Message>
)

data class Message(
    val id: Long,
    val type: String,
    val date: LocalDateTime,
    val from: String,
    val fromId: String,
    val replyTo: Long?,
    val isForwarded: Boolean, //fwd_messages vk
    val text: Text,

    /*val file: String? = null,
    val photo: String? = null,
    val media_type: String? = null,
    val sticker_emoji: String? = null,
    val mime_type: String? = null,*/
    val attachments: List<Media> = emptyList(),
    val durationSeconds: Int? = null
)

// Текст сообщения

data class Text(val parts: List<TextItem>) {
    fun simpleText() = parts.joinToString("") { it.text }
}

data class TextItem(val text: String, val type: String? = null)

// Элемент вложения

data class Media(val data: String)

enum class MediaType {
    Photo, Video, VoiceMessage, Other
}
