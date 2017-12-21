package com.example.diyujia.selectroom;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.EditText;

import com.example.diyujia.bean.Pickers;
import com.example.diyujia.bean.UserInfo;
import com.example.diyujia.views.PickerScrollView;
import com.example.diyujia.views.PickerScrollView.onSelectListener;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;
import static com.example.diyujia.selectroom.MainActivity.DO_NOT_VERIFY;

/**
 * Created by diyujia on 2017/12/20.
 */

public class UserSelectRoom extends Activity implements View.OnClickListener{
    private Button bt_scrollNum; // 滚动选择器按钮-选择人数
    private PickerScrollView pickerscrlllview; // 滚动选择器-选择人数
    private List<Pickers> list; // 滚动选择器数据-选择人数
    private String[] id;//滚动选择器-选择人数
    private String[] name;//滚动选择器-选择人数
    private Button bt_yes; // 确定按钮-选择人数
    private RelativeLayout picker_rel; // 选择器布局-选择人数

    //滚动选择器-选择宿舍楼
    private Button bt_scrollNum_building;
    private PickerScrollView pickerscrollview_building;
    private List<Pickers> list_building;
    private String[] id_building;
    private String[] name_building;
    private Button bt_yes_building;
    private RelativeLayout picker_rel_building;

    //同住人信息：
    private TextView select_stu1_id;
    private EditText select_stu1_id_input;
    private TextView select_stu1_code;
    private EditText select_stu1_code_input;
    private TextView select_stu2_id;
    private EditText select_stu2_id_input;
    private TextView select_stu2_code;
    private EditText select_stu2_code_input;
    private TextView select_stu3_id;
    private EditText select_stu3_id_input;
    private TextView select_stu3_code;
    private EditText select_stu3_code_input;

