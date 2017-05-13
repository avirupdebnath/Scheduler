package com.avirupdebnath.scheduler;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class ViewDatabaseActivity extends Activity implements AdapterView.OnItemSelectedListener{
    ListView listView;
    Spinner day;
    Context context;
    SQLiteDatabase myDatabase;
    Cursor cursor;
    TodoCursorAdapter adapter;
    Button back;
    Switch enables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_existing_classes);
        this.context = this;

        //Spinner element
        day=(Spinner)findViewById(R.id.spinner2);

        //Spinner click listener
        if (day != null) {
            day.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) context);
        }

        //spinner drop elements
        List<String> daysOfWeek=new ArrayList<String>();
        daysOfWeek.add("Monday");
        daysOfWeek.add("Tuesday");
        daysOfWeek.add("Wednesday");
        daysOfWeek.add("Thursday");
        daysOfWeek.add("Friday");
        daysOfWeek.add("Saturday");
        daysOfWeek.add("Sunday");

        //creating adapter for spinner
        ArrayAdapter<String> dataAdapter=new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,daysOfWeek);

        //drop down layout style
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Attaching adapter to spinner
        day.setAdapter(dataAdapter);

        Intent getIntent=getIntent();
        int x=getIntent.getIntExtra("day",0);
        day.setSelection(x);

        //instantiating the list view object
        listView = (ListView) findViewById(R.id.listView2);



        //open or create database for schedules to be stored
        //myDatabase = openOrCreateDatabase("Schedules", MODE_PRIVATE, null);


        //creating a table in the database if not already existing
       // myDatabase.execSQL("CREATE TABLE IF NOT EXISTS Demo(_id INTEGER PRIMARY KEY AUTOINCREMENT,day VARCHAR,class_name VARCHAR,batch_name VARCHAR,location VARCHAR, start_time VARCHAR, end_time VARCHAR,enable INT);");

        // TodoDatabaseHandler is a SQLiteOpenHelper class connecting to SQLite
        TodoItemDatabase handler = new TodoItemDatabase(this);

        // Get access to the underlying writeable database
        myDatabase = handler.getWritableDatabase();

        //fetching data from the dataset in the database to display in the list view

        String[] FROM={"_id","day","class_name","batch_name","location","start_time","end_time","enable"};
        cursor=myDatabase.query("Demo",FROM,"day="+day.getSelectedItemPosition(),null,null,null,null);

        enables=(Switch)findViewById(R.id.enable_disable2);
        enables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                if (enables != null && !enables.isChecked()) {
                    values.put("enable", 0);
                    Toast.makeText(context,"Yohoo you have a day off!",Toast.LENGTH_SHORT).show();
                }
                else{
                    values.put("enable", 1);
                    Toast.makeText(context,"You are all set! ",Toast.LENGTH_SHORT).show();
                }
                myDatabase.update("Demo", values, "day=" + day.getSelectedItemPosition(), null);
                String[] FROM={"_id","day","class_name","batch_name","location","start_time","end_time","enable"};
                Cursor cursorTemp=myDatabase.query("Demo",FROM,"day="+day.getSelectedItemPosition(),null,null,null,null);
                adapter.changeCursor(cursorTemp);
                Intent changeAlarmStatus=new Intent(context,AlarmResetOnBoot.class);
                changeAlarmStatus.putExtra("ref",0);
                changeAlarmStatus.putExtra("day",day.getSelectedItemPosition());
                context.startService(changeAlarmStatus);
            }
        });


        adapter = new TodoCursorAdapter(this, cursor);
        listView.setAdapter(adapter);

        //setting up an itemclick listener for the listview object
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor mycursor=(Cursor)listView.getItemAtPosition(position);
                int rowid=mycursor.getInt(mycursor.getColumnIndexOrThrow("_id"));
                Intent myIntent=new Intent(context,EditDatabaseActivity.class);
                myIntent.putExtra("rowid",rowid);
                finish();
                startActivity(myIntent);
            }
        });

        back=(Button)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(parent.getContext(),"SELECTED:"+day.getSelectedItemPosition(),Toast.LENGTH_SHORT).show();
        String[] FROM={"_id","day","class_name","batch_name","location","start_time","end_time","enable"};
        Cursor cursorTemp=myDatabase.query("Demo",FROM,"day="+day.getSelectedItemPosition(),null,null,null,null);
        adapter.changeCursor(cursorTemp);
        enables.setChecked(true);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //DO Nothing
    }

}
