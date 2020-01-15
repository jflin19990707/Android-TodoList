package com.example.mytodolist;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mytodolist.beans.Note;
import com.example.mytodolist.beans.Priority;
import com.example.mytodolist.beans.State;
import com.example.mytodolist.db.TodoContract;
import com.example.mytodolist.db.TodoDbHelper;
import com.example.mytodolist.fragment.FilesFragment;
import com.example.mytodolist.ui.NoteListAdapter;
import com.example.mytodolist.ui.RecyclerItemClickListener;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class fileActivity extends AppCompatActivity {
    private TextView tv;
    private RecyclerView recyclerView;
    private NoteListAdapter itemlistAdapter;
    private TodoDbHelper dbHelper;
    private static SQLiteDatabase database;
    private Button ViewFile;
    private Button AddItem;
    private Button ViewBack;
    private List<Note> itemList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);
        Intent intent = getIntent();
        int pos = intent.getIntExtra("Position", -1);
        String filename = MainActivity.getFilesAdapter().getFiles().get(pos);

        dbHelper = new TodoDbHelper(this);
        database = dbHelper.getWritableDatabase();
        itemlistAdapter=MainActivity.getItemListAdapter();
        itemList = loaditemsFromDatabase(database,filename);
        itemlistAdapter.refresh(itemList);
        itemlistAdapter.notifyDataSetChanged();

        ViewFile = (Button) findViewById(R.id.btn_view);
        AddItem = (Button) findViewById(R.id.Add_item);
        recyclerView = (RecyclerView) findViewById(R.id.list_items);
        ViewBack = (Button)findViewById(R.id.back_view);
        tv = (TextView) findViewById(R.id.Filename);
        tv.setText(filename);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, recyclerView.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(itemlistAdapter);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int childAdapterPosition = parent.getChildAdapterPosition(view);
                outRect.set(50*itemList.get(childAdapterPosition).getCountstar()-40, 10, 10, 10);
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(fileActivity.this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                SubItem(position,true);
            }

            @Override
            public void onItemClick(View view, int position) {
                SubItem(position,false);
            }
        }));

        AddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(fileActivity.this, AddTodoActivity.class);
                intent.putExtra("filename",filename);
                intent.putExtra("FromFileActivity",true);
                startActivity(intent);
            }
        });

        ViewFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(fileActivity.this, ShowFileActivity.class);
                intent.putExtra("Position",pos);
                startActivity(intent);
            }
        });
        ViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



    public void SubItem(int position,Boolean op){
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                Note temptNote, temptparent;
                int childAdapterPosition = parent.getChildAdapterPosition(view);
                temptNote = itemList.get(childAdapterPosition);
                if (childAdapterPosition != 0) {
                    temptparent = itemList.get(childAdapterPosition - 1);
                    if (childAdapterPosition == position ) {
                        if(op && temptNote.getCountstar()>1) {
                            outRect.set(-50, 0, 0, 0);
                            temptNote.setCountstar(temptNote.getCountstar() - 1);
                        }
                        else if(!op && temptNote.getCountstar() < temptparent.getCountstar() + 1){
                            outRect.set(50, 0, 0, 0);
                            temptNote.setCountstar(temptNote.getCountstar() + 1);
                        }
                        ContentValues values = new ContentValues();
                        values.put(TodoContract.TodoNote.COLUMN_COUNTSTAR, temptNote.getCountstar());

                        values.put(TodoContract.TodoNote.COLUMN_CONTENT, temptNote.getContent());
                        database.update(TodoContract.TodoNote.TABLE_NAME, values, "_id=?", new String[]{String.valueOf(temptNote.id)});

                        MainActivity.getTodoAdapter().refresh(MainActivity.loadNotesFromDatabase(database));
                        MainActivity.getTodoAdapter().notifyDataSetChanged();
                    }
                }
            }
        });
    }

    public static List<Note> loaditemsFromDatabase(SQLiteDatabase db,String filename) {
        if (db == null) {
            return Collections.emptyList();
        }
        List<Note> result = new LinkedList<>();
        Cursor cursor = null;
        try {
            cursor = database.query(TodoContract.TodoNote.TABLE_NAME, null,
                    TodoContract.TodoNote.COLUMN_FILES+"=?",new String[]{filename},
                    null,null,
                    null);
            if(cursor.getCount()==0){
                return null;
            }
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndex(TodoContract.TodoNote._ID));
                int countStar = cursor.getInt(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_COUNTSTAR));
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
                note.setCountstar(countStar);
                note.setfiles(files);
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

}
