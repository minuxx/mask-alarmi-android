package com.minux.mask_alarmi.util

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity

class InputUtil(private val activity: AppCompatActivity) {
    fun showSoftInput(view: View, flags: Int = InputMethodManager.SHOW_IMPLICIT) {
        view.requestFocus()
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.showSoftInput(view, flags)
    }

    fun hideSoftInput() {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        val currentFocusView = activity.currentFocus
        currentFocusView?.let { imm?.hideSoftInputFromWindow(it.windowToken, 0) }
    }
}