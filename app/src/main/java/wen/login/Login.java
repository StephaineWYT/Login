package wen.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Login extends AppCompatActivity {

    private TextView tv_username;
    private TextView tv_password;
    private Button btn_query;

    private String result;
    private String username = "";
    private String password = "";

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 200) {
                tv_username.setText(username);
                tv_password.setText(password);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 保持屏幕亮度
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.login);

        tv_username = findViewById(R.id.tv_username);
        tv_password = findViewById(R.id.tv_password);
        btn_query = findViewById(R.id.btn_query);

        //获取值
        Intent intent = getIntent();
        final String intent_username = intent.getStringExtra("username");

        btn_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HttpClient client = new DefaultHttpClient();
                        HttpPost post = new HttpPost("http://10.151.24.168/query.php");
                        try {

                            List<NameValuePair> params = new ArrayList<NameValuePair>();
                            NameValuePair p1 = new BasicNameValuePair("username", intent_username);
                            params.add(p1);

                            post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

                            HttpResponse response = client.execute(post);
                            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                                result = EntityUtils.toString(response.getEntity());
                                JSONObject jsonObject = new JSONObject(result);
                                username = "用户名： " + jsonObject.getString("username");
                                password = "密码： " + jsonObject.getString("password");
                                System.out.println(username);
                                System.out.println(password);
                            } else {
                                result = "请求失败";
                            }
                            Message message = handler.obtainMessage();
                            message.what = 200;
                            message.obj = result;
                            handler.sendMessage(message);
                        } catch (IOException | JSONException e) {
                            System.out.println("出错了");
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
        });
    }

}
