package analmess.loader.vkapi

import analmess.*
import analmess.loader.Loader
import com.vk.api.sdk.client.VkApiClient
import com.vk.api.sdk.client.actors.UserActor
import com.vk.api.sdk.httpclient.HttpTransportClient
import com.vk.api.sdk.objects.messages.GetHistoryRev
import com.vk.api.sdk.objects.messages.MessageAttachmentType
import com.vk.api.sdk.objects.messages.responses.GetConversationMembersResponse
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class VkApi(myId: Int, token: String, private val chatId: Int) : Loader {
    private val vk = VkApiClient(HttpTransportClient())
    private val actor = UserActor(myId, token)

    override fun loadChat(): Chat {
        //todo type
        return Chat(chatId.toLong(), chatId.toString(), "", loadAllMessages(chatId))
    }

    private fun loadAllMessages(peerId: Int) = buildList {
        val countMessByStep = 200
        var offset = 0

        val members = vk.messages().getConversationMembers(actor, peerId).execute()

        while (true) {
            val history = vk.messages()
                .getHistory(actor)
                .userId(peerId)
                .rev(GetHistoryRev.CHRONOLOGICAL)
                .count(countMessByStep)
                .offset(offset)
                .extended(false)
                .execute()

            if (history.items.isEmpty()) break

            addAll(history.items.map { toMessage(it, members) })
            offset += countMessByStep
        }
    }

    private fun toMessage(vkMess: com.vk.api.sdk.objects.messages.Message, members: GetConversationMembersResponse) =
        Message(
            id = vkMess.id.toLong(),
            type = "message",

            date = Instant.fromEpochSeconds(
                vkMess.date.toLong()
            ).toLocalDateTime(TimeZone.UTC),

            from = members.profiles
                .firstOrNull { it.id == vkMess.fromId }
                ?.run { "$firstName $lastName" }
                ?: members.groups
                    .first { -it.id == vkMess.fromId }.name,

            fromId = vkMess.fromId.toString(),
            replyTo = vkMess.replyMessage?.id?.toLong(),
            isForwarded = vkMess.fwdMessages.isNotEmpty(),
            text = Text(listOf(TextItem(vkMess.text))),
            attachments = vkMess.attachments.map { Media(it.type?.toString() ?: "null") },
            durationSeconds = vkMess.attachments.firstOrNull { it.type == MessageAttachmentType.AUDIO_MESSAGE }?.audioMessage?.duration
        )
}
