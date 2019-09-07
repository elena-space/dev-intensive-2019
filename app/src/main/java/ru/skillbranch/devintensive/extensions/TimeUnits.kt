package ru.skillbranch.devintensive.extensions

enum class TimeUnits(val durationInMs: Long, val one: String, val few: String, val many: String) {
    SECOND(1000L, "секунду", "секунды", "секунд"),
    MINUTE(60000L, "минуту", "минуты", "минут"),
    HOUR(3600000L, "час", "часа", "часов"),
    DAY(86400000L, "день", "дня", "дней");
}

fun TimeUnits.plural(value: Int): String = ru.skillbranch.devintensive.utils.with(value % 100 / 10, value % 10) { tens, ones ->
    when {
        tens == 1 || ones == 0 || ones >= 5 -> "$value $many"
        ones == 1 -> "$value $one"
        else -> "$value $few"
    }
}