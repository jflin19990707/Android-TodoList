package com.example.mytodolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.applandeo.materialcalendarview.EventDay;
import com.example.mytodolist.beans.Note;
import com.example.mytodolist.beans.Priority;
import com.example.mytodolist.beans.State;
import com.example.mytodolist.db.TodoContract;
import com.example.mytodolist.db.TodoDbHelper;
import com.example.mytodolist.fragment.AgendaFragment;
import com.example.mytodolist.fragment.FilesFragment;
import com.example.mytodolist.fragment.SettingFragment;
import com.example.mytodolist.fragment.TodoFragment;
import com.example.mytodolist.ui.FilesListAdapter;
import com.example.mytodolist.ui.NoteListAdapter;
import com.example.mytodolist.ui.NoteOperator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static List<EventDay>[] event;
    private RadioGroup mRgGroup;
    private FragmentManager fragmentManager;
    private final int show_tab_agenda = 0;  //日历
    private final int show_tab_todo = 1;    //需做事项
    private final int show_tab_files = 2;   //文件
    private final int show_tab_setting = 3; //设置
    private int initIndex = show_tab_agenda; //默认选择明细

    private int index = -100; //记录当前选项

    private static final String[] FRAGMENT_TAG = {"tab_agenda","tab_todo","tab_files","tab_setting"};
    private static final String PRV_SELINDEX = "PREV_SELINDEX";

    private AgendaFragment agendaFragment;
    private TodoFragment todoFragment;
    private FilesFragment filesFragment;
    private SettingFragment settingFragment;


    private TodoDbHelper dbHelper;
    private static SQLiteDatabase database;
    private static NoteListAdapter agendaAdapter;
    private static NoteListAdapter itemlistAdapter;
    private static NoteListAdapter todoAdapter;
    private static FilesListAdapter filesAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new TodoDbHelper(this);
        database = dbHelper.getWritableDatabase();

        todoAdapter = new NoteListAdapter(this);
        filesAdapter = new FilesListAdapter(this);
        itemlistAdapter = new NoteListAdapter(this);
        agendaAdapter = new NoteListAdapter(this);

        todoAdapter.refresh(loadNotesFromDatabase(database));
        agendaAdapter.refresh(loadNotesFromDatabase(database));
        fragmentManager = getSupportFragmentManager();

        if(savedInstanceState != null){
            //读取上一次界面保存时的tab选中的状态
            initIndex = savedInstanceState.getInt(PRV_SELINDEX,initIndex);

            agendaFragment = (AgendaFragment) fragmentManager.findFragmentByTag(FRAGMENT_TAG[0]);
            todoFragment = (TodoFragment) fragmentManager.findFragmentByTag(FRAGMENT_TAG[1]);
            filesFragment = (FilesFragment) fragmentManager.findFragmentByTag(FRAGMENT_TAG[2]);
            settingFragment = (SettingFragment) fragmentManager.findFragmentByTag(FRAGMENT_TAG[3]);

        }
        initView();
        List<Note> noteList = MainActivity.loadNotesFromDatabase(database);
        List<String> id = new ArrayList<>();
        List<String> name = new ArrayList<>();
        for(int i = 0;i<noteList.size();i++){
            id.add(String.valueOf(i));
            name.add(name+String.valueOf(i));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        database.close();
        database = null;
        dbHelper.close();
        dbHelper = null;
    }

    public static NoteListAdapter getTodoAdapter(){
        return todoAdapter;
    }

    public static NoteListAdapter getAgendaAdapter(){return  agendaAdapter;}

    public static NoteListAdapter getItemListAdapter(){
        return itemlistAdapter;
    }

    public static FilesListAdapter getFilesAdapter(){
        return filesAdapter;
    }

    public static void deleteNote(int position,SQLiteDatabase db) {
        Cursor cursor = null;
        long id;
        try {
            cursor = db.query(TodoContract.TodoNote.TABLE_NAME, null,
                    null, null,
                    null, null,
                    null );
            cursor.moveToPosition(position);
            id = cursor.getLong(cursor.getColumnIndex(TodoContract.TodoNote._ID));
        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        if (db == null) {
            return;
        }
        int rows = db.delete(TodoContract.TodoNote.TABLE_NAME,
                TodoContract.TodoNote._ID + "=?",
                new String[]{String.valueOf(id)});
        if (rows > 0) {
            todoAdapter.refresh(loadNotesFromDatabase(db));
            todoAdapter.notifyItemRemoved(position);

        }
    }

    public static void showDelete(final int postion, Context context, final SQLiteDatabase db) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(context).setTitle("删除记录")
                .setMessage("是否要删除该条记录").setIcon(R.drawable.tag_icon).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteNote(postion,db);
                        filesAdapter.refresh(FilesFragment.loadFilesFromDatabase(database));
                        filesAdapter.notifyDataSetChanged();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.create().show();
    }


    private void initView() {
        //获得RadioGroup控件
        mRgGroup = (RadioGroup)findViewById(R.id.rg_group);
        //调整radiobutton的大小
        RadioButton mradiobutton_agenda = (RadioButton) findViewById(R.id.rb_agenda);
        Drawable drawable =getResources().getDrawable(R.drawable.radiobutton_agenda);
        drawable.setBounds(0,0,50,50);
        mradiobutton_agenda.setCompoundDrawables(null,drawable,null,null);

        RadioButton mradiobutton_todo = (RadioButton) findViewById(R.id.rb_todo);
        Drawable drawable1 =getResources().getDrawable(R.drawable.radiobutton_todo);
        drawable1.setBounds(0,0,50,50);
        mradiobutton_todo.setCompoundDrawables(null,drawable1,null,null);

        RadioButton mradiobutton_files = (RadioButton) findViewById(R.id.rb_files);
        Drawable drawable2 =getResources().getDrawable(R.drawable.radiobutton_files);
        drawable2.setBounds(0,0,50,50);
        mradiobutton_files.setCompoundDrawables(null,drawable2,null,null);

        RadioButton mradiobutton_setting = (RadioButton) findViewById(R.id.rb_setting);
        Drawable drawable3 =getResources().getDrawable(R.drawable.radiobutton_setting);
        drawable3.setBounds(0,0,50,50);
        mradiobutton_setting.setCompoundDrawables(null,drawable3,null,null);




        setTabSelection(show_tab_agenda);
        //点击事件
        mRgGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                RadioButton mRadiobutton = (RadioButton)radioGroup.findViewById(checkedId);
                switch (checkedId){
                    case R.id.rb_agenda:  //日历
                        setTabSelection(show_tab_agenda);
                        break;
                    case R.id.rb_todo:    //需做事项
                        setTabSelection(show_tab_todo);
                        break;
                    case R.id.rb_files:   //文件
                        setTabSelection(show_tab_files);
                        break;
                    case R.id.rb_setting: //设置
                        setTabSelection(show_tab_setting);
                        break;
                }
            }
        });
    }



    public static List<Note> loadNotesFromDatabase(SQLiteDatabase db) {
        if (db == null) {
            return Collections.emptyList();
        }
        List<Note> result = new LinkedList<>();
        Cursor cursor = null;
        try {
            cursor = db.query(TodoContract.TodoNote.TABLE_NAME, null,
                    null, null,
                    null, null,
                    null);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndex(TodoContract.TodoNote._ID));
                int countstar = cursor.getInt(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_COUNTSTAR));
                String headline = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_HEADLINE));
                String files = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_FILES));
                int state = cursor.getInt(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_STATE));
                int intPriority = cursor.getInt(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_PRIORITY));

                String schedule = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_SCHEDULE));
                String deadline = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_DEADLINE));
                String show = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_SHOW));
                String tag = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_TAG));

                String repeat_show = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_REPEAT_SHOW));
                int repeat_show_num = cursor.getInt(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_REPEAT_SHOW_NUM));
                String content = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_CONTENT));

                Note note = new Note(id);
                note.setCountstar(countstar);
                note.setheadline(headline);
                note.setfiles(files);
                note.setState(State.from(state));
                note.setPriority(Priority.from(intPriority));
                note.setTag(tag);

                note.setschedule(schedule);
                note.setdeadline(deadline);
                note.setshow(show);

                note.setrepeat_show(repeat_show);
                note.setrepeat_show_num(repeat_show_num);
                note.setContent(content);

                result.add(note);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    private void setTabSelection(int id){
        if(id == index){
            return;
        }
        index = id;
        //开启一个Fragment事件
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //设置切换动画
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        //先隐藏掉所有的Fragment，以防由多个Fragment显示在界面上的情况
        hideFragment(transaction);
        switch (index){
            case show_tab_agenda:
                mRgGroup.check(R.id.rb_agenda);
                if(agendaFragment == null){
                    agendaFragment = new AgendaFragment();
                    transaction.add(R.id.fl_container,agendaFragment,FRAGMENT_TAG[index]);
                }else{
                    transaction.show(agendaFragment);
                }
                transaction.commit();
                break;
            case show_tab_todo:
                mRgGroup.check(R.id.rb_todo);
                if(todoFragment == null){
                    todoFragment = new TodoFragment();
                    transaction.add(R.id.fl_container,todoFragment,FRAGMENT_TAG[index]);
                }else{
                    transaction.show(todoFragment);
                }
                transaction.commit();
                break;
            case show_tab_files:
                mRgGroup.check(R.id.rb_files);
                if(filesFragment == null){
                    filesFragment = new FilesFragment();
                    transaction.add(R.id.fl_container,filesFragment,FRAGMENT_TAG[index]);
                }else{
                    transaction.show(filesFragment);
                }
                transaction.commit();
                break;
            case show_tab_setting:
                mRgGroup.check(R.id.rb_setting);
                if(settingFragment == null){
                    settingFragment = new SettingFragment();
                    transaction.add(R.id.fl_container,settingFragment,FRAGMENT_TAG[index]);
                }else{
                    transaction.show(settingFragment);
                }
                transaction.commit();
                break;

            default:
                break;
        }
    }

    private void hideFragment(FragmentTransaction transaction){
        if(agendaFragment != null){
            transaction.hide(agendaFragment);
        }
        if(todoFragment != null){
            transaction.hide(todoFragment);
        }
        if(filesFragment != null){
            transaction.hide(filesFragment);
        }
        if(settingFragment != null){
            transaction.hide(settingFragment);
        }
    }


    public static long stringToLong(String strTime, String formatType)
            throws ParseException {
        Date date = stringToDate(strTime, formatType); // String类型转成date类型
        if (date == null) {
            return 0;
        } else {
            long currentTime = dateToLong(date); // date类型转成long类型
            return currentTime;
        }
    }
    public static long dateToLong(Date date) {
        return date.getTime();
    }
    public static Date stringToDate(String strTime, String formatType)
            throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        date = formatter.parse(strTime);
        return date;
    }
    public static void setAlarmTime(Context context, long timeInMillis,int requestCode,String title) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context,AlarmReceiver.class);
        intent.putExtra("title",title);
        intent.putExtra("id",requestCode);
        PendingIntent sender = PendingIntent.getBroadcast(context, requestCode, intent,PendingIntent.FLAG_UPDATE_CURRENT);
        am.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, sender);
    }

}
