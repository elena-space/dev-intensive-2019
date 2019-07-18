package ru.skillbranch.devintensive.models

import android.os.Bundle
import ru.skillbranch.devintensive.models.Bender.Question.NAME
import ru.skillbranch.devintensive.models.Bender.Status.NORMAL
import ru.skillbranch.devintensive.utils.ARGBColor
import ru.skillbranch.devintensive.utils.next

class Bender(private var currentStatus: Status = NORMAL,
             private var currentQuestion: Question = NAME) {

    constructor(bundle: Bundle?) : this() {
        bundle?.run {
            currentStatus = Status.valueOf(getString(KEY_STATUS, NORMAL.name))
            currentQuestion = Question.valueOf(getString(KEY_QUESTION, NAME.name))
        }
    }

    val currentColor get() = currentStatus.color

    fun askQuestion() = currentQuestion.text

    fun listenAnswer(answer: String): Pair<String, ARGBColor> = with(currentQuestion) {
        when {
            this == Question.IDLE -> text
            !isValid(answer) -> "$validationErrorMessage\n$text"
            isCorrectAnswer(answer) -> "Отлично - ты справился\n${run { ++currentQuestion }.text}"
            ++currentStatus != NORMAL -> "Это неправильный ответ\n$text"
            else -> resetQuestion().let { "Это неправильный ответ. Давай все по новой\n${it.text}" }
        } to currentStatus.color
    }

    fun saveState(bundle: Bundle?) = bundle?.apply {
        putString(KEY_STATUS, currentStatus.name)
        putString(KEY_QUESTION, currentQuestion.name)
    }

    private fun resetQuestion(): Question {
        currentQuestion = NAME
        return currentQuestion
    }

    enum class Status(val color: ARGBColor) {
        NORMAL(ARGBColor.WHITE),
        WARNING(ARGBColor.ORANGE),
        DANGER(ARGBColor.RED),
        CRITICAL(ARGBColor.RED_BRIGHT);

        operator fun inc() = next()
    }

    enum class Question(val text: String, private val possibleAnswers: List<String>, val isValid: (String) -> Boolean, val validationErrorMessage: String) {
        NAME("Как меня зовут?", listOf("бендер", "bender"),
                { it.firstOrNull()?.isUpperCase() ?: false }, "Имя должно начинаться с заглавной буквы"),

        PROFESSION("Назови мою профессию?", listOf("сгибальщик", "bender"),
                { it.firstOrNull()?.isLowerCase() ?: false }, "Профессия должна начинаться со строчной буквы"),

        MATERIAL("Из чего я сделан?", listOf("металл", "дерево", "metal", "iron", "wood"),
                { Regex("[A-Za-z_ /-]+").matches(it) }, "Материал не должен содержать цифр"),

        BDAY("Когда меня создали?", listOf("2993"),
                { Regex("\\d+").matches(it) }, "Год моего рождения должен содержать только цифры"),

        SERIAL("Мой серийный номер?", listOf("2716057"),
                { Regex("\\d{7}").matches(it) }, "Серийный номер содержит только цифры, и их 7"),

        IDLE("На этом все, вопросов больше нет", listOf(), { true }, "");

        fun isCorrectAnswer(answer: String) = possibleAnswers.contains(answer.toLowerCase())

        operator fun inc(): Question = with((ordinal + 1) % values().size) {
            if (this == 0) IDLE else values()[this]
        }
    }

    companion object {
        private const val KEY_QUESTION = "KEY_QUESTION"
        private const val KEY_STATUS = "KEY_STATUS"
    }
}