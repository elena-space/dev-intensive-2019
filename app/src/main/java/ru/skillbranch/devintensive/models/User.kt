package ru.skillbranch.devintensive.models

import ru.skillbranch.devintensive.utils.Utils
import java.util.*

data class User(val id: String,
                var firstName: String?,
                var lastName: String?,
                var avatar: String?,
                var rating: Int = 0,
                var respect: Int = 0,
                var lastVisit: Date? = null,
                var isOnline: Boolean = false) {

    constructor(id: String, firstName: String?, lastName: String?) : this(id, firstName, lastName, null)

    constructor(id: String) : this(id, "John", "Doe $id")

    class Builder {
        var id = ""
        var firstName: String? = null
        var lastName: String? = null
        var avatar: String? = null
        var rating: Int = 0
        var respect: Int = 0
        var lastVisit: Date? = null
        var isOnline: Boolean = false

        fun id(s: String) = apply { id = s }
        fun firstName(s: String?) = apply { firstName = s }
        fun lastName(s: String?) = apply { lastName = s }
        fun avatar(s: String?) = apply { avatar = s }
        fun rating(n: Int) = apply { rating = n }
        fun respect(n: Int) = apply { respect = n }
        fun lastVisit(d: Date?) = apply { lastVisit = d }
        fun isOnline(b: Boolean) = apply { isOnline = b }

        fun build() = User(
            if (id.isBlank()) "${++lastId}" else id,
            firstName,
            lastName,
            avatar,
            rating,
            respect,
            lastVisit,
            isOnline)
    }

    companion object Factory {
        private var lastId = -1

        fun makeUser(fullName: String?): User = with(Utils.parseFullName(fullName)) {
            User("${++lastId}", first, second)
        }
    }
}