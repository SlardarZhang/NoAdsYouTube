package net.slardar.widget

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import net.slardar.noadsyoutube.R


class FragmentIcon : RelativeLayout {

    private lateinit var imageView: ImageView
    private lateinit var textView: TextView

    constructor(context: Context?) : super(context) {
        setView(this.context, null)
    }

    constructor(context: Context?, attrs: AttributeSet? = null) : super(context, attrs) {
        setView(this.context, attrs)
    }

    constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setView(this.context, attrs)
    }

    private fun setView(context: Context?, attrs: AttributeSet?) {

        val baseLayout: RelativeLayout = View.inflate(context, R.layout.fragment_icon, null) as RelativeLayout

        imageView = baseLayout.getChildAt(0) as ImageView
        textView = baseLayout.getChildAt(1) as TextView

        baseLayout.removeAllViews()

        this.addView(imageView)
        this.addView(textView)


        if (attrs != null) {
            val attrArray: TypedArray? = context?.obtainStyledAttributes(attrs, R.styleable.FragmentIcon)
            if (attrArray != null) {
                setImage(attrArray.getDrawable(R.styleable.FragmentIcon_image))
                setText(attrArray.getText(R.styleable.FragmentIcon_text))
                setTextColor(attrArray.getColor(R.styleable.FragmentIcon_textColor, Color.BLACK))
                attrArray.recycle()
            }
        }
    }

    fun setText(text: CharSequence?) {
        if (text != null)
            textView.text = text
    }

    fun setText(text: String?) {
        if (text != null)
            textView.text = text
    }

    fun setTextColor(color: Int?) {
        if (color != null)
            textView.setTextColor(color)
    }

    fun setImage(drawable: Drawable?) {
        if (drawable != null)
            imageView.setImageDrawable(drawable)
    }


}

