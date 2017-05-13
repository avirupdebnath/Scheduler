package com.avirupdebnath.scheduler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.Calendar;

public class CoverPageActivity extends Activity {
    Context context;
    Button add;
    Button view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cover_page_activity);
        this.context = this;

        TextView appName=(TextView)findViewById(R.id.appname);
        appName.bringToFront();
        FrameLayout frameLayout=(FrameLayout)findViewById(R.id.frame);
        frameLayout.getBackground().setAlpha(128);

        Calendar calendar=Calendar.getInstance();
        int x=calendar.get(Calendar.DAY_OF_WEEK);
        int y=0;
        if(x==2)y=0;
        else if(x==3)y=1;
        else if(x==4)y=2;
        else if(x==5)y=3;
        else if(x==6)y=4;
        else if(x==7)y=5;
        else if(x==1)y=6;

        //add button
        add=(Button)findViewById(R.id.add1);
        assert add != null;
        add.getBackground().setAlpha(190);

        final int finalY = y;
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,AddClassActivity.class);
                intent.putExtra("day", finalY);
                startActivity(intent);
            }
        });
        view=(Button)findViewById(R.id.view1);
        view.getBackground().setAlpha(190);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,ViewDatabaseActivity.class);
                intent.putExtra("day", finalY);
                startActivity(intent);
            }
        });
    }
}