    private TextView select_nameTv;
    private TextView select_idTv;
    private int SelectNum;
    private int SelectBuilding;
    private SharedPreferences sharedPreferences;
    private Button bt_commit;
    private Handler mHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectroom_select);

        sharedPreferences = getSharedPreferences("selectroom",MODE_PRIVATE);
        initView();
        initLinstener();
        initData();

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                switch (msg.what){
                    case 0:
                        Dialog alertDialog1 = new AlertDialog.Builder(UserSelectRoom.this).
                                setTitle("恭喜：").
                                setMessage("选宿舍成功").
                                setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //确定按钮，点击可取消提示框
                                        //这里点击确定以后回到个人信息页面，并且关闭其他所有activity
                                        Intent intent = new Intent();
                                        intent.setClass(UserSelectRoom.this, UserInformation.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    }
                                }).
                                create();
                        alertDialog1.show();
                        break;
                    default:
                        Dialog alertDialog = new AlertDialog.Builder(UserSelectRoom.this).
                                setTitle("提示").
                                setMessage("选宿舍失败，错误代码："+msg.what).
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

    @Override
    public void onClick(View view){
        if(view.getId() == R.id.select_selectNum){
            picker_rel.setVisibility(View.VISIBLE);
        }else if(view.getId() == R.id.picker_yes){//选择人数确定按钮，点击以后给出相应的填写框
            picker_rel.setVisibility(View.GONE);

            setClassmate();
        }else if(view.getId() == R.id.select_building_input){//选择宿舍楼按钮，点击以后空间出现。
            picker_rel_building.setVisibility(View.VISIBLE);
        }else if(view.getId() == R.id.picker_yes_building){ //选择宿舍楼滚轮中的确定按钮，点击以后控件消失
            picker_rel_building.setVisibility(View.GONE);
        }else if(view.getId() == R.id.select_commit){
            selectCommit();
        }
    }

    private void initView() {
        select_nameTv = (TextView)findViewById(R.id.select_name);
        select_idTv = (TextView)findViewById(R.id.select_id);

        //选择人数控件绑定view控件
        bt_scrollNum = (Button) findViewById(R.id.select_selectNum);
        picker_rel = (RelativeLayout) findViewById(R.id.picker_rel);
        pickerscrlllview = (PickerScrollView) findViewById(R.id.pickerscrlllview);
        bt_yes = (Button) findViewById(R.id.picker_yes);

        //选择宿舍号绑定view控件
        bt_scrollNum_building = (Button) findViewById(R.id.select_building_input);
        picker_rel_building = (RelativeLayout)findViewById(R.id.picker_rel_building);
        pickerscrollview_building = (PickerScrollView)findViewById(R.id.pickerscrlllview_building);
        bt_yes_building = (Button) findViewById(R.id.picker_yes_building);

        select_stu1_id = (TextView)findViewById(R.id.select_stu1_id);
        select_stu1_id_input = (EditText)findViewById(R.id.select_stu1_id_input);
        select_stu2_id = (TextView)findViewById(R.id.select_stu2_id);
        select_stu2_id_input = (EditText)findViewById(R.id.select_stu2_id_input);
        select_stu3_id = (TextView)findViewById(R.id.select_stu3_id);
        select_stu3_id_input = (EditText)findViewById(R.id.select_stu3_id_input);

        select_stu1_code = (TextView)findViewById(R.id.select_stu1_code);
        select_stu1_code_input = (EditText) findViewById(R.id.select_stu1_code_input);
        select_stu2_code = (TextView)findViewById(R.id.select_stu2_code);
        select_stu2_code_input = (EditText) findViewById(R.id.select_stu2_code_input);
        select_stu3_code = (TextView)findViewById(R.id.select_stu3_code);
        select_stu3_code_input = (EditText) findViewById(R.id.select_stu3_code_input);

        bt_commit = (Button)findViewById(R.id.select_commit);
    }
    private void initLinstener() {
        bt_scrollNum.setOnClickListener(this);
        pickerscrlllview.setOnSelectListener(pickerListener);
        bt_yes.setOnClickListener(this);

        bt_scrollNum_building.setOnClickListener(this);
        pickerscrollview_building.setOnSelectListener(pickerListener_building);
        bt_yes_building.setOnClickListener(this);
        bt_commit.setOnClickListener(this);
    }
    private void initData() {
        select_nameTv.setText("姓名："+sharedPreferences.getString("Name",null));
        select_idTv.setText("学号："+sharedPreferences.getString("userid",null));

        list = new ArrayList<Pickers>();
        id = new String[] { "1", "2", "3", "4"};
        name = new String[] { "一人办理", "双人办理", "三人办理", "四人办理"};
        for (int i = 0; i < name.length; i++) {
            list.add(new Pickers(name[i], id[i]));
        }
        // 设置数据，默认选择第一条
        pickerscrlllview.setData(list);
        pickerscrlllview.setSelected(0);
        bt_scrollNum.setText(name[0]);
        SelectNum = Integer.parseInt(id[0]);

        list_building = new ArrayList<Pickers>();
        id_building = new String[] { "5", "8", "9", "13","14"};
        name_building = new String[] { "5号楼", "8号楼", "9号楼", "13号楼","14号楼"};
        for (int i = 0; i < name_building.length; i++) {
            list_building.add(new Pickers(name_building[i], id_building[i]));
        }
        // 设置数据，默认选择第一条
        pickerscrollview_building.setData(list_building);
        pickerscrollview_building.setSelected(0);
        bt_scrollNum_building.setText(name_building[0]);
        SelectBuilding = Integer.parseInt(id_building[0]);
    }

    // 办理入住人数控件监听器
    onSelectListener pickerListener = new onSelectListener() {

        @Override
        public void onSelect(Pickers pickers) {
            Log.d("SelectRoom","您选择的办理入住人数："+pickers.getShowConetnt());
            SelectNum = Integer.parseInt(pickers.getShowId());
            bt_scrollNum.setText(pickers.getShowConetnt());
        }
    };

    // 宿舍楼选择控件监听器
    onSelectListener pickerListener_building = new onSelectListener() {

        @Override
        public void onSelect(Pickers pickers) {
            Log.d("SelectRoom","您选择的宿舍楼："+pickers.getShowConetnt());
            SelectBuilding = Integer.parseInt(pickers.getShowId());
            bt_scrollNum_building.setText(pickers.getShowConetnt());
        }
    };

    private void selectCommit(){
        new Thread(new Runnable() {
            @Override
            public void run(){
                String address_url = "https://api.mysspku.com/index.php/V1/MobileCourse/SelectRoom";
                try{
                    URL url = new URL(address_url);
                    MainActivity.trustAllHosts();
                    HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
                    con.setHostnameVerifier(DO_NOT_VERIFY);
                    con.setRequestMethod("POST");
                    con.setReadTimeout(8000);
                    con.setConnectTimeout(8000);

                    String num = Integer.toString(SelectNum);
                    String stuid = sharedPreferences.getString("userid",null);
                    String stu1id = select_stu1_id_input.getText().toString();
                    String v1code = select_stu1_code_input.getText().toString();
                    String stu2id = select_stu2_id_input.getText().toString();
                    String v2code = select_stu2_code_input.getText().toString();
                    String stu3id = select_stu3_id_input.getText().toString();
                    String v3code = select_stu3_code_input.getText().toString();
                    String buildingNo = Integer.toString(SelectBuilding);
                    String post_data = "num=" + num +
                            "&stuid=" + stuid +
                            "&stu1id=" + stu1id +
                            "&v1code=" + v1code +
                            "&stu2id=" + stu2id +
                            "&v2code=" + v2code +
                            "&stu3id=" + stu3id +
                            "&v3code=" + v3code +
                            "&buildingNo=" + buildingNo;
                    con.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                    con.setRequestProperty("Content-Length",post_data.length()+"");
                    //设置打开输出流
                    con.setDoOutput(true);
                    //拿到输出流
                    OutputStream os = con.getOutputStream();
                    //使用输出流向服务器提交数据
                    os.write(post_data.getBytes());
                    if(con.getResponseCode() == 200){
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
                        Message msg = new Message();
                        if(resultConInt == 0){
                            msg.what = 0;
                            mHandler.sendMessage(msg);
                            return;//如果返回的错误代码为0，说明登陆成功
                        }else{
                            msg.what = resultConInt;
                            mHandler.sendMessage(msg);
                            return;
                        }
                    }


                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        ).start();
    }

    //用来设置同住人1、2、3的可见与否
    private void setClassmate(){
        switch (SelectNum){
            case 1:
                select_stu1_id.setVisibility(View.GONE);
                select_stu1_id_input.setVisibility(View.GONE);
                select_stu1_id_input.setText("");
                select_stu2_id.setVisibility(View.GONE);
                select_stu2_id_input.setVisibility(View.GONE);
                select_stu2_id_input.setText("");
                select_stu3_id.setVisibility(View.GONE);
                select_stu3_id_input.setVisibility(View.GONE);
                select_stu3_id_input.setText("");

                select_stu1_code.setVisibility(View.GONE);
                select_stu1_code_input.setVisibility(View.GONE);
                select_stu1_code_input.setText("");
                select_stu2_code.setVisibility(View.GONE);
                select_stu2_code_input.setVisibility(View.GONE);
                select_stu2_code_input.setText("");
                select_stu3_code.setVisibility(View.GONE);
                select_stu3_code_input.setVisibility(View.GONE);
                select_stu3_code_input.setText("");
                break;
            case 2:
                select_stu1_id.setVisibility(View.VISIBLE);
                select_stu1_id_input.setVisibility(View.VISIBLE);
                //select_stu1_id_input.setText("");
                select_stu2_id.setVisibility(View.GONE);
                select_stu2_id_input.setVisibility(View.GONE);
                select_stu2_id_input.setText("");
                select_stu3_id.setVisibility(View.GONE);
                select_stu3_id_input.setVisibility(View.GONE);
                select_stu3_id_input.setText("");

                select_stu1_code.setVisibility(View.VISIBLE);
                select_stu1_code_input.setVisibility(View.VISIBLE);
                //select_stu1_code_input.setText("");
                select_stu2_code.setVisibility(View.GONE);
                select_stu2_code_input.setVisibility(View.GONE);
                select_stu2_code_input.setText("");
                select_stu3_code.setVisibility(View.GONE);
                select_stu3_code_input.setVisibility(View.GONE);
                select_stu3_code_input.setText("");
                break;
            case 3:
                select_stu1_id.setVisibility(View.VISIBLE);
                select_stu1_id_input.setVisibility(View.VISIBLE);
                //select_stu1_id_input.setText("");
                select_stu2_id.setVisibility(View.VISIBLE);
                select_stu2_id_input.setVisibility(View.VISIBLE);
                //select_stu2_id_input.setText("");
                select_stu3_id.setVisibility(View.GONE);
                select_stu3_id_input.setVisibility(View.GONE);
                select_stu3_id_input.setText("");

                select_stu1_code.setVisibility(View.VISIBLE);
                select_stu1_code_input.setVisibility(View.VISIBLE);
                //select_stu1_code_input.setText("");
                select_stu2_code.setVisibility(View.VISIBLE);
                select_stu2_code_input.setVisibility(View.VISIBLE);
                //select_stu2_code_input.setText("");
                select_stu3_code.setVisibility(View.GONE);
                select_stu3_code_input.setVisibility(View.GONE);
                select_stu3_code_input.setText("");
                break;
            case 4:
                select_stu1_id.setVisibility(View.VISIBLE);
                select_stu1_id_input.setVisibility(View.VISIBLE);
                //select_stu1_id_input.setText("");
                select_stu2_id.setVisibility(View.VISIBLE);
                select_stu2_id_input.setVisibility(View.VISIBLE);
                //select_stu2_id_input.setText("");
                select_stu3_id.setVisibility(View.VISIBLE);
                select_stu3_id_input.setVisibility(View.VISIBLE);
                //select_stu3_id_input.setText("");

                select_stu1_code.setVisibility(View.VISIBLE);
                select_stu1_code_input.setVisibility(View.VISIBLE);
                //select_stu1_code_input.setText("");
                select_stu2_code.setVisibility(View.VISIBLE);
                select_stu2_code_input.setVisibility(View.VISIBLE);
                //select_stu2_code_input.setText("");
                select_stu3_code.setVisibility(View.VISIBLE);
                select_stu3_code_input.setVisibility(View.VISIBLE);
                //select_stu3_code_input.setText("");
                break;
        }
    }
}
