package com.example.mytodolist;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mytodolist.fragment.FilesFragment;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class ShowFileActivity extends AppCompatActivity {

    private TextView tv;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showfiles);
        Intent intent = getIntent();
        int pos = intent.getIntExtra("Position", -1);

        tv=findViewById(R.id.tv_files);
        String filename = MainActivity.getFilesAdapter().getFiles().get(pos);

        BufferedReader bufferedReader=null;
        try {
            FileInputStream save = openFileInput(filename);
            bufferedReader = new BufferedReader(new InputStreamReader(save));
            StringBuffer sb = new StringBuffer();
            String line = "";
            int i=0;
            while ((line = bufferedReader.readLine()) != null) {
                if(i%6<2) sb.append("<big>"+"<font color='#559922'>"+line.replace("<","&lt;" +
                        "").replace(">","&gt")+"</font>"+"</big>"+"\n");
                else sb.append("<small>"+line.replace("<","&lt;").replace(">","&gt")+"</small>"+"\n");
                i++;
            }
            tv.setText(Html.fromHtml(sb.toString().replace("\n","<br />")));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "未找到保存的文件", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
