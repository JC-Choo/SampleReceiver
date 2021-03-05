package kr.co.receiver.etc

import android.content.Context
import android.widget.Toast

fun Context.toast(message: String, duration: Int = 0) = Toast.makeText(this, message, duration).show()

val Any.TAG: String
    get() = this::class.simpleName ?: this.toString()

fun Context.openApp() {
    val intent = packageManager.getLaunchIntentForPackage(packageName)
    startActivity (intent)
}