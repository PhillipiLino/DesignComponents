package com.phillipilino.basicviews

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.phillipilino.basehelpers.ResourcesHelper
import kotlinx.android.synthetic.main.notification_view.view.*

enum class NotificationType(val id: Int,
                            @DrawableRes val icon: Int,
                            @ColorRes val color: Int) {
    ERROR(0, R.drawable.ic_notification_error, R.color.ruby_dark),
    ATTENTION(1, R.drawable.ic_notification_attention, R.color.sunflower_dark),
    SUCCESS(2, R.drawable.ic_notification_success, R.color.bamboo_dark);

    companion object {
        private val all = listOf(ERROR, ATTENTION, SUCCESS)
        fun fromId(id: Int) = all.firstOrNull { it.id == id } ?: ERROR
    }
}

class NotificationView(context: Context, attrs: AttributeSet): LinearLayout(context, attrs) {
    private val resHelper = ResourcesHelper(context)
    var message: String
        get() = lbl_notification_message.text.toString()
        set(value) {
            lbl_notification_message.text = value
        }

    var type: NotificationType = NotificationType.ERROR
        set(value) {
            field = value
            setTypeProperties(value)
        }

    init {
        inflate(context, R.layout.notification_view, this)
        alpha = 0.0f

        val array = context.theme.obtainStyledAttributes(attrs, R.styleable.NotificationView, 0, 0)
        val initialMessage = array.getString(R.styleable.NotificationView_message) ?: ""
        val showOnStart = array.getBoolean(R.styleable.NotificationView_showOnStart, false)
        val attrType = array.getInt(R.styleable.NotificationView_type, 0)
        val initialType = NotificationType.fromId(attrType)

        setMessage(initialType, initialMessage)
        if (showOnStart) show()

        array.recycle()
    }

    private fun setTypeProperties(type: NotificationType) {
        val color = resHelper.getColorHelper(type.color)
        iv_notification_icon.setImageResource(type.icon)
        lbl_notification_message.setTextColor(color)
    }

    fun setMessage(type: NotificationType, message: String): NotificationView {
        this.type = type
        this.message = message
        return this
    }

    fun show() {
        animate()
            .alpha(1.0f)
            .setStartDelay(0)
            .setDuration(300)
            .withEndAction {
            animate().setStartDelay(1000).alpha(0.0f).setDuration(300).start()
        }.start()
    }
}