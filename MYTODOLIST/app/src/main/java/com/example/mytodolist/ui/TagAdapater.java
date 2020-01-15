package com.example.mytodolist.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mytodolist.R;
import com.example.mytodolist.beans.Tag;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TagAdapater extends RecyclerView.Adapter<TagAdapater.TagViewHolder> {


    private Context context;
    private List<Tag> tags = new ArrayList<>();
    public static List<String> checklist;
    private HashMap<Integer,Boolean> maps=new HashMap<Integer,Boolean>();
    private String tempt1;

    public TagAdapater(Context context, List<Tag> tagList) {
        this.context = context;
        this.tags =  tagList;
        initMap();
        checklist = new ArrayList<>();
    }

    public List<String> getChecklist() {
        return checklist;
    }

        public void refresh(List<Tag> newTags) {
        tags.clear();
        if (newTags != null) {
            tags.addAll(newTags);
        }
        notifyDataSetChanged();
    }

    @NonNull
    public TagViewHolder onCreateViewHolder(ViewGroup parent, int pos) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tag, parent, false);
        TagViewHolder tagViewHolder = new TagViewHolder(itemView);
        return tagViewHolder;
    }

    public void onBindViewHolder(@NonNull final TagViewHolder holder, final int position) {
        Tag tag = tags.get(position);
        tempt1 = tag.getTagName();
        holder.cb_tag.setText(tempt1);
        holder.cb_tag.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    maps.put(position,true);
                    checklist.add(holder.cb_tag.getText().toString());
                }else{
                    maps.remove(position);
                }
            }
        });
        if (maps != null && maps.containsKey(position)) {
            holder.cb_tag.setChecked(true);
        } else {
            holder.cb_tag.setChecked(false);
        }
    }




    @Override
    public int getItemCount() {
        return tags.size();
    }


    public void initMap(){
        for (int i = 0; i < tags.size(); i++) {
            maps.put(i, false);
        }
    }

    public class TagViewHolder extends RecyclerView.ViewHolder{
        CheckBox cb_tag;
        public TagViewHolder(View view){
            super(view);
            cb_tag = (CheckBox)view.findViewById(R.id.tag_checkbox);
            //调整checkbox中图标的大小
            Drawable drawable7 = view.getResources().getDrawable(R.drawable.checkbox_tag);
            drawable7.setBounds(25, 0, 85, 60);
            cb_tag.setCompoundDrawables(drawable7, null, null, null);
        }

    }
}
