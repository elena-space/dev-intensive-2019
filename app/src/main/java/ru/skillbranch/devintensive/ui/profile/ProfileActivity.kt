package ru.skillbranch.devintensive.ui.profile

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_profile.*
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.extensions.themedColorAccent
import ru.skillbranch.devintensive.models.Profile
import ru.skillbranch.devintensive.ui.custom.TextDrawable
import ru.skillbranch.devintensive.utils.Utils
import ru.skillbranch.devintensive.viewmodels.ProfileViewModel
import ru.skillbranch.devintensive.viewmodels.interfaces.IProfileViewModel
import kotlin.properties.Delegates


class ProfileActivity : AppCompatActivity() {

    private var isEditMode = false

    private val infoFields by lazy { mapOf("firstName" to et_first_name, "lastName" to et_last_name, "about" to et_about, "repository" to et_repository) }

    private val viewFields by lazy { infoFields + mapOf("nickName" to tv_nick_name, "rank" to tv_rank, "rating" to tv_rating, "respect" to tv_respect) }

    private var viewModel: IProfileViewModel by Delegates.notNull()

    private val repositoryErrorText by lazy { getString(R.string.error_repo_url_is_not_valid) }
    private val iconSave by lazy { resources.getDrawable(R.drawable.ic_save_white_24dp, theme) }
    private val iconEdit by lazy { resources.getDrawable(R.drawable.ic_edit_day_night, theme) }
    private val avatarPixelSize by lazy { resources.getDimensionPixelSize(R.dimen.profile_rounded_avatar_size) }
    private val avatarFontPixelSize by lazy { resources.getDimensionPixelSize(R.dimen.font_56) }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_costraint)
        initViews(savedInstanceState)
        initViewModel()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putBoolean(IS_EDIT_MODE, isEditMode)
        super.onSaveInstanceState(outState)
    }

    private fun initViews(state: Bundle?) {
        isEditMode = state?.getBoolean(IS_EDIT_MODE, false) ?: false
        btn_edit.setOnClickListener { switchEditMode() }
        btn_switch_theme.setOnClickListener { viewModel.switchTheme() }
        updateUIForCurrentMode(isEditMode)
        et_repository.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) = viewModel.onRepositoryChanged(s.toString())
        })
        et_repository.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event?.run { action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER } == true) {
                switchEditMode()
                true
            } else {
                false
            }
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        viewModel.profileData.observe(this, Observer { updateUI(it) })
        viewModel.appTheme.observe(this, Observer { updateTheme(it) })
        viewModel.isRepositoryErrorVisible.observe(this, Observer { if (isEditMode) updateRepositoryError(it) })
    }

    private fun updateTheme(mode: Int) = delegate.setLocalNightMode(mode)

    private fun updateUI(profile: Profile) {
        profile.toMap().also { for ((k, v) in viewFields) v.text = it[k].toString() }
        Utils.toInitials(profile.firstName, profile.lastName)?.also {
            iv_avatar.setImageDrawable(TextDrawable.Builder()
                    .text(it)
                    .textColor(Color.WHITE)
                    .backgroundColor(themedColorAccent)
                    .fontSize(avatarFontPixelSize)
                    .width(avatarPixelSize)
                    .height(avatarPixelSize)
                    .bold()
                    .round()
                    .build())
        }
    }

    private fun updateRepositoryError(isErrorVisible: Boolean) {
        wr_repository.isErrorEnabled = isErrorVisible
        wr_repository.error = if (isErrorVisible) repositoryErrorText else null
    }

    private fun switchEditMode() {
        if (isEditMode) saveProfileInfo()
        isEditMode = !isEditMode
        updateUIForCurrentMode(isEditMode)
    }

    private fun updateUIForCurrentMode(isEditMode: Boolean) {
        for ((_, v) in infoFields) {
            v.isFocusable = isEditMode
            v.isFocusableInTouchMode = isEditMode
            v.isEnabled = isEditMode
            v.background.alpha = if (isEditMode) 255 else 0
        }
        ic_eye.visibility = if (isEditMode) View.GONE else View.VISIBLE
        wr_about.isCounterEnabled = isEditMode
        wr_repository.isErrorEnabled = isEditMode
        with(btn_edit) {
            background.colorFilter = if (isEditMode) PorterDuffColorFilter(themedColorAccent, PorterDuff.Mode.SRC_IN) else null
            setImageDrawable(if (isEditMode) iconSave else iconEdit)
        }
    }

    private fun saveProfileInfo() = viewModel.saveProfileData(
            Profile(firstName = et_first_name.text.toString(),
                    lastName = et_last_name.text.toString(),
                    about = et_about.text.toString(),
                    repository = et_repository.text.toString()))

    companion object {
        const val IS_EDIT_MODE = "IS_EDIT_MODE"
    }
}