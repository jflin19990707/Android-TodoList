package com.example.mytodolist.fragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class AgendaFragment extends Fragment {
    private static NoteListAdapter agendaAdaapter;

    CalendarView calendarView;
    private FloatingActionButton mfab;
    private static SQLiteDatabase database;
    private static String scheduledTime = "";
    private static Boolean fromAgenda = true;
    SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view =View.inflate(getContext(), R.layout.fragment_agenda,null);
        TodoDbHelper dbHelper = new TodoDbHelper(getContext());
        database = dbHelper.getWritableDatabase();
        //选择日期显示对应的todo
        final List<EventDay> event = new ArrayList<>();

        HashMap<Calendar,Integer> calendarmap= new HashMap<>();
        icon_show(calendarmap,event,view);

        agendaAdaapter = MainActivity.getAgendaAdapter();

        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                //将calendar转换成String，方便处理记录
                scheduledTime = sdf.format(eventDay.getCalendar().getTime());
                agendaAdaapter.refresh(loadSeletedNotesFromDatabase(database));
                agendaAdaapter.notifyDataSetChanged();
                icon_show(calendarmap,event,view);
            }
        });

        agendaAdaapter.refresh(loadSeletedNotesFromDatabase(database));
        agendaAdaapter.notifyDataSetChanged();

        //fab按钮设置监听事件
        mfab = (FloatingActionButton)view.findViewById(R.id.fab);
        mfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AgendaFragment.this.getActivity(), AddTodoActivity.class);
                intent.putExtra("scheduledTime",scheduledTime);
                intent.putExtra("FromAgenda",fromAgenda);
                startActivity(intent);
            }
        });


        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.list_todo);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mRecyclerView.setAdapter(agendaAdaapter);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
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



        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemLongClick(View view, int position) {

            }

            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(),AddTodoActivity.class);
                intent.putExtra("cursorPosition",position);
                intent.putExtra("scheduledTime",scheduledTime);
                intent.putExtra("FromAgenda",fromAgenda);
                startActivity(intent);
            }
        }));

        onAttach(getActivity());
        return view;
    }



    public static List<Note> loadSeletedNotesFromDatabase(SQLiteDatabase db) {
        if (db == null) {
            return Collections.emptyList();
        }
        List<Note> result = new LinkedList<>();
        Cursor cursor = null;
        try {
            cursor = db.query(TodoContract.TodoNote.TABLE_NAME, null,
                    TodoContract.TodoNote.COLUMN_DEADLINE+"=?", new String[]{scheduledTime},null,
                    null,
                    null);

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

    public void icon_show(Map<Calendar,Integer> calendarIntegerMap, List<EventDay> event,View view){
        calendarView = (CalendarView)view.findViewById(R.id.calendarView);
        List <Note> note=MainActivity.loadNotesFromDatabase(database);
        for(int i =0;i<note.size();i++){
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            String str= note.get(i).getdeadline();
            int count = queryDeadlineFromDatabase(database,str);
            Date date = null;
            try {
                date = sdf.parse(str);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            calendar.setTime(date);
            calendarIntegerMap.put(calendar,count);
        }
        for(Map.Entry<Calendar,Integer> entry:calendarIntegerMap.entrySet()){
            if(entry.getValue()==0){
                event.add(new EventDay(entry.getKey(),null));
            }else if(entry.getValue()==1){
                event.add(new EventDay(entry.getKey(),R.drawable.sample_one_icon));
            }else if(entry.getValue()==2){
                event.add(new EventDay(entry.getKey(),R.drawable.sample_two_icons));
            }else{
                event.add(new EventDay(entry.getKey(),R.drawable.sample_three_icons));
            }

        }
        calendarView.setEvents(event);

    }

    public int queryDeadlineFromDatabase(SQLiteDatabase db,String ddl) {
        int count;
        if (db == null) {
            return 0;
        }
        Cursor cursor = null;
        try {
            cursor = db.query(TodoContract.TodoNote.TABLE_NAME, null,
                    TodoContract.TodoNote.COLUMN_DEADLINE+"=?", new String[]{ddl},null,
                    null,
                    null);
            count =cursor.getCount();

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count;
    }

}

