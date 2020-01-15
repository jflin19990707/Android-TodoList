package com.example.mytodolist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.annimon.stream.Stream;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.builders.DatePickerBuilder;
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener;
import com.example.mytodolist.beans.Priority;
import com.example.mytodolist.beans.State;
import com.example.mytodolist.beans.Tag;
import com.example.mytodolist.db.TagContract;
import com.example.mytodolist.db.TagDBOpenHelper;
import com.example.mytodolist.db.TodoContract;
import com.example.mytodolist.db.TodoDbHelper;
import com.example.mytodolist.fragment.AgendaFragment;
import com.example.mytodolist.fragment.FilesFragment;
import com.example.mytodolist.scrollview.CommonPopWindow;
import com.example.mytodolist.scrollview.GetConfigReq;
import com.example.mytodolist.scrollview.PickerScrollView;
import com.example.mytodolist.ui.NoteListAdapter;
import com.example.mytodolist.ui.TagAdapater;
import com.google.gson.Gson;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class AddTodoActivity extends AppCompatActivity implements View.OnClickListener, CommonPopWindow.ViewClickListener,OnSelectDateListener{
    private final static int DIALOG_STATE = 1;
    private final static int DIALOG_PRIORITY = 2;
    private final static int DIALOG_CANCEL = 3;

    Button btn_cancel;
    Button btn_save;
    Button btn_done;

    EditText et_headline;
    EditText et_document;
    EditText et_searchTag;
    EditText ev_item_note;
    EditText et_search;

    TextView tv_deadline;
    TextView tv_show;
    TextView tv_priority;
    TextView tv_state;
    TextView tv_scheduled;
    TextView tv_repeatShow;
    TextView tv_repeatShow_num;
    TextView tv_tag;
    TextView tv_addTag;

    private Cursor cursor;
    private Boolean flag = false;
    private Boolean flag_sch = false;
    private Boolean flag_show = false;
    private Boolean flag_dead = false;
    private static NoteListAdapter itemListAdapter;
    private String strTime;
    private Boolean isFromAgenda;
    private Boolean isFromFileActivity;
    private Boolean isFromToDo;
    private Boolean filter_todo;
    private Boolean filter_done;
    private Boolean filter_overdue;
    private Boolean filter_priority;
    private Boolean filter_next7day;
    private String headLine = "";


    int mYear,mMonth,mDay;

    private List<GetConfigReq.DatasBean> datasBeanList;
    private List<GetConfigReq.DatasBean> datasBeanList2;
    private String categoryName;
    private String categoryNumber;
    private TodoDbHelper dbHelper;
    private TagDBOpenHelper tagDBOpenHelper;
    private static SQLiteDatabase database;
    private static SQLiteDatabase db_tag;
    private int cursorPosition;
    String[] whereArgs=new String[1];
    private String scheduledTime;
    private String fileName;

    private List<Tag> lists;
    private RecyclerView mTagRvView;
    private TagAdapater tagAdapater;
    private NoteListAdapter agendaAdapter;
    private NoteListAdapter todoAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtodo);
        Intent intent = getIntent();
        cursorPosition = -2;
        isFromAgenda = false;
        isFromToDo = false;

        //从别的fragment传来的数据
        cursorPosition = intent.getIntExtra("cursorPosition",-2);
        scheduledTime = intent.getStringExtra("scheduledTime");
        isFromAgenda = intent.getBooleanExtra("FromAgenda",false);
        isFromFileActivity = intent.getBooleanExtra("FromFileActivity",false);
        isFromToDo = intent.getBooleanExtra("FromToDo",false);
        headLine = intent.getStringExtra("HeadLine");
        filter_done = intent.getBooleanExtra("filter_done",false);
        filter_todo = intent.getBooleanExtra("filter_todo",false);
        filter_overdue = intent.getBooleanExtra("filter_overdue",false);
        filter_priority = intent.getBooleanExtra("filter_priority",false);
        filter_next7day = intent.getBooleanExtra("filter_next7day",false);
        fileName = intent.getStringExtra("filename");


        dbHelper = new TodoDbHelper(this);
        database = dbHelper.getWritableDatabase();

        tagDBOpenHelper = new TagDBOpenHelper(this);
        db_tag = tagDBOpenHelper.getWritableDatabase();

        initView();
        getDate();
        initData();
        initRepeatNumber();
        initListener();
        initRepeatNumber();
        itemListAdapter = MainActivity.getItemListAdapter();
        agendaAdapter = MainActivity.getAgendaAdapter();
        todoAdapter = MainActivity.getTodoAdapter();

       // mAdapter = new NoteListAdapter(context);
    }


    //设置点击监听
    private  void initListener(){
        tv_priority.setOnClickListener(this);
        tv_state.setOnClickListener(this);
        tv_tag.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        tv_scheduled.setOnClickListener(this);
        tv_deadline.setOnClickListener(this);
        tv_show.setOnClickListener(this);

        tv_repeatShow.setOnClickListener(this);
        tv_repeatShow_num.setOnClickListener(this);

    }

    //初始化View
    private void initView() {
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);

        et_headline = (EditText) findViewById(R.id.et_title);
        et_document = (EditText) findViewById(R.id.document_name);
        tv_deadline = (TextView) findViewById(R.id.item_deadline);
        tv_show = (TextView) findViewById(R.id.item_showtime);
        tv_priority = (TextView) findViewById(R.id.item_priority);
        tv_state = (TextView) findViewById(R.id.item_state);
        tv_tag = (TextView) findViewById(R.id.item_tag);
        tv_scheduled = (TextView) findViewById(R.id.item_scheduled);
        tv_repeatShow = (TextView)findViewById(R.id.repeat_show);
        tv_repeatShow_num = (TextView)findViewById(R.id.repeat_show_number);
        ev_item_note = (EditText) findViewById(R.id.item_note);



        //设置edittext中icon的大小
        Drawable drawable = getResources().getDrawable(R.drawable.document_icon);
        drawable.setBounds(25, 0, 85, 60);
        et_document.setCompoundDrawables(drawable, null, null, null);

        Drawable drawable1 = getResources().getDrawable(R.drawable.deadline_icon);
        drawable1.setBounds(25, 0, 85, 60);
        tv_deadline.setCompoundDrawables(drawable1, null, null, null);

        Drawable drawable2 = getResources().getDrawable(R.drawable.show_icon);
        drawable2.setBounds(25, 0, 85, 60);
        tv_show.setCompoundDrawables(drawable2, null, null, null);

        Drawable drawable3 = getResources().getDrawable(R.drawable.priority_icon);
        drawable3.setBounds(25, 0, 85, 60);
        tv_priority.setCompoundDrawables(drawable3, null, null, null);

        Drawable drawable4 = getResources().getDrawable(R.drawable.state_icon);
        drawable4.setBounds(25, 0, 85, 60);
        tv_state.setCompoundDrawables(drawable4, null, null, null);

        Drawable drawable5 = getResources().getDrawable(R.drawable.tag_icon);
        drawable5.setBounds(25, 0, 85, 60);
        tv_tag.setCompoundDrawables(drawable5, null, null, null);

        Drawable drawable6 = getResources().getDrawable(R.drawable.agenda);
        drawable6.setBounds(25, 0, 85, 60);
        tv_scheduled.setCompoundDrawables(drawable6, null, null, null);

        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");

        try {
            Cursor cursor = null;
            //判断是修改还是添加
            if(cursorPosition != -2) {
                if(isFromAgenda){
                    //判断是否来自AgendaFragment
                    //从AgendaFragment点击item来进行修改
                    cursor = database.query(TodoContract.TodoNote.TABLE_NAME, null,
                            TodoContract.TodoNote.COLUMN_DEADLINE+"=?", new String[]{scheduledTime},null,
                            null,
                            null);
                }
                else if(isFromToDo){
                    //判断是否来自TODOFragment
                    if(!headLine.equals("")){
                        //注意这里是模糊查询
                        cursor = database.query(TodoContract.TodoNote.TABLE_NAME, null,
                                TodoContract.TodoNote.COLUMN_HEADLINE+" like '%"+headLine+"%'",null,
                                null,null,
                                null);
                    }
                    else{
                        if(filter_todo){
                            cursor = database.query(TodoContract.TodoNote.TABLE_NAME, null,
                                    TodoContract.TodoNote.COLUMN_STATE+"=?",new String[]{String.valueOf(State.TODO.intValue)},
                                    null,null,
                                    null);
                            Log.i("todo","todo");
                        }else if(filter_done){
                            cursor = database.query(TodoContract.TodoNote.TABLE_NAME, null,
                                    TodoContract.TodoNote.COLUMN_STATE+"=?",new String[]{String.valueOf(State.DONE.intValue)},
                                    null,null,
                                    null);
                            Log.i("done","done");
                        }else if(filter_priority){
                            cursor = database.query(TodoContract.TodoNote.TABLE_NAME, null,
                                    TodoContract.TodoNote.COLUMN_PRIORITY+"<>?",new String[]{String.valueOf(Priority.NONE.intValue)},
                                    null,null,
                                    null);
                            Log.i("priority","priority");
                        }else if(filter_overdue){
                            Calendar calendar = Calendar.getInstance();
                            String today = sdf.format(calendar.getTime());
                            cursor = database.query(TodoContract.TodoNote.TABLE_NAME, null,
                                    TodoContract.TodoNote.COLUMN_DEADLINE+"<?",new String[]{today},
                                    null,null,
                                    null);
                            Log.i("overdue","overdue");

                        }else if(filter_next7day){
                            Calendar now = Calendar.getInstance();
                            Calendar next7Day = Calendar.getInstance();
                            next7Day.add(Calendar.DAY_OF_YEAR,7);
                            String rightNow = sdf.format(now.getTime());
                            String next7day = sdf.format(next7Day.getTime());
                            cursor = database.query(TodoContract.TodoNote.TABLE_NAME, null,
                                    TodoContract.TodoNote.COLUMN_DEADLINE+"<=?"+" and "+ TodoContract.TodoNote.COLUMN_DEADLINE+">=?",new String[]{next7day,rightNow},
                                    null,null,
                                    null);
                            Log.i("next7","next7");

                        }else{
                            cursor = database.query(TodoContract.TodoNote.TABLE_NAME, null,
                                    null, null,
                                    null, null,
                                    null);
                            Log.i("else","else");
                        }

                    }

                }
                else{
                    cursor = database.query(TodoContract.TodoNote.TABLE_NAME, null,
                            null, null,
                            null, null,
                            null);
                }
                flag = true;//修改的话则flag为true
                cursor.moveToPosition(cursorPosition);

                String headline = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_HEADLINE));
                long id = cursor.getLong(cursor.getColumnIndex(TodoContract.TodoNote._ID));
                String files = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_FILES));
                int state = cursor.getInt(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_STATE));
                int intPriority = cursor.getInt(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_PRIORITY));

                String tag = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_TAG));
                String schedule = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_SCHEDULE));
                String deadline = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_DEADLINE));
                String show = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_SHOW));

                String repeat_show = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_REPEAT_SHOW));
                int repeat_show_num = cursor.getInt(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_REPEAT_SHOW_NUM));
                String content = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_CONTENT));

                String str_repeat_show_num = String.valueOf(repeat_show_num);

                whereArgs[0] = String.valueOf(id);
                et_headline.setText(headline);
                et_document.setText(files);
                tv_state.setText(State.intToString(state));
                tv_priority.setText(Priority.intToString(intPriority));
                tv_tag.setText(tag);

                tv_scheduled.setText(schedule);
                tv_deadline.setText(deadline);
                tv_show.setText(show);

                tv_repeatShow.setText(repeat_show);
                tv_repeatShow_num.setText(str_repeat_show_num);
                ev_item_note.setText(content);

            }
            if(scheduledTime!=null){
                tv_scheduled.setText(scheduledTime);
            }
        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }

    }

    //获取时间
    private void getDate(){
        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
    }

    //初始化repeat的数据
    private void initData(){
        //模拟请求后台返回数据
        String response = "{\"ret\":0,\"msg\":\"succes,\",\"datas\":[{\"categoryName\":\"days\",\"state\":\"1\"},{\"categoryName\":\"weeks\",\"state\":\"1\"}," +
                "{\"categoryName\":\"months\",\"state\":\"1\"},{\"categoryName\":\"years\",\"state\":\"1\"}]}";
        GetConfigReq getConfigReq = new Gson().fromJson(response, GetConfigReq.class);
        //0请求表示成功
        if (getConfigReq.getRet() == 0) {
            //滚动选择数据集合
            datasBeanList = getConfigReq.getDatas();
        }
    }

    private void initRepeatNumber(){
        //模拟请求后台返回数据
        String response = "{\"ret\":0,\"msg\":\"succes,\",\"datas\":[{\"categoryName\":\"0\",\"state\":\"1\"},{\"categoryName\":\"1\",\"state\":\"1\"}," +
                "{\"categoryName\":\"2\",\"state\":\"1\"},{\"categoryName\":\"3\",\"state\":\"1\"},{\"categoryName\":\"4\",\"state\":\"1\"}," +
                "{\"categoryName\":\"5\",\"state\":\"1\"},{\"categoryName\":\"6\",\"state\":\"1\"},{\"categoryName\":\"7\",\"state\":\"1\"}," +
                "{\"categoryName\":\"8\",\"state\":\"1\"},{\"categoryName\":\"9\",\"state\":\"1\"},{\"categoryName\":\"10\",\"state\":\"1\"}]}";
        GetConfigReq getConfigReq = new Gson().fromJson(response, GetConfigReq.class);
        //0请求表示成功
        if (getConfigReq.getRet() == 0) {
            //滚动选择数据集合
            datasBeanList2 = getConfigReq.getDatas();
        }
    }

    //设置点击事件
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.item_scheduled: //记录日期
                flag_sch = true;
                DatePickerBuilder builder = new DatePickerBuilder(this, this)
                        .pickerType(CalendarView.ONE_DAY_PICKER)
                        .setDate(Calendar.getInstance())
                        .setHeaderColor(R.color.purple)
                        .setHeaderLabelColor(R.color.white)
                        .setAbbreviationsBarColor(R.color.darkPink)
                        .setAbbreviationsLabelsColor(R.color.white)
                        .setPagesColor(R.color.pink)
                        .setSelectionColor(R.color.white)
                        .setSelectionLabelColor(R.color.pink)
                        .setDaysLabelsColor(R.color.white)
                        .setTodayLabelColor(R.color.black)
                        .setDialogButtonsColor(R.color.white);
                com.applandeo.materialcalendarview.DatePicker datePicker = builder.build();
                datePicker.show();
                break;
            case R.id.item_deadline: //截止日期
                flag_dead = true;
                DatePickerBuilder builder1 = new DatePickerBuilder(this, this)
                        .pickerType(CalendarView.ONE_DAY_PICKER)
                        .setDate(Calendar.getInstance())
                        .setHeaderColor(R.color.purple)
                        .setHeaderLabelColor(R.color.white)
                        .setAbbreviationsBarColor(R.color.darkPink)
                        .setAbbreviationsLabelsColor(R.color.white)
                        .setPagesColor(R.color.pink)
                        .setSelectionColor(R.color.white)
                        .setSelectionLabelColor(R.color.pink)
                        .setDaysLabelsColor(R.color.white)
                        .setTodayLabelColor(R.color.black)
                        .setDialogButtonsColor(R.color.white);
                com.applandeo.materialcalendarview.DatePicker datePicker1 = builder1.build();
                datePicker1.show();
                break;
            case R.id.item_showtime: //显示日期
                flag_show = true;
                DatePickerBuilder builder2 = new DatePickerBuilder(this, this)
                        .pickerType(CalendarView.ONE_DAY_PICKER)
                        .setDate(Calendar.getInstance())
                        .setHeaderColor(R.color.purple)
                        .setHeaderLabelColor(R.color.white)
                        .setAbbreviationsBarColor(R.color.darkPink)
                        .setAbbreviationsLabelsColor(R.color.white)
                        .setPagesColor(R.color.pink)
                        .setSelectionColor(R.color.white)
                        .setSelectionLabelColor(R.color.pink)
                        .setDaysLabelsColor(R.color.white)
                        .setTodayLabelColor(R.color.black)
                        .setDialogButtonsColor(R.color.white);
                com.applandeo.materialcalendarview.DatePicker datePicker2 = builder2.build();
                datePicker2.show();
                break;
            case R.id.btn_cancel: //取消按钮
                if(cursorPosition == -2)
                    showDialog(DIALOG_CANCEL);//如果是新建的，取消则不保存
                else
                    finish(); //结束activity
                break;
            case R.id.btn_save: //保存按钮
                CharSequence content = ev_item_note.getText();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(AddTodoActivity.this,
                            "有项目没有填写", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean succeed = saveNote2Database(content.toString().trim(),
                        getSelectedPriority(),getSelectedState(),flag);
                if (succeed) {
                    if(flag){
                        Toast.makeText(AddTodoActivity.this,
                                "修改成功", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(AddTodoActivity.this,
                                "添加成功", Toast.LENGTH_SHORT).show();
                    }
                    setResult(Activity.RESULT_OK);
                } else {
                    Toast.makeText(AddTodoActivity.this,
                            "发生错误", Toast.LENGTH_SHORT).show();
                }
                //更新数据库
                if(isFromAgenda){
                    agendaAdapter.refresh(AgendaFragment.loadSeletedNotesFromDatabase(database));
                    agendaAdapter.notifyDataSetChanged();
                }
                else if(isFromFileActivity) {
                    itemListAdapter.refresh(fileActivity.loaditemsFromDatabase(database, fileName));
                    itemListAdapter.notifyDataSetChanged();
                }
                todoAdapter.refresh(MainActivity.loadNotesFromDatabase(database));
                todoAdapter.notifyDataSetChanged();
                List<String> files;
                files=FilesFragment.loadFilesFromDatabase(database);
                if(files!=null&&files.size()!=0){
                    MainActivity.getFilesAdapter().refresh(files);
                    MainActivity.getFilesAdapter().notifyDataSetChanged();
                }

                AddTodoActivity.this.finish();
                break;
            case R.id.item_state: //状态选择
                showDialog(DIALOG_STATE);
                break;
            case R.id.item_priority: //优先级选择
                showDialog(DIALOG_PRIORITY);
                break;
            case R.id.item_tag://标签选择或创建
                showTagDialog();
                break;
            case R.id.repeat_show_number:
                setAddressSelectorPopup(view,R.layout.dialog_repeatnumber,tv_repeatShow_num);
                break;

            case R.id.repeat_show:
                setAddressSelectorPopup(view,R.layout.dialog_repeat,tv_repeatShow);
                break;

            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.close();
        database = null;
        dbHelper.close();
        dbHelper = null;
    }

    private boolean saveNote2Database(String content, Priority priority,State state,boolean changeflag) {
        if (database == null || TextUtils.isEmpty(content)) {
            return false;
        }
        ContentValues values = new ContentValues();
        long rowId;

        values.put(TodoContract.TodoNote.COLUMN_COUNTSTAR,1);
        values.put(TodoContract.TodoNote.COLUMN_HEADLINE, et_headline.getText().toString());
        values.put(TodoContract.TodoNote.COLUMN_FILES, et_document.getText().toString());
        values.put(TodoContract.TodoNote.COLUMN_STATE, state.intValue);
        values.put(TodoContract.TodoNote.COLUMN_PRIORITY, priority.intValue);
        values.put(TodoContract.TodoNote.COLUMN_TAG, tv_tag.getText().toString());

        values.put(TodoContract.TodoNote.COLUMN_SCHEDULE, tv_scheduled.getText().toString());
        values.put(TodoContract.TodoNote.COLUMN_DEADLINE, tv_deadline.getText().toString());
        values.put(TodoContract.TodoNote.COLUMN_SHOW, tv_show.getText().toString());
        values.put(TodoContract.TodoNote.COLUMN_REPEAT_SHOW, tv_repeatShow.getText().toString());
        values.put(TodoContract.TodoNote.COLUMN_REPEAT_SHOW_NUM, tv_repeatShow_num.getText().toString());

        values.put(TodoContract.TodoNote.COLUMN_CONTENT, content);
        if(changeflag){
            //更新数据库修改内容
            rowId = database.update(TodoContract.TodoNote.TABLE_NAME,values, "_id=?",whereArgs);
        }else {
            //添加新的数据
            rowId = database.insert(TodoContract.TodoNote.TABLE_NAME, null, values);
        }
        return rowId != -1;
    }

    private boolean saveTag2Database(String content) {
        if (db_tag == null || TextUtils.isEmpty(content)) {
            return false;
        }
        ContentValues values = new ContentValues();
        long rowId;

        values.put(TagContract.Tag.COLUMN_TAGNAME, tv_addTag.getText().toString());
            //更新数据库修改内容
            rowId = db_tag.insert(TagContract.Tag.TABLE_NAME, null, values);
        return rowId != -1;
    }

    private State getSelectedState() {
        switch (tv_state.getText().toString()) {
            case "TODO":
                return State.TODO;
            case "DONE":
                return State.DONE;
            default:
                return State.NONE;
        }
    }

    private Priority getSelectedPriority() {
        switch (tv_priority.getText().toString()) {
            case "#A":
                return Priority.A;
            case "#B":
                return Priority.B;
            case "#C":
                return Priority.C;
            case "#D":
                return Priority.D;
            default:
                return Priority.NONE;
        }
    }



    //自定义的显示TagDialog
    private void showTagDialog(){
        LayoutInflater inflater = getLayoutInflater();
        //通过inflate加载出自定义布局
        View view = inflater.inflate(R.layout.dialog_tag,null);
        final Dialog dialog = new Dialog(this,R.style.Theme_AppCompat_Light_NoActionBar);
        dialog.setContentView(view);

        //调整搜索图标的大小
        et_search = (EditText) view.findViewById(R.id.et_searchtag);
        Drawable drawable =getResources().getDrawable(R.drawable.search_icon);
        drawable.setBounds(15,0,65,50);
        et_search.setCompoundDrawables(drawable,null,null,null);

        //调整增加tag的图标大小
        tv_addTag = (TextView) view.findViewById(R.id.add_new_tag);
        Drawable drawable2 =getResources().getDrawable(R.drawable.add_tag);
        drawable2.setBounds(15,0,65,50);
        tv_addTag.setCompoundDrawables(drawable2,null,null,null);


        //适配Tagdialog中的recyclerView

        mTagRvView = (RecyclerView)view.findViewById(R.id.tag_recyclerview);
        mTagRvView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        lists = new ArrayList<>();
        tagAdapater = new TagAdapater(this,lists);
        mTagRvView.setAdapter(tagAdapater);
        tagAdapater.refresh(loadTagFromDatabase(db_tag));

        //设置edittex变化后获取内容
        et_searchTag = view.findViewById(R.id.et_searchtag);
        et_searchTag.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tv_addTag.setEnabled(true);
                if(queryTagFromDatabase(db_tag,s)!=null){
                    tv_addTag.setText(null);
                    tv_addTag.setHint(null);
                    tv_addTag.setEnabled(false);
                }else{
                    tv_addTag.setText(s.toString());
                    tagAdapater.refresh(queryBlurTagFromDatabase(db_tag,s));
                }
            }
        });

        //设置点击addTag textview之后的事件
        tv_addTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence content = tv_addTag.getText();
                boolean succeed = saveTag2Database(content.toString().trim());
                if(!succeed){
                    Toast.makeText(AddTodoActivity.this,
                            "发生错误", Toast.LENGTH_SHORT).show();
                }
                tagAdapater.refresh(loadTagFromDatabase(db_tag));
                tagAdapater.notifyItemInserted(loadTagFromDatabase(db_tag).size());
                tv_addTag.setText(null);
            }
        });


        //设置确认按钮的监听函数
        btn_done = (Button)view.findViewById(R.id.btn_done);
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringBuilder sb=new StringBuilder();
                for(int i=0;i<tagAdapater.getChecklist().size();i++){
                    String tagName=tagAdapater.getChecklist().get(i);
                    sb.append(tagName+" ");
                }
                tv_tag.setText(sb);
                dialog.dismiss();
            }
        });

        dialog.show();
        // 将对话框的大小按屏幕大小的百分比设置
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = (int)(display.getWidth() * 0.8); //设置宽度
        lp.height = (int)(display.getHeight()*0.6); //设置高度
        dialog.getWindow().setAttributes(lp);
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch (id){
            case DIALOG_STATE: //状态选择单选对话框
                AlertDialog.Builder builder =new android.app.AlertDialog.Builder(this);
                builder.setTitle("设置状态");
                builder.setSingleChoiceItems(R.array.state_choose, 2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i==2){
                            //如果是"无"的话把textview清空
                            tv_state.setText(null);
                        }else{
                            String state = getResources().getStringArray(R.array.state_choose)[i];
                            tv_state.setText(state);
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
            case DIALOG_PRIORITY: //优先度选择单选对话框
                AlertDialog.Builder builder2 =new android.app.AlertDialog.Builder(this);
                builder2.setTitle("设置优先级");
                builder2.setSingleChoiceItems(R.array.priority_choose, 4, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i==4){
                            tv_priority.setText(null);
                        }else{
                            String priority = getResources().getStringArray(R.array.priority_choose)[i];
                            tv_priority.setText("#"+priority);
                        }
                    }
                });
                builder2.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                dialog = builder2.create();
                break;
            case DIALOG_CANCEL: //优先度选择单选对话框
                AlertDialog.Builder builder3 =new android.app.AlertDialog.Builder(this);
                builder3.setTitle("确定删除该TODO");
                builder3.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AddTodoActivity.this.finish();
                    }
                });
                builder3.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                dialog = builder3.create();
                break;
        }
        return  dialog;
    }

    private void setAddressSelectorPopup(View v, int layout,TextView textView) {
        int screenHeigh = getResources().getDisplayMetrics().heightPixels;

        CommonPopWindow.newBuilder()
                .setView(layout)
                .setTextview(textView)
                .setAnimationStyle(R.style.Animation_Design_BottomSheetDialog)
                .setBackgroundDrawable(new BitmapDrawable())
                .setSize(ViewGroup.LayoutParams.MATCH_PARENT, Math.round(screenHeigh * 0.3f))
                .setViewOnClickListener(this)
                .setBackgroundDarkEnable(true)
                .setBackgroundAlpha(0.7f)
                .setBackgroundDrawable(new ColorDrawable(999999))
                .build(this)
                .showAsBottom(v);
    }


    public void getChildView(final PopupWindow mPopupWindow, View view, int mLayoutResId, final TextView textView) {
        switch (mLayoutResId){
            case R.layout.dialog_repeatnumber:
                TextView imgBtn2 = view.findViewById(R.id.imgnumber_complete);
                PickerScrollView addressSelector2 = view.findViewById(R.id.number_address);
                //设置数据，默认选择第一条
                addressSelector2.setData(datasBeanList2);
                addressSelector2.setSelected(0);
                categoryNumber = "0";

                //滚动监听
                addressSelector2.setOnSelectListener(new PickerScrollView.onSelectListener() {
                    @Override
                    public void onSelect(GetConfigReq.DatasBean pickers) {
                        categoryNumber = pickers.getCategoryName();
                    }
                });

                //完成按钮
                imgBtn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPopupWindow.dismiss();
                        textView.setText("+"+categoryNumber);
                    }
                });
                break;
            case R.layout.dialog_repeat:
                TextView imgBtn = view.findViewById(R.id.img_complete);
                PickerScrollView addressSelector = view.findViewById(R.id.address);
                //设置数据，默认选择第一条
                addressSelector.setData(datasBeanList);
                addressSelector.setSelected(0);
                categoryName = "days";

                //滚动监听
                addressSelector.setOnSelectListener(new PickerScrollView.onSelectListener() {
                    @Override
                    public void onSelect(GetConfigReq.DatasBean pickers) {
                        categoryName = pickers.getCategoryName();
                    }
                });

                //完成按钮
                imgBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPopupWindow.dismiss();
                        textView.setText(categoryName);
                    }
                });
                break;

        }
    }


    public static List<Tag> loadTagFromDatabase(SQLiteDatabase db) {
        if (db == null) {
            return Collections.emptyList();
        }
        List<Tag> result = new LinkedList<>();
        Cursor cursor = null;
        try {
            cursor = db.query(TagContract.Tag.TABLE_NAME, null,
                    null, null,
                    null, null,
                    TagContract.Tag.COLUMN_TAGNAME + " DESC");

            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndex(TagContract.Tag._ID));
                String tagName = cursor.getString(cursor.getColumnIndex(TagContract.Tag.COLUMN_TAGNAME));


                Tag tag = new Tag(id);
                tag.setTagName(tagName);
                result.add(tag);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    public static List<Tag> queryBlurTagFromDatabase(SQLiteDatabase db,Editable s) {
        if (db == null) {
            return Collections.emptyList();
        }
        List<Tag> result = new LinkedList<>();
        Cursor cursor = null;
        try {
            cursor = db_tag.query(TagContract.Tag.TABLE_NAME, new String[]{TagContract.Tag.COLUMN_TAGNAME,TagContract.Tag._ID},
                    TagContract.Tag.COLUMN_TAGNAME+" like '%"+s+"%'",null,
                    null,null,
                    null);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndex(TagContract.Tag._ID));
                String tagName = cursor.getString(cursor.getColumnIndex(TagContract.Tag.COLUMN_TAGNAME));

                Tag tag = new Tag(id);
                tag.setTagName(tagName);
                result.add(tag);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    public static List<Tag> queryTagFromDatabase(SQLiteDatabase db, Editable s) {
        if (db == null) {
            return Collections.emptyList();
        }
        List<Tag> result = new LinkedList<>();
        Cursor cursor = null;
        try {
            String text = s.toString();
            cursor = db.query(TagContract.Tag.TABLE_NAME, null,
                    TagContract.Tag.COLUMN_TAGNAME+"=?",new String[]{text},null,
                    null,null,
                    null);
            if(cursor.getCount()==0){
                return null;
            }
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndex(TagContract.Tag._ID));
                String tagName = cursor.getString(cursor.getColumnIndex(TagContract.Tag.COLUMN_TAGNAME));

                Tag tag = new Tag(id);
                tag.setTagName(tagName);
                result.add(tag);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    @Override
    public void onSelect(List<Calendar> calendars) {
        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
        Stream.of(calendars).forEach(calendar -> {
            strTime =sdf.format(calendar.getTime());
            if(flag_show){
                tv_show.setText(strTime);
                flag_show = false;
            }
            if(flag_dead){
                tv_deadline.setText(strTime);
                flag_dead = false;
            }
            if(flag_sch){
                tv_scheduled.setText(strTime);
                flag_sch = false;
            }

        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK )
        {
            if(cursorPosition==-2)
            {
                showDialog(DIALOG_CANCEL);
            }

            return false;
        }
        return false;

    }

}
