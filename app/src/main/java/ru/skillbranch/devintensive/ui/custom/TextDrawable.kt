package ru.skillbranch.devintensive.ui.custom

import android.graphics.*
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.graphics.drawable.shapes.RectShape
import android.graphics.drawable.shapes.RoundRectShape
import android.graphics.drawable.shapes.Shape
import kotlin.math.min

/**
 * @author Space
 * @date 01.08.2019
 */

class TextDrawable private constructor(private val text: String,
                                       font: Typeface,
                                       shape: Shape,
                                       textColor: Int,
                                       isBold: Boolean,
                                       bgColor: Int,
                                       borderColor: Int,
                                       private val borderThickness: Int,
                                       private val width: Int,
                                       private val height: Int,
                                       private val fontSize: Int,
                                       private val radius: Float) : ShapeDrawable(shape) {

    private val textPaint: Paint = Paint()
    private val borderPaint: Paint = Paint()

    init {
        textPaint.color = textColor
        textPaint.isAntiAlias = true
        textPaint.isFakeBoldText = isBold
        textPaint.style = Paint.Style.FILL
        textPaint.typeface = font
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.strokeWidth = borderThickness.toFloat()

        borderPaint.color = borderColor
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = borderThickness.toFloat()

        val paint = paint
        paint.color = bgColor
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        val r = bounds

        if (borderThickness > 0) drawBorder(canvas)

        val count = canvas.save()
        canvas.translate(r.left.toFloat(), r.top.toFloat())

        val width = if (this.width < 0) r.width() else this.width
        val height = if (this.height < 0) r.height() else this.height
        val fontSize = if (this.fontSize < 0) min(width, height) / 2 else this.fontSize
        textPaint.textSize = fontSize.toFloat()
        canvas.drawText(text, (width / 2).toFloat(), height / 2 - (textPaint.descent() + textPaint.ascent()) / 2, textPaint)
        canvas.restoreToCount(count)
    }

    private fun drawBorder(canvas: Canvas) {
        val rect = RectF(bounds)
        rect.inset((borderThickness / 2).toFloat(), (borderThickness / 2).toFloat())
        when (shape) {
            is OvalShape -> canvas.drawOval(rect, borderPaint)
            is RoundRectShape -> canvas.drawRoundRect(rect, radius, radius, borderPaint)
            else -> canvas.drawRect(rect, borderPaint)
        }
    }

    override fun setAlpha(alpha: Int) {
        textPaint.alpha = alpha
    }

    override fun setColorFilter(cf: ColorFilter?) {
        textPaint.colorFilter = cf
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    override fun getIntrinsicWidth(): Int = width

    override fun getIntrinsicHeight(): Int = height

    class Builder {

        private var text: String = ""

        private var bgColor = Color.GRAY

        private var borderThickness: Int = 0

        private var borderColor = Color.WHITE

        private var width: Int = -1

        private var height: Int = -1

        private var font = Typeface.create("sans-serif-light", Typeface.NORMAL)

        private var shape = RectShape()

        private var textColor = Color.WHITE

        private var fontSize: Int = -1

        private var isBold: Boolean = false

        private var toUpperCase: Boolean = false

        private var radius = 0f

        fun width(w: Int) = apply { width = w }

        fun height(h: Int) = apply { height = h }

        fun text(s: String) = apply { text = s }

        fun textColor(color: Int) = apply { textColor = color }

        fun backgroundColor(color: Int) = apply { bgColor = color }

        fun withBorder(thickness: Int) = apply { borderThickness = thickness }

        fun withBorder(thickness: Int, color: Int) = apply { borderThickness = thickness; borderColor = color }

        fun useFont(typeface: Typeface) = apply { font = typeface }

        fun fontSize(size: Int) = apply { fontSize = size }

        fun bold() = apply { isBold = true }

        fun toUpperCase() = apply { toUpperCase = true }

        fun rect() = apply { shape = RectShape() }

        fun round() = apply { shape = OvalShape() }

        fun roundRect(radius: Int) = apply {
            this.radius = radius.toFloat()
            val radii = floatArrayOf(radius.toFloat(), radius.toFloat(), radius.toFloat(), radius.toFloat(), radius.toFloat(), radius.toFloat(), radius.toFloat(), radius.toFloat())
            shape = RoundRectShape(radii, null, null)
        }

        fun build(): TextDrawable = TextDrawable(if (toUpperCase) text.toUpperCase() else text,
                font,
                shape,
                textColor,
                isBold,
                bgColor,
                borderColor,
                borderThickness,
                width,
                height,
                fontSize,
                radius)
    }
}