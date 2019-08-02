package ru.skillbranch.devintensive.ui.custom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.Dimension
import androidx.annotation.DrawableRes
import ru.skillbranch.devintensive.R
import kotlin.math.min
import kotlin.math.pow

/**
 * @author Space
 * @date 29.07.2019
 */

class CircleImageView : ImageView {

    constructor(context: Context) : super(context)

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet, defStyle: Int = 0) : super(context, attrs, defStyle) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView, defStyle, 0)
        borderWidthInDp = a.getDimensionPixelSize(R.styleable.CircleImageView_cv_borderWidth, DEFAULT_BORDER_WIDTH)
        borderColor = a.getColor(R.styleable.CircleImageView_cv_borderColor, DEFAULT_BORDER_COLOR)
        isBorderOverlay = a.getBoolean(R.styleable.CircleImageView_cv_borderOverlay, DEFAULT_BORDER_OVERLAY)
        circleBackgroundColor = a.getColor(R.styleable.CircleImageView_cv_circleBackgroundColor, DEFAULT_CIRCLE_BACKGROUND_COLOR)
        a.recycle()
    }

    private val drawableRect = RectF()
    private val borderRect = RectF()

    private val shaderMatrix = Matrix()
    private val bitmapPaint = Paint()
    private val borderPaint = Paint()
    private val circleBackgroundPaint = Paint()

    private var borderColor = DEFAULT_BORDER_COLOR
    private var borderWidthInDp = DEFAULT_BORDER_WIDTH
    private var circleBackgroundColor = DEFAULT_CIRCLE_BACKGROUND_COLOR

    private var bitmap: Bitmap? = null
    private var bitmapShader: BitmapShader? = null
    private var bitmapWidth: Int = 0
    private var bitmapHeight: Int = 0

    private var drawableRadius: Float = 0.toFloat()
    private var borderRadius: Float = 0.toFloat()

    private var colorFilter: ColorFilter? = null

    private var isReady: Boolean = false
    private var isSetupPending: Boolean = false
    private var isBorderOverlay: Boolean = false
    private var isDisableCircularTransformation: Boolean = false

    init {
        super.setScaleType(SCALE_TYPE)
        isReady = true
        outlineProvider = OutlineProvider()

        if (isSetupPending) {
            setup()
            isSetupPending = false
        }
    }

    fun getBorderColor(): Int = borderColor

    fun setBorderColor(hex: String) = setBorderIntColor(Color.parseColor(hex))

    fun setBorderColor(@ColorRes colorId: Int) {
        setBorderIntColor(resources.getColor(colorId, context.theme))
    }

    fun setBorderIntColor(@ColorInt color: Int) {
        if (color == borderColor) return
        borderColor = color
        borderPaint.color = borderColor
        invalidate()
    }

    @Dimension(unit = Dimension.DP)
    fun getBorderWidth() = borderWidthInDp

    fun setBorderWidth(@Dimension(unit = Dimension.DP) dp: Int) {
        if (dp == borderWidthInDp) return
        borderWidthInDp = dp
        setup()
    }

    fun getCircleBackgroundColor(): Int = this.circleBackgroundColor

    fun setCircleBackgroundColor(@ColorInt color: Int) {
        if (color == this.circleBackgroundColor) return
        this.circleBackgroundColor = color
        circleBackgroundPaint.color = color
        invalidate()
    }

    fun isBorderOverlay() = isBorderOverlay

    fun setBorderOverlay(enable: Boolean) {
        if (isBorderOverlay == enable) return
        isBorderOverlay = enable
        setup()
    }

    fun isDisableCircularTransformation() = isDisableCircularTransformation

    fun setDisableCircularTransformation(disable: Boolean) {
        if (isDisableCircularTransformation == disable) return
        isDisableCircularTransformation = disable
        initializeBitmap()
    }

    override fun getScaleType(): ScaleType = SCALE_TYPE

    override fun setScaleType(scaleType: ScaleType) {
        if (scaleType != SCALE_TYPE) throw IllegalArgumentException(String.format("ScaleType %s not supported.", scaleType))
    }

    override fun setAdjustViewBounds(adjustViewBounds: Boolean) {
        if (adjustViewBounds) {
            throw IllegalArgumentException("adjustViewBounds not supported.")
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (isDisableCircularTransformation) {
            super.onDraw(canvas)
            return
        }
        if (bitmap == null) return

        if (circleBackgroundColor != Color.TRANSPARENT)
            canvas.drawCircle(drawableRect.centerX(), drawableRect.centerY(), drawableRadius, circleBackgroundPaint)
        canvas.drawCircle(drawableRect.centerX(), drawableRect.centerY(), drawableRadius, bitmapPaint)
        if (borderWidthInDp > 0) canvas.drawCircle(borderRect.centerX(), borderRect.centerY(), borderRadius, borderPaint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setup()
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        super.setPadding(left, top, right, bottom)
        setup()
    }

    override fun setPaddingRelative(start: Int, top: Int, end: Int, bottom: Int) {
        super.setPaddingRelative(start, top, end, bottom)
        setup()
    }

    fun setCircleBackgroundColorResource(@ColorRes circleBackgroundRes: Int) {
        circleBackgroundColor = context.resources.getColor(circleBackgroundRes)
    }

    override fun setImageBitmap(bm: Bitmap) {
        super.setImageBitmap(bm)
        initializeBitmap()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        initializeBitmap()
    }

    override fun setImageResource(@DrawableRes resId: Int) {
        super.setImageResource(resId)
        initializeBitmap()
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        initializeBitmap()
    }

    override fun setColorFilter(filter: ColorFilter) {
        if (filter === colorFilter) return
        colorFilter = filter
        applyColorFilter()
        invalidate()
    }

    override fun getColorFilter(): ColorFilter? = colorFilter

    private fun applyColorFilter() {
        bitmapPaint.colorFilter = colorFilter
    }

    private fun getBitmapFromDrawable(drawable: Drawable?): Bitmap? = when (drawable) {
        null -> null
        is BitmapDrawable -> drawable.bitmap
        else -> try {
            val bitmap: Bitmap = if (drawable is ColorDrawable) {
                Bitmap.createBitmap(COLOR_DRAWABLE_DIMENSION, COLOR_DRAWABLE_DIMENSION, BITMAP_CONFIG)
            } else {
                Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, BITMAP_CONFIG)
            }
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun initializeBitmap() {
        if (isDisableCircularTransformation) bitmap = null else bitmap = getBitmapFromDrawable(drawable)
        setup()
    }

    private fun setup() {
        if (!isReady) {
            isSetupPending = true
            return
        }

        if (width == 0 && height == 0) return

        bitmap?.run {
            bitmapShader = BitmapShader(this, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

            bitmapPaint.isAntiAlias = true
            bitmapPaint.shader = bitmapShader

            borderPaint.style = Paint.Style.STROKE
            borderPaint.isAntiAlias = true
            borderPaint.color = borderColor
            val borderPixelWidth = borderWidthInDp * resources.displayMetrics.density
            borderPaint.strokeWidth = borderPixelWidth

            circleBackgroundPaint.style = Paint.Style.FILL
            circleBackgroundPaint.isAntiAlias = true
            circleBackgroundPaint.color = circleBackgroundColor

            bitmapHeight = height
            bitmapWidth = width

            borderRect.set(calculateBounds())
            borderRadius = min((borderRect.height() - borderPixelWidth) / 2.0f, (borderRect.width() - borderPixelWidth) / 2.0f)

            drawableRect.set(borderRect)
            if (!isBorderOverlay && borderPixelWidth > 0) drawableRect.inset(borderPixelWidth - 1.0f, borderPixelWidth - 1.0f)
            drawableRadius = min(drawableRect.height() / 2.0f, drawableRect.width() / 2.0f)

            applyColorFilter()
            updateShaderMatrix()
        }
        invalidate()
    }

    private fun calculateBounds(): RectF {
        val availableWidth = width - paddingLeft - paddingRight
        val availableHeight = height - paddingTop - paddingBottom

        val sideLength = min(availableWidth, availableHeight)

        val left = paddingLeft + (availableWidth - sideLength) / 2f
        val top = paddingTop + (availableHeight - sideLength) / 2f

        return RectF(left, top, left + sideLength, top + sideLength)
    }

    private fun updateShaderMatrix() {
        val scale: Float
        var dx = 0f
        var dy = 0f

        shaderMatrix.set(null)

        if (bitmapWidth * drawableRect.height() > drawableRect.width() * bitmapHeight) {
            scale = drawableRect.height() / bitmapHeight.toFloat()
            dx = (drawableRect.width() - bitmapWidth * scale) * 0.5f
        } else {
            scale = drawableRect.width() / bitmapWidth.toFloat()
            dy = (drawableRect.height() - bitmapHeight * scale) * 0.5f
        }

        shaderMatrix.setScale(scale, scale)
        shaderMatrix.postTranslate((dx + 0.5f).toInt() + drawableRect.left, (dy + 0.5f).toInt() + drawableRect.top)

        bitmapShader?.setLocalMatrix(shaderMatrix)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean = inTouchableArea(event.x, event.y) && super.onTouchEvent(event)

    private fun inTouchableArea(x: Float, y: Float): Boolean = (x - borderRect.centerX()).toDouble().pow(2.0) +
            (y - borderRect.centerY()).toDouble().pow(2.0) <= borderRadius.toDouble().pow(2.0)

    private inner class OutlineProvider : ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline) {
            val bounds = Rect()
            borderRect.roundOut(bounds)
            outline.setRoundRect(bounds, bounds.width() / 2.0f)
        }
    }

    companion object {
        private val SCALE_TYPE = ScaleType.CENTER_CROP
        private val BITMAP_CONFIG = Bitmap.Config.ARGB_8888
        private const val COLOR_DRAWABLE_DIMENSION = 2
        private const val DEFAULT_BORDER_WIDTH = 2
        private const val DEFAULT_BORDER_COLOR = Color.WHITE
        private const val DEFAULT_CIRCLE_BACKGROUND_COLOR = Color.TRANSPARENT
        private const val DEFAULT_BORDER_OVERLAY = false
    }
}