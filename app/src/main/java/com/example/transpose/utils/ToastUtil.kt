package com.example.transpose.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.StringRes
import com.example.transpose.R

object ToastUtil {
    private var toast: Toast? = null
    private val handler = Handler(Looper.getMainLooper())

    fun showShort(context: Context, message: String) {
        showToast(context, message, Toast.LENGTH_SHORT)
    }

    fun showShort(context: Context, @StringRes messageResId: Int) {
        showToast(context, context.getString(messageResId), Toast.LENGTH_SHORT)
    }

    fun showLong(context: Context, message: String) {
        showToast(context, message, Toast.LENGTH_LONG)
    }

    fun showLong(context: Context, @StringRes messageResId: Int) {
        showToast(context, context.getString(messageResId), Toast.LENGTH_LONG)
    }

    fun showNotImplemented(context: Context) {
        showToast(context, context.getString(R.string.feature_not_implemented), Toast.LENGTH_SHORT)

        // 1초 후에 Toast 메시지를 취소
        handler.postDelayed({
            toast?.cancel()
        }, 1000)
    }

    private fun showToast(context: Context, message: String, duration: Int) {
        toast?.cancel()
        toast = Toast.makeText(context, message, duration)
        toast?.show()
    }
}