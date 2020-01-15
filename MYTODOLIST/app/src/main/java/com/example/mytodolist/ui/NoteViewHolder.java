package com.example.mytodolist.ui;
import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.applandeo.materialcalendarview.EventDay;
import com.example.mytodolist.R;
import com.example.mytodolist.beans.Note;
import com.example.mytodolist.beans.Priority;
import com.example.mytodolist.beans.State;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

class NoteViewHolder extends RecyclerView.ViewHolder
{

    private List<EventDay> mevent;
    private ImageView clock;

    private long days;

    private String tempt1;
    private String tempt2;

    private TextView tv1;
    private TextView tv2;
    private View view;
    private TextView files;

    public NoteViewHolder(View view, List<EventDay> event)
    {
        super(view);
        this.mevent = event;
        tv1 = (TextView) view.findViewById(R.id.days);
        tv2 = (TextView) view.findViewById(R.id.headline);
        clock = (ImageView)view.findViewById(R.id.clock);
        files = (TextView)view.findViewById(R.id.files);
        this.view=view;
    }

    @SuppressLint("SetTextI18n")
    public void bind( final Note note) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        tempt1 = sdf.format(calendar.getTime());
        if (note.getdeadline() != null) {
            tempt2 = note.getdeadline();
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");//输入日期的格式
        Date date1 = null;
        try {
            date1 = simpleDateFormat.parse(tempt1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date2 = null;
        try {
            date2 = simpleDateFormat.parse(tempt2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        GregorianCalendar cal1 = new GregorianCalendar();
        GregorianCalendar cal2 = new GregorianCalendar();
        cal1.setTime(date1);
        cal2.setTime(date2);
        days = (long)(cal2.getTimeInMillis()-cal1.getTimeInMillis())/(1000*3600*24);//从间隔毫秒变成间隔天数

        //根据优先级改变图片颜色
        if (note.getPriority() == Priority.A) {
            view.setBackgroundColor(view.getResources().getColor(R.color.pink));
        } else if (note.getPriority() == Priority.B) {
            view.setBackgroundColor(view.getResources().getColor(R.color.md_pink_200));
        } else if (note.getPriority() == Priority.C) {
            view.setBackgroundColor(view.getResources().getColor(R.color.md_pink_100));
        } else if (note.getPriority() == Priority.D) {
            view.setBackgroundColor(view.getResources().getColor(R.color.md_pink_50));
        }else{
            view.setBackgroundColor(view.getResources().getColor(R.color.white));
        }
        //根据是否overdue设置图标
        if (days < 0) {
            tv2.setTextColor(view.getResources().getColor(R.color.red));
            clock.setImageDrawable(view.getResources().getDrawable(R.drawable.overdue));
        } else {
            tv2.setTextColor(view.getResources().getColor(R.color.darkGray));
            clock.setImageDrawable(view.getResources().getDrawable(R.drawable.clock_gray));
        }
        tv1.setText("d." + days + "d");
        tv2.setText(note.getheadline());
        files.setText(note.getfiles());


        if (note.getState() == State.DONE) {
            clock.setImageDrawable(view.getResources().getDrawable(R.drawable.done));
        }

    }

}
