package kr.co.receiver.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kr.co.receiver.MainApplication
import kr.co.receiver.R
import kr.co.receiver.data.Api
import kr.co.receiver.entity.LoginItem
import kr.co.receiver.entity.ResponseItem
import kr.co.receiver.etc.Const.AMOUNT
import kr.co.receiver.etc.Const.IS_POST
import kr.co.receiver.etc.Const.LOGIN_ID
import kr.co.receiver.etc.Const.LOGIN_PW
import kr.co.receiver.etc.Const.SENDER
import kr.co.receiver.etc.TAG
import kr.co.receiver.etc.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private val REQUEST_CODE = 1000

    private val etId by lazy { findViewById<EditText>(R.id.id) }
    private val etPw by lazy { findViewById<EditText>(R.id.pw) }
    private val btLogin by lazy { findViewById<Button>(R.id.login) }

    private var id: String? = null
    private var pw: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.i(TAG, "onCreate")

        checkPermissionSMS()
        checkSavingIdAndPw()
        getReceiverData()

        onClickEvent()
    }

    private fun checkPermissionSMS() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECEIVE_SMS), REQUEST_CODE)
    }

    private fun checkSavingIdAndPw() {
        id = MainApplication.getInstance().appPref.getString(LOGIN_ID) ?: ""
        pw = MainApplication.getInstance().appPref.getString(LOGIN_PW) ?: ""
        Log.i(TAG, "result : id = $id, pw = $pw")
    }

    private fun getReceiverData() {
        val sender = intent.getStringExtra(SENDER) ?: ""
        val amount = intent.getStringExtra(AMOUNT) ?: ""
        val isPost = intent.getBooleanExtra(IS_POST, false)
        Log.i(TAG, "result : sender = $sender, amount = $amount, isPost = $isPost")


        if(checkIdPw(id, pw)) {
            if(checkReceivedData(sender, amount, isPost)) {
                checkSenderAndContents(sender, amount, isPost)
            } else {
                goToMain()
            }
        }
    }

    private fun onClickEvent() {
        btLogin.setOnClickListener {
            if(etId.text.isNullOrEmpty() || etPw.text.isNullOrEmpty()) {
                toast("아이디 또는 비밀번호를 입력해 주세요.")
            } else {
                val id = etId.text.toString()
                val pw = etPw.text.toString()

                val requestBody = LoginItem(id, pw)
                Api.createRetrofit().postLogin(requestBody).enqueue(object : Callback<ResponseItem> {
                    override fun onResponse(
                        call: Call<ResponseItem>,
                        response: Response<ResponseItem>
                    ) {
                        if(response.isSuccessful) {
                            val body = response.body()
                            Log.i(TAG, "result : body = $body")
                            if(body != null) {
                                if(body.result) {
                                    MainApplication.getInstance().appPref.put(LOGIN_ID, id)
                                    MainApplication.getInstance().appPref.put(LOGIN_PW, pw)

                                    goToMain()
                                } else {
                                    toast("로그인에 실패하였습니다.")
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseItem>, t: Throwable) {
                        Log.e(TAG, ""+t.printStackTrace())
                    }
                })
            }
        }
    }

    private fun checkIdPw(savedId: String?, savedPw: String?) = !savedId.isNullOrEmpty() && !savedPw.isNullOrEmpty()
    private fun checkReceivedData(sender: String?, amount: String?, isPost: Boolean) = !sender.isNullOrEmpty() && !amount.isNullOrEmpty() && isPost

    private fun checkSenderAndContents(sender: String?, amount: String?, isPost: Boolean) {
        if(isPost) {
            goToMain(sender, amount)
        }
    }

    private fun goToMain(sender: String? = null, amount: String? = null) {
        startActivity(Intent(this@LoginActivity, MainActivity::class.java).apply {
            if(!sender.isNullOrEmpty()) putExtra(SENDER, sender)
            if(!amount.isNullOrEmpty()) putExtra(AMOUNT, amount)
        })
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                toast("권한 실패")
            }
        }
    }
}