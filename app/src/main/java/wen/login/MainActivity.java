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
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private Button btn_login;
    private TextView hint;
    private Handler handler;
    private String str_username = null;
    private String str_password = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 保持屏幕亮度
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        btn_login = findViewById(R.id.btn_login);
        hint = findViewById(R.id.hint);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                str_username = username.getText().toString();
                str_password = password.getText().toString();
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String result = sendPost(str_username, str_password);
                        Message message = handler.obtainMessage();
                        message.what = 12;
                        message.obj = result;
                        handler.sendMessage(message);
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
        });

        handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                super.handleMessage(message);
                if (message.what == 12) {
                    String res = message.obj.toString().trim();
                    System.out.println(res);
                    if (!res.isEmpty()) {
                        Intent intent = new Intent();
                        intent.putExtra("username", username.getText().toString().trim());
                        intent.setClass(MainActivity.this, Login.class);
                        startActivity(intent);
                    } else {
                        hint.setText("登录失败");
                        username.setText("");
                        password.setText("");
                    }
                }
            }
        };
    }

    public String sendPost(String username, String password) {

        String result = "";
        String URL = "http://10.151.24.168/login.php";
        HttpPost post = new HttpPost(URL);

        List<NameValuePair> params = new ArrayList<NameValuePair>();

        NameValuePair pair_username = new BasicNameValuePair("username", username);
        NameValuePair pair_password = new BasicNameValuePair("password", password);
        NameValuePair pair_login = new BasicNameValuePair("login", "OK");

        params.add(pair_username);
        params.add(pair_password);
        params.add(pair_login);

        try {
            HttpEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
            post.setEntity(entity);
            HttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(post);
            if (response.getStatusLine().getStatusCode() == 200) {
                result = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

}
