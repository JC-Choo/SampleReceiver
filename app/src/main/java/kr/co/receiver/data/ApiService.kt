package kr.co.receiver.data

import kr.co.receiver.entity.LoginItem
import kr.co.receiver.entity.PostItem
import kr.co.receiver.entity.ResponseItem
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/login")
    fun postLogin(@Body body: LoginItem): Call<ResponseItem>

    @POST("/post")
    fun postInfo(@Body body: PostItem): Call<ResponseItem>
}