package ru.skillbranch.devintensive.models

import ru.skillbranch.devintensive.models.Bender.Question.NAME
import ru.skillbranch.devintensive.models.Bender.Status.NORMAL
import ru.skillbranch.devintensive.utils.next

class Bender(var currentStatus: Status = NORMAL,
             var currentQuestion: Question = NAME) {

    fun askQuestion() = currentQuestion.text

    fun listenAnswer(answer: String): Pair<String, Color> = with(currentQuestion) {
        when {
            this == Question.IDLE -> text
            !isValid(answer) -> "$validationErrorMessage\n$text"
            isCorrectAnswer(answer) -> "Отлично - ты справился\n${run { ++currentQuestion }.text}"
            ++currentStatus != NORMAL -> "Это неправильный ответ\n$text"
            else -> resetQuestion().let { "Это неправильный ответ. Давай все по новой\n${it.text}" }
        } to currentStatus.color
    }

    private fun resetQuestion(): Question {
        currentQuestion = NAME
        return currentQuestion
    }

    class Color(val r: Int, val g: Int, val b: Int) {
        companion object {
            val WHITE = Color(255, 255, 255)
            val ORANGE = Color(255, 120, 0)
            val RED = Color(255, 0, 60)
            val RED_BRIGHT = Color(255, 0, 0)
        }
    }

    enum class Status(val color: Color) {
        NORMAL(Color.WHITE),
        WARNING(Color.ORANGE),
        DANGER(Color.RED),
        CRITICAL(Color.RED_BRIGHT);

        operator fun inc() = next()
    }

    enum class Question(val text: String, private val possibleAnswers: List<String>, val isValid: (String) -> Boolean, val validationErrorMessage: String) {
        NAME("Как меня зовут?", listOf("бендер", "bender"),
                { it.isNotEmpty() && it[0].isUpperCase() }, "Имя должно начинаться с заглавной буквы"),

        PROFESSION("Назови мою профессию?", listOf("сгибальщик", "bender"),
                { it.isNotEmpty() && it[0].isLowerCase() }, "Профессия должна начинаться со строчной буквы"),

        MATERIAL("Из чего я сделан?", listOf("металл", "дерево", "metal", "iron", "wood"),
                { it.isNotEmpty() && !it.contains(Regex("\\d")) }, "Материал не должен содержать цифр"),

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
        const val KEY_QUESTION = "KEY_QUESTION"
        const val KEY_STATUS = "KEY_STATUS"
    }
}