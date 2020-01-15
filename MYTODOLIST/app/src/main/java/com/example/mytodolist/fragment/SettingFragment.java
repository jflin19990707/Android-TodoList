package com.example.mytodolist.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.mytodolist.MainActivity;
import com.example.mytodolist.R;
import com.example.mytodolist.beans.Note;
import com.example.mytodolist.db.TodoDbHelper;
import com.example.mytodolist.ui.SwitchButton;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class SettingFragment extends Fragment implements View.OnClickListener{
    @Nullable
    private SwitchButton switch_accept_news, switch_sound, switch_shock, switch_loundspeaker;
    private LinearLayout ll_sound, ll_shock;
    private RelativeLayout rl_showTime;
    private TextView tv_timeSetting;
    private Boolean soundOpen,shockOpen;
    private final static int DIALOG_TIMESETTING = 1;
    public static String Time="";
    private TodoDbHelper dbHelper;
    private static SQLiteDatabase database;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =View.inflate(getContext(), R.layout.fragment_setting,null);

        //接收消息通知开关
        switch_accept_news = (SwitchButton) view.findViewById(R.id.switch_accept_news);
        //声音开关
        switch_sound = (SwitchButton) view.findViewById(R.id.switch_sound);
        //震动开关
        switch_shock = (SwitchButton) view.findViewById(R.id.switch_shock);
        //使用扬声器播放语音
        switch_loundspeaker = (SwitchButton) view.findViewById(R.id.switch_advertise);

        //声音item布局
        ll_sound = (LinearLayout) view.findViewById(R.id.ll_sound);
        //震动item布局
        ll_shock = (LinearLayout) view.findViewById(R.id.ll_shock);
        //设置时间item布局
        rl_showTime = (RelativeLayout)view.findViewById(R.id.rl_showTime);

        tv_timeSetting = (TextView)view.findViewById(R.id.tv_timeSetting);

        //开关布局
        switch_accept_news.setOnClickListener(this);
        switch_sound.setOnClickListener(this);
        switch_shock.setOnClickListener(this);
        switch_loundspeaker.setOnClickListener(this);
        tv_timeSetting.setOnClickListener(this);

        soundOpen = true;
        shockOpen = true;

        dbHelper = new TodoDbHelper(getContext());
        database = dbHelper.getWritableDatabase();

        ll_sound.setVisibility(View.GONE);
        ll_shock.setVisibility(View.GONE);
        rl_showTime.setVisibility(View.GONE);


        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //声音开关
            case R.id.switch_accept_news:

                if (switch_accept_news.isSwitchOpen()) {//开关为打开状态
                    //关闭逻辑
                    switch_accept_news.closeSwitch();
                    ll_sound.setVisibility(View.GONE);
                    ll_shock.setVisibility(View.GONE);
                    rl_showTime.setVisibility(View.GONE);
                    Toast.makeText(getContext(),"关闭了通知",Toast.LENGTH_SHORT);

                } else {
                    //打开逻辑
                    switch_accept_news.openSwitch();
                    ll_sound.setVisibility(View.VISIBLE);
                    ll_shock.setVisibility(View.VISIBLE);
                    rl_showTime.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(),"打开了通知",Toast.LENGTH_SHORT);



                }
                break;
            case R.id.switch_sound:
                if (switch_sound.isSwitchOpen()) {
                    switch_sound.closeSwitch();
                    soundOpen = false;
                } else {
                    switch_sound.openSwitch();
                    soundOpen = true;
                }
                break;
            case R.id.switch_shock:
                if (switch_shock.isSwitchOpen()) {
                    switch_shock.closeSwitch();
                    shockOpen = false;
                } else {
                    switch_shock.openSwitch();
                    shockOpen = true;
                }
                break;
            case R.id.switch_advertise:
                if (switch_loundspeaker.isSwitchOpen()) {
                    switch_loundspeaker.closeSwitch();
                } else {
                    switch_loundspeaker.openSwitch();
                }
                break;
            case R.id.tv_timeSetting:
                onCreateDialog(DIALOG_TIMESETTING).show();
        }
    }
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch (id){
            case DIALOG_TIMESETTING: //状态选择单选对话框
                AlertDialog.Builder builder =new android.app.AlertDialog.Builder(getContext());
                builder.setTitle("设置提醒时间");
                builder.setSingleChoiceItems(R.array.time_choose, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {

                        String time = getResources().getStringArray(R.array.time_choose)[j];
                        tv_timeSetting.setText(time);
                        Time = " "+time+":00";
                        List<Note> noteList = MainActivity.loadNotesFromDatabase(database);
                        List<String> id = new ArrayList<>();
                        List<String> name = new ArrayList<>();
                        for(int i = 0;i<noteList.size();i++){
                            id.add(String.valueOf(i));
                            name.add(name+String.valueOf(i));
                        }
                        Calendar cal = Calendar.getInstance();
                        //setAlarmTime(this,cal.getTimeInMillis());
                        for(int i = 0;i< noteList.size();i++) {
                            String strShow = noteList.get(i).getshow();
                            String title = noteList.get(i).getheadline();
                            Long time2 = null;
                            Long DayTime = null;
                            try {
                                Date date1 = null;
                                date1 = cal.getTime();
                                Date date2 = null;
                                strShow += SettingFragment.getShowTime();
                                date2 = MainActivity.stringToDate(strShow,"yyyy-MM-dd HH:mm:ss");
                                GregorianCalendar cal1 = new GregorianCalendar();
                                GregorianCalendar cal2 = new GregorianCalendar();
                                cal1.setTime(date1);
                                cal2.setTime(date2);
                                time2 = (long) (cal2.getTimeInMillis() - cal1.getTimeInMillis());
                                DayTime = (long)(cal2.getTimeInMillis()-cal1.getTimeInMillis())/(1000*3600*24);//从间隔毫秒变成间隔天数
                                if(DayTime==0){
                                    if(time2 >=0){
                                        time2 = MainActivity.dateToLong(date2);
                                        MainActivity.setAlarmTime(getContext(),time2,i,title);
                                    }
                                    else{
                                        continue;
                                    }
                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                dialog = builder.create();
                break;

        }
        return  dialog;
    }
    public static String getShowTime(){
        if(Time.equals(""))
            Time = " 16:20:00";
        return Time;
    }

}
