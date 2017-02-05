package com.ypp.myphonenumber;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ypp.myphonenumber.util.Utils;

/**
 * Created by adminstrator on 2016/12/3.
 */
public class AddActivity extends Activity implements View.OnClickListener {

    TextView tv_finsh,add_name,add_number,add_gongsi,add_dizhi,add_beizhu,font,msg;
    ImageView tv_clean;
    Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_activity);
        initView();
        setText();
        initListener();
    }



    private void initView() {
        tv_clean= (ImageView) findViewById(R.id.add_clean);
        tv_finsh= (TextView) findViewById(R.id.add_finsh);
        add_name= (TextView) findViewById(R.id.add_name);
        add_number= (TextView) findViewById(R.id.add_number);
        add_gongsi= (TextView) findViewById(R.id.add_gongsi);
        add_dizhi= (TextView) findViewById(R.id.add_dizhi);
        add_beizhu= (TextView) findViewById(R.id.add_beizhu);
        font= (TextView) findViewById(R.id.font);

    }

    private void initListener() {
        tv_clean.setOnClickListener(this);
        tv_finsh.setOnClickListener(this);
    }




    /**
     *  todo 清空输入框数据
     */
    public void clearText(){
        add_name.setText(" ");
        add_number.setText(" ");
        add_gongsi.setText(" ");
        add_dizhi.setText(" ");
        add_beizhu.setText(" ");
    }


    public void setText(){

        if (MainActivity.INTENTID==0){
            font.setText("编辑");
            add_name.setText(MainActivity.POPNAME);
            add_number.setText(MainActivity.POPNUMBER);
            add_gongsi.setText("北京俊辉煌科技有限公司");
            add_dizhi.setText("北京朝阳");
            add_beizhu.setText("XXXXXXXX");
        }else if(MainActivity.INTENTID==1){
            font.setText("新建联系人");
        }
    }


    /**
     * 页面跳转
     */
    public void IntentPage(){
        Intent intent =new Intent(this,MainActivity.class);
        this.startActivity(intent);
        overridePendingTransition(R.anim.translate_addin,R.anim.translate_addout);
        this.finish();
    }


//    public void showDiglog(String msgs) {
//        LayoutInflater inflater = LayoutInflater.from(this);
//        View myview = inflater.inflate(R.layout.layout_myself_dialog, null);
//        AlertDialog.Builder builder = new AlertDialog.Builder(AddActivity.this);
//        builder.setView(myview);
//        msg= (TextView) myview.findViewById(R.id.msg);
//        myview.findViewById(R.id.bt_submit).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//        msg.setText(msgs);
//        dialog = builder.create();
//        dialog.show();
//
//    }
    /**
     *进行数据判断添加/编辑
     */
    public void judgeText(){

        if (MainActivity.INTENTID == 1) {
            if (!add_name.getText().equals(" ") && add_name != null){
                Utils.add(add_name.getText().toString(), add_number.getText().toString());
            }
            clearText();//清空输入框数据
            IntentPage();//返回主页面

        } else if (MainActivity.INTENTID == 0) {
            Utils.update(add_name.getText().toString(), add_number.getText().toString());
            clearText();//清空输入框数据
            IntentPage();//返回主页面
        }

    }


    @Override
    public void onClick(View v) {
          switch (v.getId()){
              case R.id.add_clean:
                  //退出
                  IntentPage();
                  break;
              case R.id.add_finsh:
                  judgeText();  //添加/编辑 数据
                  break;

              default:
                  break;
          }
    }
}
