package kr.co.receiver.etc

import android.content.Context
import android.content.SharedPreferences
import java.util.*

class AppPrefUtil(
    private val context: Context
) {

    protected val mPref: SharedPreferences by lazy { context.getSharedPreferences(getPreferenceName(context), Context.MODE_PRIVATE) }

    protected fun getPreferenceName(context: Context): String {
        return context.applicationInfo.className
    }

    fun getString(key: String): String? {
        return getString(key, "")
    }

    fun getString(key: String, defValue: String): String? = mPref.getString(key, defValue)

    fun put(key: String, value: String) {
        val prefEditor: SharedPreferences.Editor = mPref.edit()
        prefEditor.putString(key, value)
        prefEditor.apply()
    }

    fun getBoolean(key: String): Boolean = getBoolean(key, false)

    fun getBoolean(key: String, defValue: Boolean): Boolean = mPref.getBoolean(key, defValue)

    fun put(key: String, value: Boolean) {
        val prefEditor: SharedPreferences.Editor = mPref.edit()
        prefEditor.putBoolean(key, value)
        prefEditor.apply()
    }

    fun getInt(key: String): Int = mPref.getInt(key, 0)

    fun put(key: String, value: Int) {
        val prefEditor: SharedPreferences.Editor = mPref.edit()
        prefEditor.putInt(key, value)
        prefEditor.apply()
    }

    fun getLong(key: String): Long = mPref.getLong(key, 0)

    fun put(key: String, value: Long) {
        val prefEditor: SharedPreferences.Editor = mPref.edit()
        prefEditor.putLong(key, value)
        prefEditor.apply()
    }

    fun remove(key: String) {
        val prefEditor: SharedPreferences.Editor = mPref.edit()
        prefEditor.remove(key)
        prefEditor.apply()
    }

    fun isExpireTime(key: String, limitSec: Long): Boolean {
        val now: Long = Calendar.getInstance().timeInMillis
        val start: Long = mPref.getLong(key, 0)
        val duration: Long = (now - start) / 1000
        return start == 0L || duration > limitSec
    }
}