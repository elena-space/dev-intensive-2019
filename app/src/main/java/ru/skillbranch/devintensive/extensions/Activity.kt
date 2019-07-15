package ru.skillbranch.devintensive.extensions

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import kotlin.math.roundToInt


/**
 * @author Space
 * @date 14.07.2019
 */

fun Activity.getRootView(): View = findViewById(android.R.id.content)

fun Context.dpToPx(dp: Float): Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).roundToInt()

fun Activity.isKeyboardOpen(): Boolean {
    val visibleBounds = Rect().apply { getRootView().getWindowVisibleDisplayFrame(this) }
    return getRootView().height - visibleBounds.height() > dpToPx(128F)
}

fun Activity.isKeyboardClosed() = !isKeyboardOpen()

fun Activity.hideKeyboard() {
    if (isKeyboardClosed()) return
    val view = currentFocus ?: return
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}