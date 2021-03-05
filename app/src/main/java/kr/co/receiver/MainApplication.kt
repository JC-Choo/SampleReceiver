package kr.co.receiver

import android.app.Application
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import kr.co.receiver.etc.AppPrefUtil
import kr.co.receiver.etc.MainReceiver
import kr.co.receiver.etc.TAG

class MainApplication : Application() , LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onAppCreated() {
        Log.i(TAG, "check app ON_CREATE!!!!!!!!!!!!!!!!!")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        Log.i(TAG, "check app ON_START!!!!!!!!!!!!!!!!!")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onAppResumed() {
        Log.i(TAG, "check app ON_RESUME!!!!!!!!!!!!!!!!!")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onAppPaused() {
        Log.i(TAG, "check app ON_PAUSE!!!!!!!!!!!!!!!!!")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        Log.i(TAG, "check app ON_STOP!!!!!!!!!!!!!!!!!")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onAppDestroyed() {
        Log.i(TAG, "check app ON_DESTROY!!!!!!!!!!!!!!!!!")
        unregisterReceiver()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    fun onAppAny() {
        Log.i(TAG, "check app ON_ANY!!!!!!!!!!!!!!!!!")
    }

    companion object {
        private var instance: MainApplication? = null
        fun getInstance(): MainApplication {
            if(instance == null)
                instance = MainApplication()

            return instance!!
        }

        const val BROADCAST_MESSAGE = "android.provider.Telephony.SMS_RECEIVED"
    }

    val appPref: AppPrefUtil by lazy { AppPrefUtil(this) }
    private val receiver = MainReceiver()
    
    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate")
        instance = this
        registerReceiver()

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    private fun registerReceiver() {
        val theFilter = IntentFilter(ConnectivityManager.EXTRA_NO_CONNECTIVITY).apply {
            addAction(BROADCAST_MESSAGE)
        }
        registerReceiver(receiver, theFilter)
    }

    private fun unregisterReceiver() {
        unregisterReceiver(receiver)
    }
}