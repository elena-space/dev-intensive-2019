package ru.skillbranch.devintensive.extensions

import ru.skillbranch.devintensive.models.User
import ru.skillbranch.devintensive.models.UserView
import ru.skillbranch.devintensive.utils.Utils

fun User.toUserView() = UserView(
        id = id,
        fullName = "$firstName $lastName",
        nickName = Utils.transliteration("$firstName $lastName"),
        initials = Utils.toInitials(firstName, lastName),
        avatar = avatar,
        status = lastVisit?.run { if (isOnline) "online" else "Последний раз был ${humanizeDiff()}" }
                ?: "Еще ни разу не был")