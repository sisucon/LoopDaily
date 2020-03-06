package com.sisucon.loopdaily.Util

import com.google.gson.Gson
import okhttp3.*
import java.util.concurrent.TimeUnit

class NetUtil {
    companion object{
        val TYPE = "application/octet-stream"
        val NotConnectReply = ReplyMessage(false,"连接不上服务器")
        var  cookieStore : HashMap<String,MutableList<Cookie>> = HashMap<String,MutableList<Cookie>>()
        fun PostUserMessage (url:String,username:String,password:String): ReplyMessage{
            try {
                val okHttp = CreateOkHttpClient(url)
                okHttp.newBuilder().build()
                val formBody:  FormBody = FormBody.Builder().add("username", username).add("password", password).build()
                val request : Request = Request.Builder().url(url).post(formBody).build()
                val response : Response = okHttp.newCall(request).execute()
                val result = response.body()?.string()
                println("result = $result")
                return if(Gson().fromJson(result,ReplyMessage::class.java)==null) NotConnectReply else Gson().fromJson(result,ReplyMessage::class.java)
            } catch (e:Exception) {
                e.printStackTrace()
                return NotConnectReply
            }
        }



        fun PostMessage (url:String,json:String): ReplyMessage{
            val okHttp = CreateOkHttpClient(url)
            val requestBody:RequestBody = FormBody.create(MediaType.parse("application/json"),json)
            val request : Request = Request.Builder().url(url).post(requestBody).build()
            val response : Response = okHttp.newCall(request).execute()
            return Gson().fromJson(response.body()?.string(),ReplyMessage::class.java)
        }

        fun GetMessage(url:String):String?{
            val okHttpClient = CreateOkHttpClient(url)
            val request = Request.Builder().url(url).get().build()
            val response = okHttpClient.newCall(request).execute()
            return response.body()?.string()
        }

        fun CreateOkHttpClient(url:String):OkHttpClient{
            return OkHttpClient.Builder().connectTimeout(10,TimeUnit.SECONDS).writeTimeout(10,TimeUnit.SECONDS).readTimeout(20,TimeUnit.SECONDS)
                .cookieJar(object : CookieJar{
                    override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>) {
                        cookieStore[url.host()] = cookies
                    }

                    override fun loadForRequest(url: HttpUrl): MutableList<Cookie>? {
                        return if (cookieStore[url.host()]!=null) cookieStore[url.host()] else  ArrayList<Cookie>()
                    }
                }).build()
        }

        fun ConvertToReplyMessage(s:String):ReplyMessage{
            return Gson().fromJson(s,ReplyMessage::class.java)
        }
    }
}




data class UserModel(val username:String,val password: String)
data class ReplyMessage(val result:Boolean,val message:String)
data class ServerUserModel(val userName: String,val mobileNum:String,val friendList: MutableList<Long> , val avatorFileName:String)