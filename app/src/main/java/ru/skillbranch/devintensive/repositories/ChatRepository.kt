package ru.skillbranch.devintensive.repositories

import ru.skillbranch.devintensive.data.managers.CacheManager
import ru.skillbranch.devintensive.models.data.Chat

object ChatRepository {

    private val chats = CacheManager.loadChats()

    fun loadChats() = chats

    fun find(chatId: String): Chat? = chats.value?.run { getOrNull(indexOfFirst { it.id == chatId }) }

    fun update(chat: Chat) {
        val currentChats = chats.value ?: return
        val index = currentChats.indexOfFirst { it.id == chat.id }
        if (index == -1) return
        val copy = currentChats.toMutableList()
        copy[index] = chat
        chats.value = copy
    }

    fun archiveItem(chatId: String) = run { update(find(chatId)?.copy(isArchived = true) ?: return) }

    fun restoreItem(chatId: String) = run { update(find(chatId)?.copy(isArchived = false) ?: return) }
}