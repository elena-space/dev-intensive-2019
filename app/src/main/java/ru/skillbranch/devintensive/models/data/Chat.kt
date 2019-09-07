package ru.skillbranch.devintensive.models.data

import androidx.annotation.VisibleForTesting
import ru.skillbranch.devintensive.extensions.shortFormat
import ru.skillbranch.devintensive.models.BaseMessage
import ru.skillbranch.devintensive.models.ImageMessage
import ru.skillbranch.devintensive.models.TextMessage
import ru.skillbranch.devintensive.utils.ChatType
import ru.skillbranch.devintensive.utils.Utils

data class Chat(val id: String,
                val title: String,
                val members: List<User> = listOf(),
                var messages: MutableList<BaseMessage> = mutableListOf(),
                var isArchived: Boolean = false) {

    private fun isSingle() = members.size == 1

    fun toChatItem() = when {
        isSingle() -> with(members.first()) {
            ChatItem(id, avatar, Utils.toInitials(firstName, lastName) ?: "??", fullName, lastMessageShort().first,
                    unreadableMessageCount(), lastMessageDate()?.shortFormat(), isOnline)
        }
        else -> ChatItem(id, null, "", title, lastMessageShort().first, unreadableMessageCount(),
                lastMessageDate()?.shortFormat(), false, ChatType.GROUP, lastMessageShort().second)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun unreadableMessageCount() = messages.count { !it.isRead }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun lastMessageDate() = messages.lastOrNull()?.date

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun lastMessageShort(): Pair<String, String?> = when (val message = messages.lastOrNull()) {
        is TextMessage -> message.text to message.from.firstName
        is ImageMessage -> "${message.from.firstName} - отправил фото" to message.from.firstName
        else -> "" to null
    }
}