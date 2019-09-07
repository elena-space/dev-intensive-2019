package ru.skillbranch.devintensive.ui.abs

import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar
import ru.skillbranch.devintensive.ui.abs.factory.IActivityFactory

/**
 * @author Space
 * @date 07.09.2019
 */

abstract class BaseActivity : AppCompatActivity(), IBaseView, IActivityFactory {

    abstract val coordinator: CoordinatorLayout

    override fun showSnackbar(text: String) = Snackbar.make(coordinator, text, Snackbar.LENGTH_LONG).show()

    override fun showSnackbar(text: String, @StringRes stringRes: Int, action: (View) -> Unit) =
            Snackbar.make(coordinator, text, Snackbar.LENGTH_LONG).setAction(stringRes, action).show()
}