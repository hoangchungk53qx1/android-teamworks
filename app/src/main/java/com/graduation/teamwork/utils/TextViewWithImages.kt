package com.graduation.teamwork.utils

import android.content.Context
import android.text.Spannable
import android.text.style.ImageSpan
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import java.util.regex.Pattern

class TextViewWithImages : AppCompatTextView {
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!,
        attrs,
        defStyle
    ) {
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {}
    constructor(context: Context?) : super(context!!) {}

    override fun setText(text: CharSequence, type: BufferType?) {
        val s = getTextWithImages(context, text)
        super.setText(s, BufferType.SPANNABLE)
    }

    companion object {
        private val spannableFactory = Spannable.Factory.getInstance()
        private fun addImages(context: Context, spannable: Spannable): Boolean {
            val refImg = Pattern.compile("\\Q[img src=\\E([a-zA-Z0-9_]+?)\\Q/]\\E")
            var hasChanges = false
            val matcher = refImg.matcher(spannable)
            while (matcher.find()) {
                var set = true
                for (span in spannable.getSpans(
                    matcher.start(), matcher.end(),
                    ImageSpan::class.java
                )) {
                    if (spannable.getSpanStart(span) >= matcher.start()
                        && spannable.getSpanEnd(span) <= matcher.end()
                    ) {
                        spannable.removeSpan(span)
                    } else {
                        set = false
                        break
                    }
                }
                val resname = spannable.subSequence(matcher.start(1), matcher.end(1)).toString()
                    .trim { it <= ' ' }
                val id = context.resources.getIdentifier(resname, "drawable", context.packageName)
                if (set) {
                    hasChanges = true
                    spannable.setSpan(
                        ImageSpan(context, id),
                        matcher.start(),
                        matcher.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
            return hasChanges
        }

        private fun getTextWithImages(context: Context, text: CharSequence): Spannable {
            val spannable = spannableFactory.newSpannable(text)
            addImages(context, spannable)
            return spannable
        }
    }
}