package ru.skillbranch.devintensive.extensions

import ru.skillbranch.devintensive.models.User
import ru.skillbranch.devintensive.models.UserView
import ru.skillbranch.devintensive.utils.Utils

fun User.toUserView(): UserView {
    val nickname = Utils.transliteration("$firstName $lastName")
    val initials = Utils.toInitials(firstName, lastName)
    val status = lastVisit?.run { if (isOnline) "online" else "Последний раз был ${humanizeDiff()}" }
            ?: "Еще ни разу не был"

    return UserView(
            id = id,
            fullName = "$firstName $lastName",
            nickName = nickname,
            initials = initials,
            avatar = avatar,
            status = status
    )
}