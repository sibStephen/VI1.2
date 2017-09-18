package college.root.vi12.AdminActivities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import college.root.vi12.NetworkTasks.CheckNetwork;
import college.root.vi12.NetworkTasks.NetworkUtils;
import college.root.vi12.R;
import college.root.vi12.StudentProfile.Realm.Student_profile;
import io.realm.Realm;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class PreTimeTableSetup extends AppCompatActivity implements OnItemSelectedListener{
    Spinner spinner_branch,spinner_year,spinner_sem,spinner_division;
    String select_branch="",select_year="",select_sem="",select_division="";
    ArrayAdapter<String> adapter_branch;
    ArrayAdapter<String> adapter_year;
    ArrayAdapter<String> adapter_sem;
    ArrayAdapter<String> adapter_division;
    final String branch[]={" ","Computer","Mechanical","E&TC","Civil" , "IT", "Others"};
    final String year[]={" ","FE","SE","TE","BE"};
    final String sem[]={" ","Sem1","Sem2"};
    final String division[]={" ","A","B","C"};
    Socket socket_loc;
    Realm realm;
    NetworkUtils networkUtils;
    Student_profile profile;
    String TAG = "Test";
    JSONObject subjetObject = null;
    JSONObject roomObject = null;
    JSONObject staffObject = null;
    ProgressDialog dialog;
    JSONObject ttJsonObject = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_time_table_setup);

        networkUtils = new NetworkUtils();
        profile  = new Student_profile();

        dialog = new ProgressDialog(this);
        dialog.setTitle("Please wait.....");

        realm = Realm.getDefaultInstance();
        profile = realm.where(Student_profile.class).findFirst();

       // id = profile.getYear() + profile.getBranch() + profile.getSemester();

        spinner_branch = (Spinner)findViewById(R.id.spinner_branch_id);
        spinner_sem = (Spinner)findViewById(R.id.spinner_sem_id);
        spinner_year = (Spinner)findViewById(R.id.spinner_year_id);
        spinner_division = (Spinner)findViewById(R.id.spinner_division_id);

        adapter_branch = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,branch);
        adapter_branch.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_branch.setAdapter(adapter_branch);
        spinner_branch.setOnItemSelectedListener(this);

        adapter_division = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,division);
        adapter_division.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_division.setAdapter(adapter_division);
        spinner_division.setOnItemSelectedListener(this);

        adapter_sem = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,sem);
        adapter_sem.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_sem.setAdapter(adapter_sem);
        spinner_sem.setOnItemSelectedListener(this);

        adapter_year = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,year);
        adapter_year.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_year.setAdapter(adapter_year);
        spinner_year.setOnItemSelectedListener(this);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        select_branch=spinner_branch.getSelectedItem().toString();
        select_year=spinner_year.getSelectedItem().toString();
        select_sem=spinner_sem.getSelectedItem().toString();
        select_division=spinner_division.getSelectedItem().toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void proceed(View view) throws JSONException, URISyntaxException {
        if(spinner_branch.getSelectedItem().toString().equals(" ") || spinner_year.getSelectedItem().toString().equals(" ")||spinner_sem.getSelectedItem().toString()==" "
                ||spinner_division.getSelectedItem().toString().equals(" "))
        {
            Toast.makeText(this,"Please Enter Valid Information",Toast.LENGTH_LONG).show();
        }
        else
        {

            if (CheckNetwork.isNetWorkAvailable(PreTimeTableSetup.this)){


                final JSONObject obj = new JSONObject();
                obj.put("GrNumber","2017");
                obj.put("collectionName","RoomAllocation");

                socket_loc = networkUtils.get();
                socket_loc.emit("getAllData",obj.toString());
                socket_loc.on("Result",roomListener);
                dialog.show();


            }else {
                Toast.makeText(PreTimeTableSetup.this, "No Internet connection ..", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void next() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Intent time_table_setup_intent = new Intent(getApplicationContext(), TableActivity.class);
                time_table_setup_intent.putExtra("Branch", select_branch);
                time_table_setup_intent.putExtra("Year", select_year);
                time_table_setup_intent.putExtra("Sem", select_sem);
                time_table_setup_intent.putExtra("Division", select_division);
                time_table_setup_intent.putExtra("SubjectObject" , subjetObject.toString());
                time_table_setup_intent.putExtra("RoomObject" , roomObject.toString());
                time_table_setup_intent.putExtra("StaffObject" , staffObject.toString());
                time_table_setup_intent.putExtra("ttObject", ttJsonObject.toString());
                dialog.dismiss();
                startActivity(time_table_setup_intent);

            }
        });

       }

    private Emitter.Listener roomListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("obj", args[0].toString());

            if (args[0].equals("0")){
                Log.d(TAG, "call: no data found");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();

                        Toast.makeText(PreTimeTableSetup.this, "Sorry, could not find corresponding data", Toast.LENGTH_SHORT).show();

                    }
                });

            }else{
                final org.json.JSONArray[] array = {(org.json.JSONArray) args[0]};
                Log.d(TAG, "call: array is " + array);

                try {
                    final JSONObject obj = (JSONObject) array[0].get(0);
                    Log.d(TAG, "call: obj is " + obj);
                    roomObject = obj;


                    // more work
                    try {
                        gotoSubjectListener();

                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        }
        };



    private void gotoSubjectListener() throws URISyntaxException, JSONException {

        Socket soc_Subj ;
        soc_Subj = networkUtils.get();
        final JSONObject obj1 = new JSONObject();
        obj1.put("GrNumber",select_year+select_branch+select_sem);

        obj1.put("collectionName" , "Subjects");

        soc_Subj.emit("getAllData", obj1.toString());
        soc_Subj.on("Result", subjectListener );




    }

    private Emitter.Listener subjectListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("obj",args[0].toString());

            if (args[0].equals("0")){
                Log.d(TAG, "call: no data found");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();

                        Toast.makeText(PreTimeTableSetup.this, "Sorry, could not find corresponding data", Toast.LENGTH_SHORT).show();

                    }
                });

            }else{

                final org.json.JSONArray[] array = {(org.json.JSONArray) args[0]};
                Log.d(TAG, "call: array is " + array);

                try {
                    final JSONObject obj = (JSONObject) array[0].get(0);
                    Log.d(TAG, "call: obj is " + obj);
                    subjetObject = obj;

                }catch (Exception e){

                }

                try {
                    goToStaffListener();


                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }


            }



        }
    };

    private void goToStaffListener() throws JSONException, URISyntaxException {
        Socket soc_Subj ;
        soc_Subj = networkUtils.get();
        final JSONObject obj1 = new JSONObject();
        obj1.put("GrNumber",select_branch+select_year+select_sem+select_division);

        obj1.put("collectionName" , "FacultyAllocation");

        soc_Subj.emit("getAllData", obj1.toString());
        soc_Subj.on("Result", staffListener );



    }
    private Emitter.Listener staffListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            Log.d("obj",args[0].toString());

            if (args[0].equals("0")){
                Log.d(TAG, "call: no data found");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();

                        Toast.makeText(PreTimeTableSetup.this, "Sorry, could not find corresponding data", Toast.LENGTH_SHORT).show();

                    }
                });
            }else{
                final org.json.JSONArray[] array = {(org.json.JSONArray) args[0]};
                Log.d(TAG, "call: array is " + array);

                try {
                    final JSONObject obj2 = (JSONObject) array[0].get(0);
                    Log.d(TAG, "call: obj2 is "+obj2);
                    staffObject = obj2;
                    Log.d(TAG, "call: staff is "+staffObject);

                    //next();

                    loadTimeTable();


                }catch (Exception e){

                }


            }


            }


    };



    private void loadTimeTable() throws URISyntaxException, JSONException {

        Socket socket;
        socket = networkUtils.get();
        JSONObject object = new JSONObject();
        object.put("GrNumber", select_branch+select_year+select_sem+select_division);
        object.put("collectionName", "Load_Time_Table");
        //  networkUtils.emitSocket("getAllData" , object);

        socket.emit("getAllData" , object.toString());
        socket.on("Result" , ttlistener);


    }


    Emitter.Listener ttlistener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            if (args[0].equals("0")){
                Log.d(TAG, "call: no data found");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        Toast.makeText(PreTimeTableSetup.this, "No previous TT entry found", Toast.LENGTH_SHORT).show();
                        next();

                    }
                });
            }else{

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(PreTimeTableSetup.this, "Previous TT entry found", Toast.LENGTH_SHORT).show();

                    }
                });

                JSONArray array = (JSONArray) args[0];

                try {
                    ttJsonObject = array.getJSONObject(0);


                    next();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
    };


        }


