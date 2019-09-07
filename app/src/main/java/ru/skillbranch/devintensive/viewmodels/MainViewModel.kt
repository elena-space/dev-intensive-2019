package ru.skillbranch.devintensive.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import ru.skillbranch.devintensive.extensions.mutableLiveData
import ru.skillbranch.devintensive.models.data.ChatItem
import ru.skillbranch.devintensive.repositories.ChatRepository

class MainViewModel : ViewModel() {

    private val query = mutableLiveData("")
    private val chatRepository = ChatRepository

    private val chats = Transformations.map(chatRepository.loadChats()) { chats ->
        chats.filter { !it.isArchived }
                .map { it.toChatItem() }
                .sortedBy { it.id.toInt() }
    }

    fun getChatData(): LiveData<List<ChatItem>> {
        val result = MediatorLiveData<List<ChatItem>>()

        val filter = {
            val queryStr = query.value!!
            val currentChats = chats.value!!

            result.value = if (queryStr.isEmpty()) currentChats else currentChats.filter { it.title.contains(queryStr, true) }
        }

        result.addSource(chats) { filter.invoke() }
        result.addSource(query) { filter.invoke() }

        return result
    }

    fun addToArchive(chatId: String) = chatRepository.archiveItem(chatId)

    fun restoreFromArchive(chatId: String) = chatRepository.restoreItem(chatId)

    fun handleSearchQuery(text: String?) {
        query.value = text
    }
}