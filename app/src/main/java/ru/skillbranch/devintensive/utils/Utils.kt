package ru.skillbranch.devintensive.utils

object Utils {

    fun parseFullName(fullName: String?) = when {
        fullName.isNullOrBlank() -> null to null
        else -> with(fullName.replace("\\s+".toRegex(), " ").split(" ")) {
            getOrNull(0)?.run { if (isBlank()) null else this } to getOrNull(1)?.run { if (isBlank()) null else this }
        }
    }

    fun transliteration(payload: String, divider: String = " "): String {
        val abcCyr = charArrayOf(
                'а', 'б', 'в', 'г', 'д', 'е', 'ё', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о', 'п', 'р', 'с', 'т',
                'у', 'ф', 'х', 'ц', 'ч', 'ш', 'щ', 'ъ', 'ы', 'ь', 'э', 'ю', 'я',
                'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ё', 'Ж', 'З', 'И', 'Й', 'К', 'Л', 'М', 'Н', 'О', 'П', 'Р', 'С', 'Т',
                'У', 'Ф', 'Х', 'Ц', 'Ч', 'Ш', 'Щ', 'Ъ', 'Ы', 'Ь', 'Э', 'Ю', 'Я'
        )
        val abcLat = arrayOf(
                "a", "b", "v", "g", "d", "e", "e", "zh", "z", "i", "i", "k", "l", "m", "n", "o", "p", "r", "s", "t",
                "u", "f", "h", "c", "ch", "sh", "sh'", "", "i", "", "e", "yu", "ya",
                "A", "B", "V", "G", "D", "E", "E", "Zh", "Z", "I", "I", "K", "L", "M", "N", "O", "P", "R", "S", "T",
                "U", "F", "H", "C", "Ch", "Sh", "Sh'", "", "I", "", "E", "Yu", "Ya"
        )
        val stringBuilder = StringBuilder()
        for (i in 0 until payload.length) {
            for (j in abcCyr.indices)
                if (payload[i] == abcCyr[j]) {
                    stringBuilder.append(abcLat[j])
                    break
                } else if (j == abcCyr.size - 1) stringBuilder.append(payload[i])
        }
        val str = stringBuilder.toString()
        return if (divider != " ") str.replace(" ", divider) else str
    }

    fun toInitials(firstName: String?, lastName: String?) = when {
        firstName.isNullOrBlank() && lastName.isNullOrBlank() -> null
        else -> "${firstName?.trim()?.take(1) ?: ""}${lastName?.trim()?.take(1) ?: ""}".toUpperCase()
    }
}

inline fun <T, Z, R> with(receiver: T, receiver2: Z, block: (T, Z) -> R): R = block(receiver, receiver2)

inline fun <reified T : Enum<T>> T.next(): T {
    val values = enumValues<T>()
    val nextOrdinal = (ordinal + 1) % values.size
    return values[nextOrdinal]
}