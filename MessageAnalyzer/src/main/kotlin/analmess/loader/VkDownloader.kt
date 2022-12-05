package analmess.loader

import analmess.*
import com.vk.api.sdk.client.VkApiClient
import com.vk.api.sdk.client.actors.UserActor
import com.vk.api.sdk.httpclient.HttpTransportClient
import com.vk.api.sdk.objects.messages.GetHistoryRev
import com.vk.api.sdk.objects.messages.MessageAttachmentType
import com.vk.api.sdk.objects.messages.responses.GetHistoryExtendedResponse
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import com.vk.api.sdk.objects.messages.Message as VkMessage

/**
 * Скачивает полную историю сообщений из конкретного чата.
 * @param myId идентификатор владельца токена.
 * @param token токен безопасности (можно получить из URL, перейдя по ссылке ниже).
 * @param chatId ID пользователя, сообщества (отрицательный) или беседы (2000000000+id)
 * @see <a href="https://oauth.vk.com/authorize?client_id=6287487&scope=1073737727&
 * redirect_uri=https://oauth.vk.com/blank.html&display=page&response_type=token&revoke=1">oauth.vk.com/authorize</a>
 */
class VkDownloader(myId: Int, token: String, private val chatId: Int) : Loader {
    private val vk = VkApiClient(HttpTransportClient())
    private val actor = UserActor(myId, token)

    override fun loadChat(): Chat {
        val type = vk.messages().getConversationsById(actor, chatId)
            .execute().items.joinToString { it.peer.type.name }
        return Chat(chatId.toLong(), chatId.toString(), type, loadAllMessages(chatId))
    }

    private fun loadAllMessages(peerId: Int) = buildList {
        val countMessByStep = 200 // max
        var offset = 0

        while (true) {
            val history = vk.messages()
                .getHistoryExtended(actor)
                .userId(peerId)
                .rev(GetHistoryRev.CHRONOLOGICAL)
                .count(countMessByStep)
                .offset(offset)
                .execute()

            addAll(history.items
                .map { toMessage(it, history) })

            if (history.items.size < countMessByStep) break
            offset += countMessByStep
        }
    }

    private fun toMessage(vkMess: VkMessage, history: GetHistoryExtendedResponse) =
        Message(
            id = vkMess.id.toLong(),
            type = "message", //todo

            date = Instant.fromEpochSeconds(
                vkMess.date.toLong()
            ).toLocalDateTime(TimeZone.UTC),

            from = history.profiles
                .firstOrNull { it.id == vkMess.fromId }
                ?.run { "$firstName $lastName" }
                ?: history.groups
                    .first { -it.id == vkMess.fromId }.name,

            fromId = vkMess.fromId.toString(),
            replyTo = vkMess.replyMessage?.id?.toLong(),
            isForwarded = vkMess.fwdMessages.isNotEmpty(),
            text = Text(listOf(TextItem(vkMess.text))),
            attachments = vkMess.attachments.map { Media(it.type?.toString() ?: "null") },
            durationSeconds = vkMess.attachments.firstOrNull { it.type == MessageAttachmentType.AUDIO_MESSAGE }?.audioMessage?.duration
        )
}
