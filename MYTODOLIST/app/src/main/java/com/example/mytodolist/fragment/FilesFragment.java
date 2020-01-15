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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mytodolist.AddTodoActivity;
import com.example.mytodolist.MainActivity;
import com.example.mytodolist.R;
import com.example.mytodolist.db.TodoContract;
import com.example.mytodolist.db.TodoDbHelper;
import com.example.mytodolist.fileActivity;
import com.example.mytodolist.ui.FilesListAdapter;
import com.example.mytodolist.ui.RecyclerItemClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class FilesFragment extends Fragment {
    FloatingActionButton mfab;
    private RecyclerView recyclerView;
    private static FilesListAdapter filesAdapter;
    private TodoDbHelper dbHelper;
    private static SQLiteDatabase database;
    public static Boolean IsFromFiles=true;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =View.inflate(getContext(), R.layout.fragment_files,null);

        dbHelper = new TodoDbHelper(getContext());
        database = dbHelper.getWritableDatabase();

        recyclerView = (RecyclerView) view.findViewById(R.id.list_files);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), recyclerView.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));

        filesAdapter =MainActivity.getFilesAdapter();
        filesAdapter.refresh(loadFilesFromDatabase(database));
        filesAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(filesAdapter);
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
            }

            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), fileActivity.class);
                intent.putExtra("Position",position);
                startActivity(intent);
            }
        }));

        //fab按钮设置监听事件
        mfab = (FloatingActionButton)view.findViewById(R.id.fab);
        mfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FilesFragment.this.getActivity(), AddTodoActivity.class);
                intent.putExtra("isfromfiles",IsFromFiles);
                startActivity(intent);
            }
        });
        return view;
    }


    //从数据库导入文件
    public static List<String> loadFilesFromDatabase(SQLiteDatabase db){
        if (db == null) {
            return Collections.emptyList();
        }
        List<String> result = new LinkedList<>();
        Cursor cursor = null;
        try {
            cursor = db.query(true,TodoContract.TodoNote.TABLE_NAME, new String[]{"_id","files"},
                    null, null,
                    "files", null,
                    TodoContract.TodoNote.COLUMN_PRIORITY + " DESC",null);
            while (cursor.moveToNext()) {
                String files = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_FILES));
                result.add(files);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }
}
