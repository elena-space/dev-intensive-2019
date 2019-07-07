package ru.skillbranch.devintensive.extensions

/**
 * @author Space
 * @date 07.07.2019
 */

fun String.truncate(till: Int = 16) = trim().run {
    when {
        length <= till -> this
        else -> substring(0 until till).trim().plus("...")
    }
}

fun String.stripHtml() = replace("&(?:[a-z\\d]+|#\\d+|#x[a-f\\d]+);".toRegex(), "")
    .replace("<(.|\\n)*?>".toRegex(), "")
    .replace("[ ]+".toRegex(), " ")