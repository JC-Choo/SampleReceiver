package kr.co.receiver.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import kr.co.receiver.MainApplication
import kr.co.receiver.R
import kr.co.receiver.etc.Const
import kr.co.receiver.etc.Const.AMOUNT
import kr.co.receiver.etc.Const.SENDER
import kr.co.receiver.etc.MainReceiver
import kr.co.receiver.etc.TAG

class MainActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.i(TAG, "onCreate")

        val tvName = findViewById<TextView>(R.id.name)
        val tvDepositDetails = findViewById<TextView>(R.id.depositDetails)
        val btLogout = findViewById<Button>(R.id.logOut)

        val name = intent.getStringExtra(SENDER) ?: ""
        val amount = intent.getStringExtra(AMOUNT) ?: ""

        tvName.text = "이름 : $name"
        tvDepositDetails.text = "금액 : $amount"

        btLogout.setOnClickListener {
            MainApplication.getInstance().appPref.remove(Const.LOGIN_ID)
            MainApplication.getInstance().appPref.remove(Const.LOGIN_PW)
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            })
        }
    }
}