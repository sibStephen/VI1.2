package college.root.vi12.AdminActivities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

import college.root.vi12.R;

import static college.root.vi12.R.id.staff;

public class TableActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    Spinner spinner_subject,spinner_staff;
    String[] days = {"Monday" , "Tuesday" , "Wednesday", "Thursday", "Friday", "Saturday"};
    String[] time = {"8.00", "9.00","10.15", "11.15",  "13.15", "14.15", "15.30", "16.30", "17.45" };
    TTHelper[] ttHelpers;
    static int numberOfObjects = 54;
    static String faculty = "";
    final String subject[]= new String[10];
    static  ArrayAdapter<String> adapter_location;
    static String[] temp_room;
    Button btnSave;
    String TAG = "Test";
    JSONArray mon , tue, wed , thrus, fri ,sat;
    JSONObject finalObject;
    ArrayAdapter<String> adapter_subject;
    String selection[] = {"","","","","",""};
    ArrayAdapter<String> adapter_staff;
    JSONArray array1;
    JSONObject roomObject,subjectObject, staffObject, ttObject;
    static String subject_selected = " ";
    String[] staff = new String[5];
    static int timeSlots = 9;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table_setup);
        //***********importing data from intent
        Bundle extras = getIntent().getExtras();

        TextView tv_branch = (TextView)findViewById(R.id.tv_branch_id);
        TextView tv_year = (TextView)findViewById(R.id.tv_year_id);
        TextView tv_sem = (TextView)findViewById(R.id.tv_sem_id);
        TextView tv_division = (TextView)findViewById(R.id.tv_division_id);

        tv_branch.setText(extras.getString("Branch"));
        tv_year.setText(extras.getString("Year"));
        tv_sem.setText(extras.getString("Sem"));
        tv_division.setText(extras.getString("Division"));


        selection[0]=extras.getString("Branch");
        selection[1]=extras.getString("Year");
        selection[2]=extras.getString("Sem");
        selection[3]=extras.getString("Division");

        //***********importing data from server of subjects and rooms
        try {




            subjectObject =  new JSONObject();
            roomObject = new JSONObject();
            String str = getIntent().getStringExtra("StaffObject");
            staffObject = new JSONObject(str);
            Log.d(TAG, "onCreate: staffobject is "+staffObject);


            str = getIntent().getStringExtra("SubjectObject");
            Log.d(TAG, "str" + str);

            subjectObject = new JSONObject(str);
            String str1 = getIntent().getStringExtra("RoomObject");
            Log.d(TAG, "str" + str);
            roomObject = new JSONObject(str1);

            array1 =  roomObject.getJSONArray(selection[0]);
            temp_room = new String [array1.length()];
            for (int j=0; j< array1.length(); j++){

                temp_room[j] = array1.getString(j);
            }
            Log.d(TAG, "onCreate: array is "+array1);

            Iterator<?> keys = subjectObject.keys();
            Log.d(TAG, "run: kes are "+keys);

            int i=0;
            while( keys.hasNext() ) {
                String key = (String) keys.next();
                Log.d(TAG, "run: key is " + key);
                Log.d(TAG, "run: and value of key is " + subjectObject.getString(key));

                if (key.equals("_id") || key.equals("SubjectCount")) {
                    // do not store it in name array
                } else {

                    subject[i] = key;
                    i++;



                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //****************setting spinners data
        Log.d(TAG, "onCreate: ");
        spinner_staff = (Spinner)findViewById(R.id.spinner_staff_id);
        spinner_subject = (Spinner)findViewById(R.id.spinner_subject_id);

        adapter_staff = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,staff);
        adapter_staff.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_staff.setAdapter(adapter_staff);
        spinner_staff.setOnItemSelectedListener(this);

        adapter_subject = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,subject);
        adapter_subject.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_subject.setAdapter(adapter_subject);
        spinner_subject.setOnItemSelectedListener(this);
        Log.d(TAG, "onCreate: adapters set");

        //***************original code for this activity

        ttHelpers = new TTHelper[numberOfObjects];
        btnSave = (Button) findViewById(R.id.save_button);

        recyclerView = (RecyclerView) findViewById(R.id.adminTTrecycler);
        recyclerView.setHasFixedSize(false);
        mLayoutManager = new GridLayoutManager(this,timeSlots,GridLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(mLayoutManager);
        adapter_location = new ArrayAdapter<String>(TableActivity.this,android.R.layout.simple_spinner_dropdown_item, temp_room);
        adapter_location.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ttObject = new JSONObject();
        String tt = getIntent().getStringExtra("ttObject");
        if (tt != null){
            try {
                ttObject = new JSONObject(tt);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (ttObject != null){

                try {
                    loadPreviousEntryOfTT(ttObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }else {

            RecyclerTimetableAdapter adapter = new RecyclerTimetableAdapter(this, days , time , ttHelpers, timeSlots);
            recyclerView.setAdapter(adapter);


        }







        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(TableActivity.this);
                builder.setTitle("Save TimeTable?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        initializeJson();
                        for (int i =0 ; i<numberOfObjects; i++ ){
                            if (ttHelpers[i] != null){
                                JSONObject object = new JSONObject();
                                try {
                                    object.put("Staff", ttHelpers[i].getFaculty());
                                    object.put("Subject" , ttHelpers[i].getSubject());
                                    object.put("Time" , ttHelpers[i].getTime());
                                    object.put("Location" , ttHelpers[i].getLocation());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                                String day = ttHelpers[i].getDay();
                                Log.d(TAG, "onClick: day is"+day);

                                switch (day){

                                    case "Monday": mon.put(object);
                                        break;

                                    case "Tuesday": tue.put(object);
                                        break;
                                    case "Wednesday": wed.put(object);
                                        break;
                                    case "Thursday": thrus.put(object);
                                        break;
                                    case "Friday": fri.put(object);
                                        break;
                                    case "Saturday": sat.put(object);
                                        break;
                                }




                            }

                        }

                        try {
                            finalObject.put("Monday", mon);
                            finalObject.put("Tuesday", tue);
                            finalObject.put("Wednesday", wed);
                            finalObject.put("Thursday", thrus);
                            finalObject.put("Friday", fri);
                            finalObject.put("Saturday", sat);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        Log.d(TAG, "onClick: the object is "+finalObject);


                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });

                builder.show();



            }
        }
        );






    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menutimetable, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.ttreset :
                for (int i=0; i<numberOfObjects ;i++){
                    ttHelpers[i] = null;
                }
                RecyclerTimetableAdapter adapter = new RecyclerTimetableAdapter(this, days , time , ttHelpers, timeSlots);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                break;

        }

            return super.onOptionsItemSelected(item);

    }

        @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        subject_selected = spinner_subject.getSelectedItem().toString();

        try {
            JSONArray array = staffObject.getJSONArray("object");

            int k=0;
            for (int i=0 ; i <array.length() ; i++){
                JSONObject obj = array.getJSONObject(i);
                if (obj.getString("Subject").equals(subject_selected) ){
                    staff[k++] = obj.getString("Faculty");


                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        faculty = spinner_staff.getSelectedItem().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    public void push_to_server(View view) {
        try {
            Intent i1 = new Intent(TableActivity.this,TimeTableDisplayActivity.class);
            Log.d(TAG, "push_to_server: in pushToServer button onclick");

            i1.putExtra("object",finalObject.toString());
            i1.putExtra("id",selection[0]+selection[1]+selection[2]+selection[3]);
            i1.putExtra("User" , "Admin");
            startActivity(i1);

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void loadPreviousEntryOfTT(JSONObject ttObject) throws JSONException {


        for (int i=0 ; i<days.length ; i++){

            JSONArray weekArray = ttObject.getJSONArray(days[i]);
            Log.d(TAG, "loadPreviousEntryOfTT: week array is "+weekArray);
            for (int j=0 ; j<weekArray.length() ; j++){

                JSONObject dayObject =weekArray.getJSONObject(j);
                // TODO continue from here

                String timeoflec = dayObject.getString("Time");
                int index = indexOf(timeoflec , time);
                if (index == -1){

                    Log.d(TAG, "loadPreviousEntryOfTT: error in fetching time");
                }else{

                    int token = i*9 + index;
                    Log.d(TAG, "loadPreviousEntryOfTT: setting tthelpers object for index "+token);
                    ttHelpers[token] = new TTHelper();
                    ttHelpers[token].setTime(timeoflec);
                    ttHelpers[token].setLocation(dayObject.getString("Location"));
                    ttHelpers[token].setSubject(dayObject.getString("Subject"));
                    ttHelpers[token].setFaculty(dayObject.getString("Staff"));
                    ttHelpers[token].setDay(days[i]);
                }
            }

        }

        Log.d(TAG, "loadPreviousEntryOfTT: outside for loop");

        RecyclerTimetableAdapter adapter = new RecyclerTimetableAdapter(this, days , time , ttHelpers, timeSlots);
        recyclerView.setAdapter(adapter);



    }

    public int indexOf(String timeOfLec , String[] time){
        for (int i=0; i<time.length ; i++){
            if (time[i].equals(timeOfLec)){
                return i;
            }
        }

        return -1;

    }

    public  void  initializeJson(){
        finalObject = new JSONObject();
        mon = new JSONArray();
        tue = new JSONArray();
        wed = new JSONArray();
        thrus = new JSONArray();
        fri = new JSONArray();
        sat = new JSONArray();

    }

}


