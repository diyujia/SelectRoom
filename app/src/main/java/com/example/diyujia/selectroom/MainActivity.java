package com.example.diyujia.selectroom;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.Dialog;
import android.app.AlertDialog;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import com.example.diyujia.util.NetUtil;
import com.example.diyujia.bean.UserInfo;
import com.example.diyujia.selectroom.UserInformation;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


/**
 * Created by diyujia on 2017/12/15.
 */

public class MainActivity extends Activity implements View.OnClickListener{
    private Button bLogin;//登录按钮
    private EditText eUsername;//用户
    private EditText ePassword;//密码

    private Handler mHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectroom_info);

        bLogin = (Button)findViewById(R.id.login_button);
        bLogin.setOnClickListener(this);

        //用于测试网络连接是否正常
        if(NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE){  //如果当前状态不是NONE则显示网络OK
            Log.d("SelectRoom","网络OK");
            Toast.makeText(MainActivity.this,"网络OK！",Toast.LENGTH_LONG).show();
        }else{
            Log.d("SelectRoom","网络挂了");
            Toast.makeText(MainActivity.this,"网络挂了！",Toast.LENGTH_LONG).show();
        }

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                switch (msg.what){
                    case 0:
                        //保存userid，每次登陆成功以后，都将userid改为编辑模式，然后修改user的值
                        SharedPreferences sharedPreferences = getSharedPreferences("selectroom",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("userid",eUsername.getText().toString());
                        editor.commit();
                        Toast.makeText(MainActivity.this,"登陆成功",Toast.LENGTH_LONG).show();
                        Intent i = new Intent(MainActivity.this,UserInformation.class);
                        startActivity(i);
                        break;
                    default:
                        Dialog alertDialog = new AlertDialog.Builder(MainActivity.this).
                                setTitle("提示").
                                setMessage("登录失败").
                                setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //确定按钮，点击可取消提示框
                                    }
                                }).
                                create();
                        alertDialog.show();
                        break;
                }
            }
        };
    }

    //主页面的登录按钮监听事件
    @Override
    public void onClick(View view){
        //登录按钮，如果登录成功，则进入个人信息界面，否则弹出提示框
        if(view.getId() == R.id.login_button){
            if(NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE){
                eUsername = (EditText)findViewById(R.id.login_username);
                ePassword = (EditText)findViewById(R.id.login_password);
                String Username = eUsername.getText().toString();
                String Password = ePassword.getText().toString();
                //调用UserLogin函数，请求登录的接口。
                UserLogin(Username,Password);

            }else{//如果网络连接不正常则显示“网络挂了”
                Toast.makeText(MainActivity.this,"网络挂了！",Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     *
     * @param Username
     * @param Password
     * @return
     */
    private void UserLogin(String Username,String Password){
        final String address = "https://api.mysspku.com/index.php/V1/MobileCourse/Login?username="
                +Username+"&password="+Password;
        Log.d("SelectRoom",address);
        final String userid = Username;
        //String resultCon = null;
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpsURLConnection con = null;
                try{
                    URL url = new URL(address);
                    //trustAllHosts函数用来给权限相信所有的网站证书
                    trustAllHosts();
                    con = (HttpsURLConnection)url.openConnection();
                    con.setHostnameVerifier(DO_NOT_VERIFY);
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String str;
                    while ((str = reader.readLine()) != null) {
                        response.append(str);
                        Log.d("SelectRoom",str);
                    }
                    String responseStr = response.toString();
                    Log.d("SelectRoom",responseStr);
                    //获取json中的错误信息，以及数据信息
                    int resultConInt = new JSONObject(responseStr).getInt("errcode");
                    String resultCon = Integer.toString(resultConInt);
                    Log.d("SelectRoom",resultCon);
                    JSONObject jsonObject1 = new JSONObject(responseStr).getJSONObject("data");
                    Log.d("SelectRoom",jsonObject1.getString("errmsg"));
                    Message msg = new Message();
                    if(resultConInt == 0){
                        msg.what = 0;
                        mHandler.sendMessage(msg);
                        return;//如果返回的错误代码为0，说明登陆成功
                    }else{
                        msg.what = 1;
                        mHandler.sendMessage(msg);
                        return;
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if(con != null){
                        con.disconnect();
                    }
                }
            }
        }).start();
    }

    /**
     * 登录界面接收到数据以后的解析函数
     * @param xmldata
     */
    private void parseXML(String xmldata){

    }

    public static void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        // Android use X509 cert
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
            }

            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }
        } };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection
                    .setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
}
