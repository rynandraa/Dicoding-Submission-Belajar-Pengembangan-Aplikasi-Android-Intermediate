package com.example.storyapp.ui.custom_view

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.storyapp.R


class   CustomNama : AppCompatEditText, View.OnTouchListener {
    var isNameValid: Boolean = false
    private lateinit var IconName: Drawable

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        IconName = ContextCompat.getDrawable(context, R.drawable.ic_person_otline) as Drawable
        onShowVisibilityIcon(IconName)
        setHint(R.string.name)
    }


    private fun onShowVisibilityIcon(icon: Drawable) {
        setButtonDrawables(startOfTheText = icon)
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null) {

        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText, topOfTheText,
            endOfTheText, bottomOfTheText
        )
    }

    private fun checkName() {
        val email = text?.trim()
        if (email.isNullOrEmpty()) {
            isNameValid = false
            error = resources.getString(R.string.fill_name)
        }else {
            isNameValid = true
        }
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        if (!focused) checkName()
    }
    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        return false
    }
}
