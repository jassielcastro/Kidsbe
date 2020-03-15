package com.payclip.design.extensions

import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import com.google.android.material.textfield.TextInputLayout

fun TextInputLayout.clear() {
    this.editText?.text = null
}

fun TextInputLayout.addOnTextChangeListener(change: (String?) -> Unit) = editText?.apply {
    addTextChangedListener(object : TextWatcher {

        private var textByDelay: Runnable = Runnable { change(editText?.text.toString()) }
        private val delaySearch = 1500L

        override fun afterTextChanged(p0: Editable?) {
            handler?.removeCallbacks(textByDelay)
            handler?.postDelayed(textByDelay, delaySearch)
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    })
}

fun TextInputLayout.setOnActionClickListener(listener: (String?) -> Unit, change: () -> Unit) = editText?.apply {
    setOnEditorActionListener { textView, actionId, event ->
        if ((actionId == EditorInfo.IME_ACTION_DONE) || ((event?.keyCode == KeyEvent.KEYCODE_ENTER) && (event.action == KeyEvent.ACTION_DOWN))) {
            change()
        }
        when (actionId) {
            EditorInfo.IME_ACTION_SEARCH -> {
                hideKeyboard()
                listener(textView.text?.toString())
            }
        }
        true
    }
}
