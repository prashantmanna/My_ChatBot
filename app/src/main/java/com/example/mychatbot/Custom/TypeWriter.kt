package com.example.mychatbot.Custom

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class TypeWriter : AppCompatTextView {
    private var mText: CharSequence? = null
    private var index = 0
    private var delay: Long = 500
    private val handler = Handler()
    private val characterAdder: Runnable = object : Runnable {
        override fun run() {
            text = mText!!.subSequence(0, index++)
            if (index <= mText!!.length) {
                handler.postDelayed(this, delay)
            }
        }
    }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        // Initialization code here if needed
    }

    fun animateText(text: CharSequence?) {
        mText = text
        index = 0
        setText("")
        handler.removeCallbacks(characterAdder)
        handler.postDelayed(characterAdder, delay)
    }

    fun setCharacterDelay(millis: Long) {
        delay = millis
    }
}
