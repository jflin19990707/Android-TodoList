package com.example.mytodolist.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mytodolist.AddTodoActivity;
import com.example.mytodolist.MainActivity;
import com.example.mytodolist.R;
import com.example.mytodolist.beans.Note;
import com.example.mytodolist.beans.Priority;
import com.example.mytodolist.beans.State;
import com.example.mytodolist.db.TodoContract;
import com.example.mytodolist.db.TodoDbHelper;
import com.example.mytodolist.ui.NoteListAdapter;
import com.example.mytodolist.ui.RecyclerItemClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TodoFragment extends Fragment {

    private Context context;

    private NoteListAdapter todoAdapter;
    private RecyclerView recyclerView;
    private RadioGroup mRgGroup;
    private RadioButton radioButton_all;
    private RadioButton radioButton_filter;
    private EditText et_search;
    private FloatingActionButton mfab;
    private Boolean fromToDo = false;
    public String headLine = "";

    private TodoDbHelper dbHelper;
    private static SQLiteDatabase database;
    private final static int DIALOG_FILTER = 0;

    public final static  int TODO = 0;
    public final static  int DONE = 1;
    public final static  int OVERDUE = 2;
    public final static  int PRIORITY = 3;
    public final static  int NEXT_7_DAYS = 4;

    public Boolean FILTER_TODO = false;
    public Boolean FILTER_DONE = false;
    public Boolean FILTER_PRIORITY = false;
    public Boolean FILTER_OVERDUE = false;
    public Boolean FILTER_NEXT7DAY = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.fragment_todo, null);
        recyclerView = (RecyclerView) view.findViewById(R.id.list_todo);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), recyclerView.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));

        dbHelper = new TodoDbHelper(getContext());
        database = dbHelper.getWritableDatabase();

        todoAdapter=MainActivity.getTodoAdapter();
        todoAdapter.refresh(MainActivity.loadNotesFromDatabase(database));
        todoAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(todoAdapter);

        mRgGroup = (RadioGroup) view.findViewById(R.id.rg_group);
        radioButton_all = (RadioButton) view.findViewById(R.id.rb_showAll);
        radioButton_filter = (RadioButton) view.findViewById(R.id.rb_filter);
        mfab = (FloatingActionButton) view.findViewById(R.id.fab);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

            }

            @Override
            public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {

            }

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(10, 10, 10, 10);
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                MainActivity.showDelete(position,getContext(),database);
            }

            @Override
            public void onItemClick(View view, int position) {
                fromToDo = true;
                Intent intent = new Intent(getActivity(),AddTodoActivity.class);
                intent.putExtra("cursorPosition",position);
                intent.putExtra("FromToDo",fromToDo);
                intent.putExtra("HeadLine",headLine);
                intent.putExtra("filter_todo",FILTER_TODO);
                intent.putExtra("filter_done",FILTER_DONE);
                intent.putExtra("filter_priority",FILTER_PRIORITY);
                intent.putExtra("filter_overdue",FILTER_OVERDUE);
                intent.putExtra("filter_next7day",FILTER_NEXT7DAY);
                startActivity(intent);
            }
        }));

        mRgGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_filter:  //过滤器
                        onCreateDialog(DIALOG_FILTER).show();
                        break;
                    case R.id.rb_showAll:    //显示全部
                        FILTER_DONE = false;
                        FILTER_NEXT7DAY =false;
                        FILTER_OVERDUE =false;
                        FILTER_PRIORITY = false;
                        FILTER_TODO = false;
                        todoAdapter.refresh(queryNotesFromDatabase(database,null,null));
                        break;
                }
            }
        });

        //调整搜索图表的大小
        et_search = (EditText) view.findViewById(R.id.et_search);
        Drawable drawable4 =getResources().getDrawable(R.drawable.search_icon);
        drawable4.setBounds(15,0,65,50);
        et_search.setCompoundDrawables(drawable4,null,null,null);

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                headLine = s.toString();
                todoAdapter.refresh(queryBlurNotesFromDatabase(database,s));
            }
        });

        mfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TodoFragment.this.getActivity(), AddTodoActivity.class);
                startActivity(intent);
            }
        });


        return view;
    }


    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch (id) {
            case DIALOG_FILTER: //状态选择单选对话框
                AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
                builder.setTitle("选择过滤");
                builder.setSingleChoiceItems(R.array.filter_choose, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FILTER_DONE = false;
                        FILTER_NEXT7DAY =false;
                        FILTER_OVERDUE =false;
                        FILTER_PRIORITY = false;
                        FILTER_TODO = false;
                        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");

                            String state = getResources().getStringArray(R.array.filter_choose)[i];
                            radioButton_filter.setText(state);
                            switch (i){
                                case PRIORITY:
                                    todoAdapter.refresh(queryNotesFromDatabase(database, TodoContract.TodoNote.COLUMN_PRIORITY+"<>?",new String[]{String.valueOf(Priority.NONE.intValue)}));
                                    FILTER_PRIORITY = true;
                                    break;
                                case TODO:
                                    todoAdapter.refresh(queryNotesFromDatabase(database, TodoContract.TodoNote.COLUMN_STATE+"=?",new String[]{String.valueOf(State.TODO.intValue)}));
                                    FILTER_TODO = true;
                                    break;
                                case DONE:
                                    todoAdapter.refresh(queryNotesFromDatabase(database, TodoContract.TodoNote.COLUMN_STATE+"=?",new String[]{String.valueOf(State.DONE.intValue)}));
                                    FILTER_DONE = true;
                                    break;
                                case OVERDUE:
                                    Calendar calendar = Calendar.getInstance();
                                    String today = sdf.format(calendar.getTime());
                                    todoAdapter.refresh(queryNotesFromDatabase(database, TodoContract.TodoNote.COLUMN_DEADLINE+"<?",new String[]{today}));
                                    FILTER_OVERDUE = true;
                                    break;
                                case NEXT_7_DAYS:
                                    Calendar now = Calendar.getInstance();
                                    Calendar next7Day = Calendar.getInstance();
                                    next7Day.add(Calendar.DAY_OF_YEAR,7);
                                    String rightNow = sdf.format(now.getTime());
                                    String next7day = sdf.format(next7Day.getTime());
                                    todoAdapter.refresh(queryNotesFromDatabase(database, TodoContract.TodoNote.COLUMN_DEADLINE+"<=?"+" and "+TodoContract.TodoNote.COLUMN_DEADLINE+">=?",new String[]{next7day,rightNow}));
                                    FILTER_NEXT7DAY = true;
                                    break;
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
        return dialog;
    }
    //从数据库中模糊查询Note
    public static List<Note> queryBlurNotesFromDatabase(SQLiteDatabase db, Editable s) {
        if (db == null) {
            return Collections.emptyList();
        }
        List<Note> result = new LinkedList<>();
        Cursor cursor = null;
        try {
            String text = s.toString();
            cursor = database.query(TodoContract.TodoNote.TABLE_NAME, null,
                    TodoContract.TodoNote.COLUMN_HEADLINE+" like '%"+text+"%'",null,
                    null,null,
                    null);
            if(cursor.getCount()==0){
                return null;
            }
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndex(TodoContract.TodoNote._ID));
                String headline = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_HEADLINE));
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
                note.setheadline(headline);
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

    public static List<Note> queryNotesFromDatabase(SQLiteDatabase db, String s,String[] where) {
        if (db == null) {
            return Collections.emptyList();
        }
        List<Note> result = new LinkedList<>();
        Cursor cursor = null;
        try {

            cursor = database.query(TodoContract.TodoNote.TABLE_NAME, null,
                    s,where,
                    null,null,
                    null);
            if(cursor.getCount()==0){
                return null;
            }
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndex(TodoContract.TodoNote._ID));
                String headline = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_HEADLINE));
                int state = cursor.getInt(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_STATE));
                int intPriority = cursor.getInt(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_PRIORITY));

                String schedule = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_SCHEDULE));
                String deadline = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_DEADLINE));
                String show = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_SHOW));
                String tag = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_TAG));
                String files = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_FILES));

                String repeat_show = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_REPEAT_SHOW));
                int repeat_show_num = cursor.getInt(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_REPEAT_SHOW_NUM));
                String content = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_CONTENT));

                Note note = new Note(id);
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


}
