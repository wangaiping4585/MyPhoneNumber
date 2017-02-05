package com.ypp.myphonenumber.util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts.Data;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.Toast;

import com.ypp.myphonenumber.entity.PhoneInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by adminstrator on 2016/12/2.
 */
public class Utils {

    public static Context context;
    public static void  init(Context mcontext){
        context=mcontext;
    }

    public static void print(String msg){
        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
        Log.i("ypp",""+msg);
    }
    /**
     * 获取手机的联系人
     * @return
     */
    public static List<PhoneInfo> getNumber(){
        Cursor cursor =context.getContentResolver()
         .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);  //获取联系人
        List<PhoneInfo>  list =new ArrayList<PhoneInfo>();
        if(cursor!=null) {
            while (cursor.moveToNext()) {//遍历去的联系人
                PhoneInfo pInfo = new PhoneInfo();
                pInfo.setPhoneName(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                pInfo.setPhoneNumber(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                list.add(pInfo);
            }
        }
        cursor.close();
        return  list;
    }



    /**
     * 搜索条件查询
     * @param list
     * @param keyword
     * @return
     */
    public static List<PhoneInfo> getSearchResult(List<PhoneInfo> list, String keyword){

        // 返回结果集合
        List<PhoneInfo> searchResultList = new ArrayList<PhoneInfo>();
        for (int i = 0; i < list.size(); i++) {
            PhoneInfo phoneInfo = list.get(i);
            if(phoneInfo.getPhoneName()!=null) {
                if (phoneInfo.getPhoneName().toLowerCase().contains(keyword.toLowerCase()) ||
                        phoneInfo.getPhoneNumber().contains(keyword)) {
                    searchResultList.add(phoneInfo);
                }
            }
        }
        return searchResultList;
    }



    /**todo 文本高亮显示*/
    public static SpannableStringBuilder highLightText(String str, String query){
        //使用indexOf()查找子字符串起始字母在母字符串中的索引
        int start=0;
        if(str!=null) {
            start = (str.toLowerCase()).indexOf(query.toLowerCase());  //不区分大小写
        }
        /**
         * 说明: inddexOf()区分大小写,在找不到关键字时返回下标值为-1,而返回的数组从0开始,从而容易出现数组溢出异常
         * */

        SpannableStringBuilder sb=null;
        if(start>=0) {  //防止返回-1的情况出现异常
            int end = start + query.length();
            if(str!=null) {
                sb = new SpannableStringBuilder(str);
                sb.setSpan(
                        new ForegroundColorSpan(Color.GREEN),   //前景颜色
                        start,   //起始坐标
                        end,     //终止坐标
                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE   //旗标
                );
            }
        }

        return  sb;

    }

    public static  String getTime(){
        SimpleDateFormat formatter = new  SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        return  str;
    }


    /**
     *判断输入的是否为数字
     * @param str
     * @return
     */
    public static boolean isNumber(String str){
        for (int i = str.length();--i>=0;){
            if (!Character.isDigit(str.charAt(i))){
                return false;  //为中文
            }
        }
        return true; //为数字
    }

    /**
     * 新增联系人
     * @param name
     * @param phoneNumber
     */
    public static  void add(String name, String phoneNumber) {
        // 根据号码找数据，如果存在则不添加，因为有号码但无名字是不允许的
        // if (!findNameByPhoneNumber(phoneNumber)) {
        // 插入raw_contacts表，并获取_id属性
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        long contact_id = ContentUris.parseId(resolver.insert(uri, values));
        // 插入data表
        uri = Uri.parse("content://com.android.contacts/data");
        // 添加姓名
        values.put("raw_contact_id", contact_id);
        values.put(Data.MIMETYPE, "vnd.android.cursor.item/name");
        values.put("data2", name);
        resolver.insert(uri, values);
        values.clear();
        // 添加手机号码
        values.put("raw_contact_id", contact_id);
        values.put(Data.MIMETYPE, "vnd.android.cursor.item/phone_v2");
        values.put("data1", phoneNumber);
        resolver.insert(uri, values);
        values.clear();

    }

    /**
     * 更新联系人
     * @param name
     * @param phoneNumber
     * @return
     */
    public static  boolean update(String name, String phoneNumber) {
        try {
            // 根据姓名求id,再根据id跟新
            Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
            ContentResolver resolver = context.getContentResolver();
            Cursor cursor = resolver.query(uri, new String[] { Data._ID }, "display_name = ?", new String[] { name }, null);
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(0);
                Uri mUri = Uri.parse("content://com.android.contacts/data");// 对data表的所有数据操作
                ContentResolver mResolver = context.getContentResolver();
                ContentValues values = new ContentValues();
                values.put("data1", phoneNumber);
                mResolver.update(mUri, values, "mimetype=? and raw_contact_id=?",
                        new String[] { "vnd.android.cursor.item/phone_v2", id + "" });
                Toast.makeText(context, "保存成功!", Toast.LENGTH_SHORT).show();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(context, "保存失败", Toast.LENGTH_SHORT).show();
        return false;
    }

    /**
     * 删除联系人
     * @param name
     * @return
     */

    public static boolean delete(String name) {
        try {
            // 根据姓名求id
            Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
            ContentResolver resolver = context.getContentResolver();
            Cursor cursor = resolver.query(uri, new String[] { Data._ID }, "display_name = ?", new String[] { name }, null);
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(0);
                // 根据id删除data中的相应数据
                resolver.delete(uri, "display_name = ?", new String[] { name });
                uri = Uri.parse("content://com.android.contacts/data");
                resolver.delete(uri, "raw_contact_id = ?", new String[] { id + "" });
                Toast.makeText(context, "删除成功!", Toast.LENGTH_SHORT).show();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(context, "删除失败!", Toast.LENGTH_SHORT).show();
        return false;
    }










}
