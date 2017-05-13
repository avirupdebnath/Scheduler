package com.avirupdebnath.scheduler;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddClassActivity extends Activity implements AdapterView.OnItemSelectedListener{
    AlarmManager alarmManager;
    Button addClass;
    Button view;
    Button cancel;
    TextView start;
    TextView stop;
    EditText classN;
    EditText batchN;
    EditText loc;
    Context context;
    SQLiteDatabase myDatabase;
    int dayOfweek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);
        this.context = this;

        classN=(EditText)findViewById(R.id.className5);
        batchN=(EditText)findViewById(R.id.batchName5);
        loc=(EditText)findViewById(R.id.location5);
        //Spinner element
        final Spinner day=(Spinner)findViewById(R.id.spinner);

        //Spinner click listener
        if (day != null) {
            day.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) context);
        }

        //spinner drop elements
        final List<String> daysOfWeek=new ArrayList<String>();
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

        // TodoDatabaseHandler is a SQLiteOpenHelper class connecting to SQLite
        TodoItemDatabase handler = new TodoItemDatabase(this);

        // Get access to the underlying writeable database
        myDatabase = handler.getWritableDatabase();

        //add onCLickListener for startTime edittext
        start=(TextView)findViewById(R.id.start_time);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerMethod(start);
            }
        });

        //stop time
        stop=(TextView)findViewById(R.id.end_time);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerMethod(stop);
            }
        });

        //add class on clicking add class button
        addClass = (Button) findViewById(R.id.add_class);
        addClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //updating the database
                updateDatabase(classN,batchN,loc,start,stop);
                //creating a cursor to get the rowid
                String[] arr={"_id"};
                Cursor cursor=myDatabase.query("Demo",arr,null,null,null,null,null,null);
                cursor.moveToLast();
                //initializing the alarm manager
                alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);

                Calendar starts=getTime(start,day);
                Calendar stops=getTime(stop,day);
                if(starts!=null&&stops!=null){
                    //setting up the intent
                    Intent intent=new Intent(context,AlarmRecieverClass.class);
                    intent.putExtra("on_off",1);
                    intent.putExtra("id",Integer.valueOf(cursor.getString(0)));

                    Intent intent2=new Intent(context,AlarmRecieverClass.class);
                    intent2.putExtra("on_off",1);
                    intent2.putExtra("id",-(Integer.valueOf(cursor.getString(0))));

                    PendingIntent myPendingIntent1=PendingIntent.getBroadcast(context,Integer.valueOf(cursor.getString(0)),intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    PendingIntent myPendingIntent2=PendingIntent.getBroadcast(context,-(Integer.valueOf(cursor.getString(0))),intent2,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, starts.getTimeInMillis()-600000, myPendingIntent1);
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, stops.getTimeInMillis()-600000, myPendingIntent2);
                    Toast.makeText(getBaseContext(),"Class Has Been Added ! ",Toast.LENGTH_SHORT).show();
                }
                Intent myIntent=getIntent();
                finish();
                startActivity(myIntent);
            }
        });

        //view the existing classes in the database
        view=(Button)findViewById(R.id.view_class);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,ViewDatabaseActivity.class);
                intent.putExtra("day",day.getSelectedItemPosition());
                startActivity(intent);
            }
        });
        //cancel the add_class activity
        cancel=(Button)findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
       dayOfweek=  parent.getSelectedItemPosition();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //DO Nothing
    }

    public void TimePickerMethod(final TextView view1){
        final Calendar calendar = Calendar.getInstance();
        int mHour = calendar.get(Calendar.HOUR_OF_DAY);
        final int mMinute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                String AM_PM = (hourOfDay >= 12&&hourOfDay<24) ? "PM" : "AM";
                int hour=(hourOfDay > 12) ? hourOfDay - 12 : hourOfDay;
                String hr=toString().valueOf(hour);
                String minutes=toString().valueOf(minute);
                if(minute<10)minutes="0"+minutes;
                if(hour<10)hr="0"+hr;
                String finalTime = (hr + " : " +
                        minutes + " " + AM_PM);
                view1.setText(finalTime);
            }
        }, mHour, mMinute, false);
        timePicker.show();
    }

    public void updateDatabase(final EditText view1,final EditText view2,final EditText view3,final TextView view4,final TextView view5){
            ContentValues values=new ContentValues();
            values.put("day",dayOfweek);
            values.put("class_name", String.valueOf(view1.getText()));
            values.put("batch_name", String.valueOf(view2.getText()));
            values.put("location", String.valueOf(view3.getText()));
            values.put("start_time", String.valueOf(view4.getText()));
            values.put("end_time", String.valueOf(view5.getText()));
            values.put("enable",1);
            long x=myDatabase.insert("Demo",null,values);
    }
    public Calendar getTime(TextView view1,Spinner days){

        //extraacting time from the values provided
        final Calendar calendar=Calendar.getInstance();
        CharSequence s1= view1.getText();
        int day=-1;
        String x= String.valueOf(days.getSelectedItem());
        if(x.equalsIgnoreCase("Monday")){
            day=2;
        }
        else if(x.equalsIgnoreCase("Tuesday")){
            day=3;
        }
        else if(x.equalsIgnoreCase("Wednesday")){
            day=4;
        }
        else if(x.equalsIgnoreCase("Thursday")){
            day=5;
        }
        else if(x.equalsIgnoreCase("Friday")){
            day=6;
        }
        else if(x.equalsIgnoreCase("Saturday")){
            day=7;
        }
        else if(x.equalsIgnoreCase("Sunday")){
            day=1;
        }
        else{}
        calendar.set(Calendar.DAY_OF_WEEK,day);
        Toast.makeText(context,String.valueOf(calendar.get(Calendar.DAY_OF_WEEK)),Toast.LENGTH_SHORT).show();
        if(s1.charAt(0)!='C') {
            int startHr = Integer.parseInt(String.valueOf(s1.subSequence(0, 2)));
            int startMin = Integer.parseInt(String.valueOf(s1.subSequence(5, 7)));
            String AM_PM = String.valueOf(s1.subSequence(8, 10));
            if (AM_PM.equalsIgnoreCase("AM")||(startHr==12&&AM_PM.equalsIgnoreCase("PM")))
                calendar.set(Calendar.HOUR_OF_DAY, startHr);
            else calendar.set(Calendar.HOUR_OF_DAY, startHr + 12);
            calendar.set(Calendar.MINUTE, startMin);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            if(calendar.before(Calendar.getInstance())){
                calendar.add(Calendar.DATE,7);
                Toast.makeText(context,"Added 7",Toast.LENGTH_SHORT).show();
            }
            return calendar;
        }
        else return null;
    }

}
