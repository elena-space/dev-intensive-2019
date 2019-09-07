package ru.skillbranch.devintensive.extensions

import ru.skillbranch.devintensive.models.UserView
import ru.skillbranch.devintensive.models.data.User
import ru.skillbranch.devintensive.models.data.UserItem
import ru.skillbranch.devintensive.utils.Utils

fun User.toUserView() = UserView(
        id = id,
        fullName = fullName,
        nickName = Utils.transliteration("$firstName $lastName"),
        initials = Utils.toInitials(firstName, lastName),
        avatar = avatar,
        status = lastVisit?.run { if (isOnline) "online" else "Последний раз был ${humanizeDiff()}" }
                ?: "Еще ни разу не был")

fun User.toUserItem() = UserItem(
        id = id,
        fullName = fullName,
        initials = Utils.toInitials(firstName, lastName),
        avatar = avatar,
        lastActivity = lastVisit?.run { if (isOnline) "online" else "Последний раз был ${humanizeDiff()}" } ?: "Еще ни разу не был",
        isSelected = false,
        isOnline = isOnline
)