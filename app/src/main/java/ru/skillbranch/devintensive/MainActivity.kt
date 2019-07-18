package ru.skillbranch.devintensive

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import ru.skillbranch.devintensive.extensions.hideKeyboard
import ru.skillbranch.devintensive.models.Bender
import ru.skillbranch.devintensive.utils.ARGBColor
import kotlin.properties.Delegates


class MainActivity : AppCompatActivity() {

    private val benderImage: ImageView by lazy { iv_bender }
    private val tvText: TextView by lazy { tv_text }
    private val messageEt: EditText by lazy { et_message }
    private val sendBtn: ImageView by lazy { iv_send }
    private var benderObj: Bender by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        benderObj = Bender(savedInstanceState)
        applyColorFilterToBenderImage(benderObj.currentColor)
        tvText.text = benderObj.askQuestion()
        sendBtn.setOnClickListener { handleAnswer() }
        messageEt.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event?.run { action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER } == true) {
                handleAnswer()
                true
            } else {
                false
            }
        }
    }

    override fun onSaveInstanceState(b: Bundle?) {
        super.onSaveInstanceState(benderObj.saveState(b))
    }

    private fun handleAnswer() = with(benderObj.listenAnswer(messageEt.text.toString())) {
        hideKeyboard()
        messageEt.text.clear()
        tvText.text = first
        applyColorFilterToBenderImage(second)
    }

    private fun applyColorFilterToBenderImage(color: ARGBColor) {
        benderImage.setColorFilter(color.toRgbInt(), PorterDuff.Mode.MULTIPLY)
    }
}