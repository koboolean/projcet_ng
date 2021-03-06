package koboolean.co.kr.project

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_get_gps__for_login.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class GetGPS_ForLogin : AppCompatActivity() {
    var web_id : String? = null
    var web_pw : String? = null
    var activity : Intent? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_gps__for_login)
        activity = Intent(this, GetGPS_getPost::class.java)
        login_button.setOnClickListener { view ->
            web_id = check_id.text.toString()
            web_pw = check_pw.text.toString()
            var thread = NetworkThread()
            thread.start()
        }

    }

    inner class NetworkThread : Thread(){
        override fun run() {
            var client = OkHttpClient()
            // 요청할 페이지 주소
            var builder = Request.Builder()

            var url = builder.url("http://203.244.145.214:8084/kobooleanWeb/selectUsers.jsp")

            var bodyBuilder = FormBody.Builder()
            bodyBuilder.add("web_id", web_id)
            bodyBuilder.add("web_pw", web_pw)
            var body = bodyBuilder.build()
            var post = url.post(body)

            var request = post.build()

            client.newCall(request).enqueue(CallBack1())
        }
    }
    inner class CallBack1 : Callback{
        // 서버와의 통신이 실패되었을 때
        override fun onFailure(call: Call, e: IOException) {
            textView3.text = "현재 통신이 원활하지 않습니다."
        }
        // 서버와의 통신이 잘 마무리 되었을 때
        override fun onResponse(call: Call, response: Response) {
            var result = response?.body()?.string()

            runOnUiThread{
                var obj = JSONObject(result)
                var name = obj.getString("user_name")
                if(!name.equals("?")){
                    activity?.putExtra("user_name", name)
                    activity?.putExtra("user_id", web_id)
                    startActivity(activity)
                }else{
                    textView3.text = "아이디 혹은 비밀번호를 확인해주세요."
                }

                if(name.equals("?")){
                    textView3.text = "아이디 및 비밀번호를 다시 확인해주세요"
                }else{
                    activity?.putExtra("name", name)
                    activity?.putExtra("web_id", web_id)
                    startActivity(activity)
                }
            }
        }
    }
}

