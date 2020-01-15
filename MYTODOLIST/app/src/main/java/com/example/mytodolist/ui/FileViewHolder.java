package com.example.mytodolist.ui;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytodolist.R;

public class FileViewHolder extends RecyclerView.ViewHolder {
    private TextView filename;
    public FileViewHolder(@NonNull View itemView) {
        super(itemView);
        filename=(TextView)itemView.findViewById(R.id.filename);
    }

    public void bind(String s) {
        filename.setText(s);
    }
}
