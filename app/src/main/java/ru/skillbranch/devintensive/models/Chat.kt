package ru.skillbranch.devintensive.models

/**
 * @author Space
 * @date 29.06.2019
 */

class Chat(
    val id: String,
    val members: MutableList<User> = mutableListOf(),
    val messages: MutableList<BaseMessage> = mutableListOf())