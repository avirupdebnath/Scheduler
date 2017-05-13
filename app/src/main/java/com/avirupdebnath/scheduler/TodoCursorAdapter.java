package com.avirupdebnath.scheduler;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Created by Avirup on 4/24/2017.
 */
public class TodoCursorAdapter extends CursorAdapter{
    SQLiteDatabase myDatabase;
    ListView listView;
    static int count=1;

    public TodoCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.row,parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        TodoItemDatabase handler = new TodoItemDatabase(context);
        // Get access to the underlying writeable database
        myDatabase = handler.getWritableDatabase();

        TextView classN = (TextView) view.findViewById(R.id.classN);
        TextView batchN = (TextView) view.findViewById(R.id.batchN);
        final Switch enable=(Switch)view.findViewById(R.id.switch1);
        // Extract properties from cursor"class_name","batch_name"
        String className = cursor.getString(cursor.getColumnIndexOrThrow("class_name"));
        String batchName = cursor.getString(cursor.getColumnIndexOrThrow("batch_name"));
        int en=cursor.getInt(cursor.getColumnIndexOrThrow("enable"));
        // Populate fields with extracted properties
        classN.setText(className);
        batchN.setText(batchName);
        if(en==1){
            enable.setChecked(true);
        }
        else enable.setChecked(false);
    }

}

