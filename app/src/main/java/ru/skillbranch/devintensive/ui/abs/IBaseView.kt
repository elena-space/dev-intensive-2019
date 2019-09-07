package ru.skillbranch.devintensive.ui.abs

import android.view.View
import androidx.annotation.StringRes

/**
 * @author Space
 * @date 07.09.2019
 */

interface IBaseView {

    fun showSnackbar(text: String)

    fun showSnackbar(text: String, @StringRes stringRes: Int, action: (View) -> Unit)
}