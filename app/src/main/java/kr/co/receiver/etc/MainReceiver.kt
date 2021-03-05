package kr.co.receiver.etc

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
import android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log
import androidx.core.app.NotificationCompat
import kr.co.receiver.BuildConfig
import kr.co.receiver.MainApplication
import kr.co.receiver.R
import kr.co.receiver.data.Api
import kr.co.receiver.entity.PostItem
import kr.co.receiver.entity.ResponseItem
import kr.co.receiver.etc.Const.IS_POST
import kr.co.receiver.etc.Const.RECEIVE_DATE
import kr.co.receiver.etc.Const.SENDER
import kr.co.receiver.ui.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MainReceiver : BroadcastReceiver() {
    @SuppressLint("SimpleDateFormat")
    private var format = SimpleDateFormat("yyyy-MM-dd HH:mm:Ss")

    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "onReceive()")
        val bundle = intent.extras
        val messages = parseSmsMessage(bundle)

        if (messages.isNotEmpty()) {
            val sender = messages[0]!!.originatingAddress
            val contents = messages[0]!!.messageBody
            val receivedDate = Date(messages[0]!!.timestampMillis)

            Log.i(TAG, "SMS sender : $sender")
            Log.i(TAG, "SMS contents : $contents")
            Log.i(TAG, "SMS received date : $receivedDate")

            if (contents.contains("입금")) {
                val first = contents.split("입금")
                val second = first[1].split("잔액")
                val amount = second[0].trim()
                Log.i(TAG, "result amount : $amount")

                sendPost(context, sender, amount, receivedDate)
            }
        }
    }

    // receive 된 메시지의 인자를 원하는 데이터로 분배하는 작업 (sender, contents, receivedDate)
    private fun parseSmsMessage(bundle: Bundle?): Array<SmsMessage?> {
        val objs = bundle!!["pdus"] as Array<*>?
        val messages = arrayOfNulls<SmsMessage>(
            objs!!.size
        )
        val smsCount = objs.size
        for (i in 0 until smsCount) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val format = bundle.getString("format")
                messages[i] = SmsMessage.createFromPdu(objs[i] as ByteArray, format)
            } else {
                messages[i] = SmsMessage.createFromPdu(objs[i] as ByteArray)
            }
        }
        return messages
    }

    // api call 하는 부분(/post)
    private fun sendPost(
        context: Context,
        sender: String?,
        amount: String,
        receivedDate: Date
    ) {
        val savedId = MainApplication.getInstance().appPref.getString(Const.LOGIN_ID) ?: ""
        val savedPw = MainApplication.getInstance().appPref.getString(Const.LOGIN_PW) ?: ""

        if (savedId.isNotEmpty() && savedPw.isNotEmpty()) {
            if (sender != null) {
                val requestBody =
                    PostItem(savedId, savedPw, sender, amount.replace(",", "").toInt())
                Log.i(TAG, "requestBody = $requestBody")

                Api.createRetrofit().postInfo(requestBody).enqueue(object : Callback<ResponseItem> {
                    override fun onResponse(
                        call: Call<ResponseItem>,
                        response: Response<ResponseItem>
                    ) {
                        Log.i(TAG, "onResponse response.isSuccessful = ${response.isSuccessful}")
                        if (response.isSuccessful) {
                            val body = response.body()
                            if (body != null) {
                                if (body.result) {
                                    sendNotification(context, sender, amount)
                                } else {
                                    sendToActivity(context, sender, amount, receivedDate)
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseItem>, t: Throwable) {
                        t.printStackTrace()
                    }
                })
            }
        }
    }

    private fun sendToActivity(
        context: Context,
        sender: String?,
        amount: String?,
        receiveDate: Date
    ) {
        context.startActivity(Intent(context, LoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(SENDER, sender)
            putExtra(Const.AMOUNT, amount)
            putExtra(RECEIVE_DATE, format.format(receiveDate))
        })
    }

    private val NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID"

    private fun sendNotification(
        context: Context,
        sender: String?,
        amount: String?
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        val notificationIntent = Intent(context, LoginActivity::class.java).apply {
            putExtra(SENDER, sender)
            putExtra(Const.AMOUNT, amount)
            putExtra(IS_POST, true)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.ic_launcher_foreground
                )
            )
            .setContentTitle("POST")
            .setContentText("성공")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setSmallIcon(R.drawable.ic_launcher_foreground) //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남
            val channelName: CharSequence = "노티페케이션 채널"
            val description = "오레오 이상을 위한 채널"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, importance)
            channel.description = description
            if (BuildConfig.DEBUG && notificationManager == null) {
                error("Assertion failed")
            }
            notificationManager?.createNotificationChannel(channel)
        } else builder.setSmallIcon(R.mipmap.ic_launcher) // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남

        notificationManager?.notify(1234, builder.build())
    }
}