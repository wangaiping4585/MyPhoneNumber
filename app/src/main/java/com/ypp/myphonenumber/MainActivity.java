package com.ypp.myphonenumber;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ypp.myphonenumber.adapter.MyAdapter;
import com.ypp.myphonenumber.entity.PhoneInfo;
import com.ypp.myphonenumber.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;


public class MainActivity extends Activity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, View.OnClickListener, TextWatcher, ListviewActivity.OnRefreshListener {


    public static  final String uri="content://icc/adn";  //本地联系人
    public static final String  suri="content://sim/adn"; //SIM卡联系人
    public static  String KEYWORD =" ";    //搜索关键字
    public static  int INTENTID=1;   //跳转标志
    public static  String POPNAME;   //记录姓名
    public static  String POPNUMBER; //记录号码
    public static  int POPID;        //记录行ID
    public ListviewActivity lvActivity;  //自定义listview
    public static final int SHARE_SUCCESS = 30; //分享标记
    public static final int SHARE_FAIL = 31;


    TextView font,name,number,poptitle,popbianji,popdelete,popsendmessage,popmail,popcancle,sendnumber;
    MyAdapter adapter;
    ImageView iv_search,iv_add,iv_search_clear,iv_menu;
    List<PhoneInfo>  list=new ArrayList<PhoneInfo>(); //全部数据
    List<PhoneInfo>  slist =new ArrayList<PhoneInfo>();                    //搜索后结果集
    EditText et_list;
    PopupWindow popup,popuplist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.init(this);
//        ShareSDK.initSDK(this);   //初始化分享SDK
        setContentView(R.layout.activity_main);
        initView();
        initListener();
        initData();
    }

    /**
     * todo 初始化组件
     */
    private void initView() {

        lvActivity= (ListviewActivity) findViewById(R.id.lv_main);
        font= (TextView) findViewById(R.id.font);
        name= (TextView) findViewById(R.id.name);
        number= (TextView) findViewById(R.id.number);
        iv_search= (ImageView) findViewById(R.id.iv_search);
        iv_add= (ImageView) findViewById(R.id.iv_add);
        iv_menu= (ImageView) findViewById(R.id.iv_menu);

    }

    /**
     * todo 初始化监听器
     */
    private void initListener() {
        //适配器
        adapter=new MyAdapter(this);
        lvActivity.setAdapter(adapter);
        //行点击事件
        lvActivity.setOnItemClickListener(this);
        //行长按事件
        lvActivity.setOnItemLongClickListener(this);
        //搜索
        iv_search.setOnClickListener(this);
        //add联系人按钮
        iv_add.setOnClickListener(this);
        //图片菜单按钮刷新
        iv_menu.setOnClickListener(this);
        //listview下拉刷新
        lvActivity.setonRefreshListener(this);

    }

    /**
     * todo 初始化数据
     */
    public  void initData() {
        updateData();  //加载数据
    }

    Handler handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {

            if(msg.what==0){
                if(et_list!=null) {
                    et_list.setText(" ");
                }
                adapter.setList(list);
                adapter.notifyDataSetChanged();
                Collections.sort(list, nameComparator);

            }

            if(msg.what==SHARE_SUCCESS){
                Utils.print("分享成功!");
            }

            if(msg.what==SHARE_FAIL){
                Utils.print("分享失败!");
            }
        }
    };


    private void updateData(){
        new Thread(){
            @Override
            public void run() {

                list =Utils.getNumber();
                handler.sendEmptyMessage(0);

            }
        }.start();
    }


    /**
     * 排序和搜索后的数据更新
     */
    private void updateData_SearchOrSoft(List<PhoneInfo> plist){
        adapter.setList(plist);            //更新数据
        Collections.sort(plist, nameComparator);  //按名称排序
        adapter.notifyDataSetChanged();
    }



    /**
     *todo 名称比较器
     */
    Comparator<PhoneInfo> nameComparator = new Comparator<PhoneInfo>() {
        @Override
        public int compare(PhoneInfo lhs,PhoneInfo rhs) {
            if (lhs.getPhoneName() != null && rhs.getPhoneName() != null) {
                return ((lhs.getPhoneName().toLowerCase()).compareTo((rhs.getPhoneName()).toLowerCase()));
            }
            return  99;
        }
    };



    /**
     * todo 行点击事件
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //todo 打电话
       lvActivity.findFocus(); //获取焦点
       PhoneInfo pInfo = (PhoneInfo) parent.getItemAtPosition(position);
       Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + pInfo.getPhoneNumber()));  //意图    Intent(行为, 行为数据)
       startActivity(intent);

    }




    /**
     * todo 行长按事件
     * @param parent
     * @param view
     * @param position
     * @param id
     * @return
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        PhoneInfo phoneInfo = (PhoneInfo) parent.getItemAtPosition(position);
        POPNAME=phoneInfo.getPhoneName();
        POPNUMBER=phoneInfo.getPhoneNumber();
        POPID=position;
        showPopChecked(view);
        return true;
    }

//    ProgressDialog pd;
//    // 显示一个环形进度框
//    public void showProgressDialog() {
//        pd = new ProgressDialog(MainActivity.this);
//        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        pd.setTitle("正在加载联系人......");
//        pd.setMessage("请耐心等待");
//        pd.show();
//    }

    /**
     * 分享
     * @param text  文本
     * @param photopath  图片绝对路径
     * @param sharename   分享方式名称
     */
    public void share(String text, String photopath, String sharename) {

        Platform.ShareParams sp = new SinaWeibo.ShareParams();
        sp.text = text;

        if (photopath != null) {
            // sp.imagePath = "/mnt/sdcard/测试分享的图片.jpg";
            sp.imagePath = photopath;

        }

        Platform weibo = ShareSDK.getPlatform(this, sharename);

        // 设置分享事件回调
        weibo.setPlatformActionListener(new PlatformActionListener() {

            public void onError(Platform platform, int action, Throwable t) {
                // 操作失败的处理代码
                Message m = handler.obtainMessage();
                m.what = SHARE_FAIL;
                MainActivity.this.handler.sendMessage(m);
            }

            public void onComplete(Platform platform, int action,
                                   HashMap<String, Object> res) {
                // 操作成功的处理代码
                Message m = handler.obtainMessage();
                m.what = SHARE_SUCCESS;
                MainActivity.this.handler.sendMessage(m);
            }

            public void onCancel(Platform platform, int action) {
                // 操作取消的处理代码
            }

        });

        // 执行图文分享
        weibo.share(sp);
    }

    /**
     * list操作弹窗
     */
    public void showPopChecked(View v){
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        View myviews = inflater.inflate(R.layout.popupwindow_list, null);
        //设置弹窗长宽
        popuplist = new PopupWindow(myviews, 0, 0);
        popuplist.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
        popuplist.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        //获取焦点
        popuplist.setFocusable(true);
        //组件赋值
        poptitle= (TextView) myviews.findViewById(R.id.poptitle);
        poptitle.setText(POPNAME);
        popbianji= (TextView) myviews.findViewById(R.id.bianji);
        popdelete= (TextView) myviews.findViewById(R.id.delete);
        popsendmessage= (TextView) myviews.findViewById(R.id.sendmessage);
        popmail= (TextView) myviews.findViewById(R.id.mail);
        popcancle= (TextView) myviews.findViewById(R.id.popcanle);
        sendnumber= (TextView) findViewById(R.id.sendnumber);
        //点击事件
        popbianji.setOnClickListener(this);
        popdelete.setOnClickListener(this);
        popsendmessage.setOnClickListener(this);
        popmail.setOnClickListener(this);
        popcancle.setOnClickListener(this);
        //弹窗
//        popuplist.showAsDropDown(v);
        popuplist.showAtLocation(v, Gravity.VERTICAL_GRAVITY_MASK, 0,0);
    }


    /**
     * 搜索弹窗
     * @param v
     */
    public  void showPopupWindowSearch(View v){

        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        View myview = inflater.inflate(R.layout.popupwindow_search, null);

        popup = new PopupWindow(myview, 1080, 450); //视图对象,长,宽
        popup.setFocusable(true);   //需要在弹出窗口前获取焦点,不然edittext无法输入
        popup.setBackgroundDrawable(new BitmapDrawable());  //防止其他组件无法点击
        popup.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE); //防止软件盘被遮挡
        myview.findViewById(R.id.iv_serarchlist).setOnClickListener(this);
        myview.findViewById(R.id.iv_searchs_clear).setOnClickListener(this);

        et_list= (EditText) myview.findViewById(R.id.et_search);
        iv_search_clear= (ImageView) myview.findViewById(R.id.iv_searchs_clear);
        //搜索框动态查询
        et_list.addTextChangedListener(this);
        //自定义位置弹出
        popup.showAtLocation(v, Gravity.CENTER, 0, -680);//视图,锚点(center为容器中心),偏移量(x,y /px)
    }


    /**
     * popupWindow中输入框抢夺焦点,使用延时来解决软键盘闪现
     */
    private void popupInputMethodWindow() {

        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                InputMethodManager   imm = (InputMethodManager) et_list.getContext().getSystemService(Service.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

            }
        }, 500);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_search:
                //搜索
                showPopupWindowSearch(v);
                popupInputMethodWindow();
                break;
            case R.id.iv_serarchlist:
                //搜索按钮
                onTextChanged(KEYWORD, 0, 0, 0);
                break;
            case R.id.iv_menu:
                if(et_list!=null) {
                    et_list.setText(" ");
                }
                updateData();
                break;
            case R.id.iv_searchs_clear:
                //搜索清除
                et_list.setText(" ");
                popup.dismiss();
                updateData();
                break;

            case R.id.iv_add:
                INTENTID=1;
                Intent intent =new Intent(this,AddActivity.class);
                this.startActivity(intent);
                overridePendingTransition(R.anim.translate_mainin,R.anim.translate_mainout);
                break;

            case R.id.bianji:
                //编辑
                INTENTID=0;
                Intent intentbain =new Intent(this,AddActivity.class);
                this.startActivity(intentbain);
                overridePendingTransition(R.anim.translate_mainin,R.anim.translate_mainout);
                break;

            case R.id.delete:
                //删除
                Utils.delete(POPNAME);
                popuplist.dismiss();
                list.remove(POPID);
                updateData();
                break;

            case R.id.sendmessage:
                //发短信
                Intent intentSms = new Intent(Intent.ACTION_VIEW, Uri.parse("smsto:"+POPNUMBER));
                startActivity(intentSms);
                break;
            case R.id.mail:
                //分享
                Intent intentmail = new Intent(Intent.ACTION_SEND);
                startActivity(Intent.createChooser(intentmail, POPNUMBER));
//                share(POPNAME, null, SinaWeibo.NAME);
                break;
            case R.id.popcanle:
                //取消
                popuplist.dismiss();
                break;
            default:
                break;
        }

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //改变前
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
         //改变时
        s=et_list.getText();
        KEYWORD=s.toString();
        slist = Utils.getSearchResult(list, KEYWORD);   //搜索后的list集合
        updateData_SearchOrSoft(slist);

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    /**
     * 下拉刷新
     */
    @Override
    public void onRefresh() {
        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                updateData();   //更新数据
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                adapter.notifyDataSetChanged();
                lvActivity.onRefreshComplete();
            }
        }.execute(null,null,null);
    }



}
