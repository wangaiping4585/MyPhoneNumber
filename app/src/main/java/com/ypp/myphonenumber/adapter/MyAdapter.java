package com.ypp.myphonenumber.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ypp.myphonenumber.MainActivity;
import com.ypp.myphonenumber.R;
import com.ypp.myphonenumber.entity.PhoneInfo;
import com.ypp.myphonenumber.util.Utils;

import java.util.List;

/**
 * Created by uilubo on 2015/9/11.
 */
public class MyAdapter extends BaseAdapter {

    List<PhoneInfo> list;
    LayoutInflater inflater;

    public MyAdapter(Context context) {
        this.inflater=LayoutInflater.from(context);
    }

    public void setList(List<PhoneInfo> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return (list == null)?0:list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.item, null);
            holder = new ViewHolder();
            holder.font = (TextView) convertView.findViewById(R.id.font);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.number = (TextView) convertView.findViewById(R.id.number);
            holder.tv_show= (RelativeLayout) convertView.findViewById(R.id.tv_show);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        PhoneInfo item = list.get(position);
        if(item.getPhoneName()!=null) {
            //文本进行高亮显示
            if (MainActivity.KEYWORD == null || MainActivity.KEYWORD.equals(" ")) {
                //处于普通状态(未进行应用搜索)
                holder.name.setText(item.getPhoneName());
                holder.number.setText(item.getPhoneNumber());

            } else {

                if (Utils.isNumber(MainActivity.KEYWORD)) {
                    //应用搜索状态,姓名或号码进行高亮
                    holder.name.setText(item.getPhoneName());
                    holder.number.setText(Utils.highLightText(item.getPhoneNumber(), MainActivity.KEYWORD));

                } else {
                    holder.name.setText(Utils.highLightText(item.getPhoneName(), MainActivity.KEYWORD));
                    holder.number.setText(item.getPhoneNumber());
                }
            }

            if (item.getPhoneName() != null) {
                holder.font.setText(item.getPhoneName().trim().substring(0, 1));
            }
            UpdateColor(holder, item.getPhoneNumber());   //根据号码尾数改变大字体背景颜色
        }
        return convertView;
    }

    public  class ViewHolder{
        TextView name;
        TextView number;
        TextView font;
        RelativeLayout tv_show;
    }

    /**
     * 改变背景颜色
     */
    public void UpdateColor(ViewHolder holder,String phonenNumber) {
        if (phonenNumber.length() > 0) {
            String num = phonenNumber.substring(phonenNumber.length() - 1);

        if (num.equals((" " + 0).trim())) {
            holder.tv_show.setBackgroundResource(R.color.colors0);
        } else if (num.equals((" " + 1).trim())) {
            holder.tv_show.setBackgroundResource(R.color.colors1);
        } else if (num.equals((" " + 2).trim())) {
            holder.tv_show.setBackgroundResource(R.color.colors2);
        } else if (num.equals((" " + 3).trim())) {
            holder.tv_show.setBackgroundResource(R.color.colors3);
        } else if (num.equals((" " + 4).trim())) {
            holder.tv_show.setBackgroundResource(R.color.colors4);
        } else if (num.equals((" " + 5).trim())) {
            holder.tv_show.setBackgroundResource(R.color.colors5);
        } else if (num.equals((" " + 6).trim())) {
            holder.tv_show.setBackgroundResource(R.color.colors6);
        } else if (num.equals((" " + 7).trim())) {
            holder.tv_show.setBackgroundResource(R.color.colors7);
        } else if (num.equals((" " + 8).trim())) {
            holder.tv_show.setBackgroundResource(R.color.colors8);
        } else if (num.equals((" " + 9).trim())) {
            holder.tv_show.setBackgroundResource(R.color.colors9);
        }
        }
    }
}
