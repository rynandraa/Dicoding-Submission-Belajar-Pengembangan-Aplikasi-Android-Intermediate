package com.example.storyapp.ui.custom_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.example.storyapp.R

class CustomButton : AppCompatButton {
    private lateinit var enableButton: Drawable
    private lateinit var disableButton: Drawable
    private var textColor: Int = 0

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        background = if (isEnabled) enableButton else disableButton

        setTextColor(textColor)
        textSize = 12f
        gravity = Gravity.CENTER
    }
    private fun init(){
        textColor = ContextCompat.getColor(context, android.R.color.background_light)
        enableButton = ContextCompat.getDrawable(context, R.drawable.bg_button) as Drawable
        disableButton = ContextCompat.getDrawable(context, R.drawable.bg_button_disable) as Drawable
    }
}