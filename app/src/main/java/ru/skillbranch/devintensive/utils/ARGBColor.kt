package ru.skillbranch.devintensive.utils

/**
 * @author Space
 * @date 19.07.2019
 */

class ARGBColor(val r: Int, val g: Int, val b: Int) {

    init {
        if (r !in 0..255 || g !in 0..255 || b !in 0..255) throw IllegalArgumentException("'r' 'g' 'b' values should be in range from 0 to 255")
    }

    fun toRgbInt() = android.graphics.Color.rgb(r, g, b)

    companion object {
        val WHITE = ARGBColor(255, 255, 255)
        val ORANGE = ARGBColor(255, 120, 0)
        val RED = ARGBColor(255, 0, 60)
        val RED_BRIGHT = ARGBColor(255, 0, 0)
    }
}