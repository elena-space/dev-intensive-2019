package ru.skillbranch.devintensive.ui.custom

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import androidx.core.content.res.use
import ru.skillbranch.devintensive.R

/**
 * @author Space
 * @date 27.07.2019
 */

@SuppressLint("Recycle")
class AspectRatioImageView @JvmOverloads constructor(context: Context,
                                                     attrs: AttributeSet? = null,
                                                     defStyleAttr: Int = 0) : ImageView(context, attrs, defStyleAttr) {

    private var aspectRatio = DEFAULT_ASPECT_RATIO
    private var keepWidthUnchanged = true

    init {
        attrs?.run {
            context.obtainStyledAttributes(this, R.styleable.AspectRatioImageView).use {
                aspectRatio = it.getFloat(R.styleable.AspectRatioImageView_aspectRatio, DEFAULT_ASPECT_RATIO)
                keepWidthUnchanged = it.getBoolean(R.styleable.AspectRatioImageView_keepWidthUnchanged, true)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        when {
            keepWidthUnchanged -> setMeasuredDimension(measuredWidth, (measuredWidth / aspectRatio).toInt())
            else -> setMeasuredDimension((measuredHeight / aspectRatio).toInt(), measuredHeight)
        }
    }

    companion object {
        private const val DEFAULT_ASPECT_RATIO = 1.78f
    }
}