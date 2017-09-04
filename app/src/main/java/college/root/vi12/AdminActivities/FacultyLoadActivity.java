package college.root.vi12.AdminActivities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import college.root.vi12.MySubjects.MySubjects;
import college.root.vi12.NetworkTasks.NetworkUtils;
import college.root.vi12.R;
import io.realm.Realm;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class FacultyLoadActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener
{
    AutoCompleteTextView autocomplete_staff;


    JSONArray store_data = new JSONArray();
    String item[] = {"", "", "", "","",""};
    Spinner spinner_branch, spinner_division, spinner_year, spinner_semester ,spinner_subject;;
    String  year[] , branch[] , semester[] , division[] , staff[] ,  subject [] ;
    String input_branch = null , input_year = null, input_semester = null
            ,  input_division = null , input_subject = null, input_staff= null;
    ProgressDialog dialog;
    String sa;
    String TAG = "Test";
    ArrayAdapter<String> subject_adapter;

    NetworkUtils networkUtils;
    JSONObject subjetObject = null;
    boolean SubjectsLoaded = true;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_faculty_load);

         dialog = new ProgressDialog(FacultyLoadActivity.this);

        networkUtils = new NetworkUtils();

        spinner_branch = (Spinner) findViewById(R.id.spinner_branch);
        ArrayAdapter<String> branch_adapter;
        branch = new String[]{" ", "Computer", "Mechanical", "E&TC", "IT" , "Civil"};
        branch_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, branch);
        branch_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_branch.setAdapter(branch_adapter);
        spinner_branch.setOnItemSelectedListener(this);


        spinner_year = (Spinner) findViewById(R.id.spinner_year);
        year = new String[]{" ", "FE", "SE", "TE", "BE"};
        ArrayAdapter<String> year_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, year);
        spinner_year.setAdapter(year_adapter);
        year_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_year.setOnItemSelectedListener(this);


        spinner_semester = (Spinner) findViewById(R.id.spinner_semester);
        semester = new String[]{" ", "Sem1 ", "Sem2"};
        ArrayAdapter<String> semester_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, semester);
        spinner_semester.setAdapter(semester_adapter);
        semester_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_semester.setOnItemSelectedListener(this);


        spinner_division = (Spinner) findViewById(R.id.spinner_division);
        division = new String[]{" ", "A", "B", "C"};
        ArrayAdapter<String> division_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, division);
        division_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_division.setAdapter(division_adapter);
        spinner_division.setOnItemSelectedListener(this);



        autocomplete_staff = (AutoCompleteTextView) findViewById(R.id.autocomplete_staff);
        ArrayAdapter<String> staff_adapter;
        staff = new String[]{"Harsh kulkarni", "harshal", "shashi", "shubham purandare"};
        staff_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, staff);
        autocomplete_staff.setHint("Enter faculty name");
        autocomplete_staff.setThreshold(1);
        autocomplete_staff.setAdapter(staff_adapter);
        autocomplete_staff.setOnItemSelectedListener(this);


    }

    public void setSubjects(){


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                spinner_subject = (Spinner) findViewById(R.id.spinner_subject);
                //  subject = new String [] {" ","Subject1", "Subject2", "Subject3", "Subject4", "Subject5", "Subject7","Subject1", "Subject1", "Subject1", "Subject1", "Subject1", "Subject1"};
                subject_adapter = new ArrayAdapter<String>(FacultyLoadActivity.this, android.R.layout.simple_spinner_item, subject);
                subject_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_subject.setAdapter(subject_adapter);
                spinner_subject.setOnItemSelectedListener(FacultyLoadActivity.this);

            }
        });

}
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
    {
        input_branch  = item[0] = spinner_branch.getSelectedItem().toString();
        input_year = item[1] = spinner_year.getSelectedItem().toString();
        input_semester =  item[2] = spinner_semester.getSelectedItem().toString();
        input_division = item[3] = spinner_division.getSelectedItem().toString();
        Log.d(TAG, "onItemSelected: input_division is "+input_division);
        Log.d(TAG, "onItemSelected: input_semester = "+input_semester);
        Log.d(TAG, "onItemSelected: input_branch = "+input_branch);
        Log.d(TAG, "onItemSelected: input_year "+input_year);

        if (!input_branch.equals(" ") && !input_year.equals(" ") && !input_semester.equals(" ") && SubjectsLoaded){

            Log.d(TAG, "onItemSelected: fetching subjects ");
            dialog.setTitle("Fetching Subjects... ");
            dialog.show();
            String id = input_year+input_branch+input_semester;
            Socket soc_Subj;
            try {
                soc_Subj = networkUtils.get();
                final JSONObject obj1 = new JSONObject();
                obj1.put("GrNumber",id);
                obj1.put("collectionName" , "Subjects");

                soc_Subj.emit("getAllData", obj1.toString());
                soc_Subj.on("Result", subjectListener );

            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }


    }

    private Emitter.Listener subjectListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("obj",args[0].toString());
            final org.json.JSONArray[] array = {(org.json.JSONArray) args[0]};
            Log.d(TAG, "call: array is " + array);

            try {
                final JSONObject obj = (JSONObject) array[0].get(0);
                Log.d(TAG, "call: obj is " + obj);
                subjetObject = obj;
                Iterator<?> keys = obj.keys();
                Log.d(TAG, "run: kes are "+keys);
                subject = new String[ Integer.parseInt( subjetObject.getString("SubjectCount")) ];

                int i=0;
                while( keys.hasNext() ) {
                    String key = (String) keys.next();
                    Log.d(TAG, "run: key is "+key);
                    Log.d(TAG, "run: and value of key is "+obj.getString(key));

                    if (key.equals("_id") || key.equals("SubjectCount")){
                        // do not store it in name array
                    }else {
                        subject[i] = key;  // store the subject names in string array
                        i++;

                    }
                }// end of while loop
                dialog.dismiss();
                Log.d(TAG, "call: subject array is "+subject);


            }catch (Exception e){

            }

//            subject_adapter.notifyDataSetChanged();
            SubjectsLoaded = false;
            setSubjects();



        }
    };

    @Override
    public void onNothingSelected(AdapterView<?> adapterView)
    {

    }
    public void save (View view) throws InterruptedException

        {

            input_subject =  item[4] = spinner_subject.getSelectedItem().toString();

            input_staff = autocomplete_staff.getText().toString();

            String s = " ";

            for(int i=0 ; i<staff.length ; i++)
            {
                if(input_staff.equals(staff[i]))
                {
                    s = "true";
                }

            }


            if (  input_staff.equals(" ") || s != "true" || input_branch.equals(" ") || input_year.equals(" ")
                    || input_division.equals(" ") || input_semester.equals(" ") || input_subject.equals(" ")
                    )

                 {
                        Toast.makeText(this,"please enter valid information", Toast.LENGTH_LONG).show();
                 }
            else
            {
                sa = autocomplete_staff.getText().toString();
                item[5] = sa;
                String msg = Arrays.toString(item);
                //Toast.makeText(this, msg, Toast.LENGTH_LONG).show();



                new AlertDialog.Builder(FacultyLoadActivity.this)
                        .setTitle("Confirm")
                        .setMessage("Your Selection is " + msg)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                JSONObject j = new JSONObject();
                                try
                                {
                                    j.put("Subject", input_subject);
                                    j.put("Faculty", input_staff);
                                    store_data.put(j);
                                    Log.d("tag", "onClick: " + store_data);

                                }
                                catch (JSONException e)
                                {
                                    e.printStackTrace();
                                }
                                dialog.cancel();
                            }
                        })


                        .setNegativeButton( "cancel" +
                                " ", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {

                                dialog.cancel();
                            }
                        })
                        .show();
            }

        }

    public void submit (View view) throws InterruptedException, JSONException

        {

            new AlertDialog.Builder(FacultyLoadActivity.this)
                .setTitle("Save Data")
                .setPositiveButton("OK", new DialogInterface.OnClickListener()
                {

                    public void onClick(DialogInterface dialog, int id)

                    {
                        try
                        {

                            JSONObject facultyObject = new JSONObject();
                            facultyObject.put("object" , store_data);


                            String[] contents = { "object"};
                            StringBuilder sb = new StringBuilder();
                            for (int j=0 ; j<contents.length; j++){
                                Log.d("Test", "onClick: "+contents[j]);
                                sb.append(contents[j]+",");
                            }
                            JSONObject finalObj = new JSONObject();
                            finalObj.put("obj" , facultyObject.toString());
                            finalObj.put("contents" , sb.toString());
                            finalObj.put("Length" , contents.length);
                            finalObj.put("collectionName" , "FacultyAllocation");
                            finalObj.put("grNumber" ,input_branch + input_year + input_semester + input_division );

                            networkUtils.emitSocket("Allinfo",finalObj);









                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }


                        finish();
                        startActivity(getIntent());
                    }
                })
                .setNegativeButton( "cancel ", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {

                        dialog.cancel();
                    }
                }).show();

        }
}