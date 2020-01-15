package com.example.mytodolist.ui;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.applandeo.materialcalendarview.EventDay;
import com.example.mytodolist.MainActivity;
import com.example.mytodolist.R;
import com.example.mytodolist.beans.Note;
import com.example.mytodolist.db.TodoDbHelper;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class NoteListAdapter extends RecyclerView.Adapter<NoteViewHolder> {

    private Context context;
    private final List<Note> notes = new ArrayList<>();
    private final List<EventDay> event =new ArrayList<>();
    private List<Note> dataNotes = new ArrayList<>();
    private String filepath;
    private TodoDbHelper dbHelper;
    private static SQLiteDatabase database;

    public NoteListAdapter(Context context) {
        this.context=context;
    }

    public void refresh(List<Note> newNotes) {
        notes.clear();
        if (newNotes != null) {
            notes.addAll(newNotes);
        }
        notifyDataSetChanged();

        //存储文件
        dbHelper = new TodoDbHelper(context);
        database = dbHelper.getWritableDatabase();
        dataNotes= MainActivity.loadNotesFromDatabase(database);
        BufferedWriter osw = null;
        File directory = context.getFilesDir();
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }
        for(int i = 0; i<dataNotes.size(); i++){
            try {
                filepath=dataNotes.get(i).getfiles();
                if(filepath==null) filepath="inbox";
                FileOutputStream fos = context.openFileOutput(filepath, Context.MODE_APPEND);//openFileOutput函数会自动创建文件
                osw = new BufferedWriter(new OutputStreamWriter(fos));
                for(int k=0;k<dataNotes.get(i).getCountstar();k++) {
                    osw.write("*");
                }
                String w;
                if((w=dataNotes.get(i).getState().intToString(dataNotes.get(i).getState().intValue))!="NONE")
                    osw.write(" "+w);
                if((w=dataNotes.get(i).getPriority().intToString(dataNotes.get(i).getPriority().intValue))!="NONE")
                    osw.write(" ["+w+"]");
                osw.write(" "+dataNotes.get(i).getheadline()+"\n");
                osw.write(":"+dataNotes.get(i).getTag().split(" ")[0]+":\n");
                osw.write("DEADLINE:"+"<"+dataNotes.get(i).getdeadline()+">\n");
                osw.write("SCHEDULE:"+"<"+dataNotes.get(i).getschedule()+">\n");
                osw.write("<"+dataNotes.get(i).getshow()+" +"+dataNotes.get(i).getrepeat_show_num()+dataNotes.get(i).getrepeat_show()+">\n");
                osw.write(dataNotes.get(i).getContent()+"\n");
                osw.flush();
                fos.flush();  //输出缓冲区中所有的内容
                osw.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    if (osw!=null) {
                        osw.close();
                    }
                }catch (IOException e) {
                    e.printStackTrace();

                }
            }
        }
    }

    @NonNull
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int pos) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_todo, parent, false);
        return new NoteViewHolder(itemView, event);

    }

    @NonNull

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int pos) {
        holder.bind(notes.get(pos));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

}
