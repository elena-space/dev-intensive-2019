package ru.skillbranch.devintensive.models

import ru.skillbranch.devintensive.utils.Utils

data class Profile(val firstName: String,
                   val lastName: String,
                   val about: String,
                   val repository: String,
                   val rating: Int = 0,
                   val respect: Int = 0) {

    val nickName: String = if (firstName.plus(lastName).isNotBlank()) Utils.transliteration("$firstName $lastName", "_") else ""
    val rank: String = "Junior Android Developer"

    fun toMap() = mapOf(
            "firstName" to firstName,
            "lastName" to lastName,
            "about" to about,
            "repository" to repository,
            "rating" to rating,
            "respect" to respect,
            "nickName" to nickName,
            "rank" to rank)
}