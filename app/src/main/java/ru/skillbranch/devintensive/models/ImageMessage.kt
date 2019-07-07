package ru.skillbranch.devintensive.models

import ru.skillbranch.devintensive.extensions.humanizeDiff
import java.util.*

/**
 * @author Space
 * @date 29.06.2019
 */

class ImageMessage(
    id: String,
    from: User?,
    chat: Chat,
    isIncoming: Boolean,
    date: Date = Date(),
    var image: String?) : BaseMessage(id, from, chat, isIncoming, date) {

    override fun formatMessage() = "id:$id ${from?.firstName} ${if (isIncoming) "получил" else "отправил"} изображение \"$image\" ${date.humanizeDiff()}"
}