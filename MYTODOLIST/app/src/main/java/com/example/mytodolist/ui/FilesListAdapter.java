package com.example.mytodolist.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mytodolist.R;

import java.util.ArrayList;
import java.util.List;

public class FilesListAdapter extends RecyclerView.Adapter<FileViewHolder> {
    private Context context;
    private final List<String> files = new ArrayList<>();

    public FilesListAdapter(Context context) {
        this.context=context;
    }

    public List<String> getFiles(){
        return files;
    }

    public void refresh(List<String> newFiles) {
        files.clear();
        if (newFiles != null) {
            files.addAll(newFiles);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_files, parent, false);
        return new FileViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int pos) {
        holder.bind(files.get(pos));
    }

    @Override
    public int getItemCount() {
        return files.size();
    }
}
