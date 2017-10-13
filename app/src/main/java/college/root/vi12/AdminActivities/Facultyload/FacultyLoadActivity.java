package college.root.vi12.AdminActivities.Facultyload;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.Iterator;

import college.root.vi12.Miscleneous.Utils;
import college.root.vi12.NetworkTasks.NetworkUtils;
import college.root.vi12.R;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class FacultyLoadActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener
{
    AutoCompleteTextView autocomplete_staff;
    JSONArray store_data = new JSONArray();
    String item[] = {"", "", "", "","",""};
    Spinner spinner_branch, spinner_division, spinner_year, spinner_semester ,spinner_subject;
    String  year[] , branch[] , semester[] , division[] , staff[] ,  subject [] = {"No Subjects"} ;
    String input_branch = null , input_year = null, input_semester = null
            ,  input_division = null , input_subject = null;
    ProgressDialog dialog;
    String TAG = "Test";
    RecyclerView recylerFacAllocation;
    FacultyAllocationAdapter adapter;
    ArrayAdapter<String> subject_adapter;
    FacultyLoadHelper[] helpers;
    NetworkUtils networkUtils;
    JSONObject subjetObject = null;
    boolean SubjectsLoaded = true;
    RecyclerView.LayoutManager manager;
    int numOfObjects = 0;



    public String loadJSONFromAsset() {
        String json = null;
        Log.d(TAG, "loadJSONFromAsset: loading file....");
        String str = null;

        try {

            AssetManager assetManager = getAssets();

            InputStream in = assetManager.open("abc.txt");
            InputStreamReader isr = new InputStreamReader(in);
            int charRead;
            char [] inputBuffer = new char[100];

            while((charRead = isr.read(inputBuffer))>0)
            {
                String readString = String.copyValueOf(inputBuffer,0,charRead);
                str += readString;
            }

        //    getAssets().list("/app");
           /* InputStream is = getAssets().open("FacultyMap.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");
            Log.d(TAG, "loadJSONFromAsset: obtained json is "+json);*/


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return str;

    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_faculty_load);

        initialize();

            autocomplete_staff.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String fac = autocomplete_staff.getText().toString();
                Log.d(TAG, "onItemClick: numofobjs are "+numOfObjects);
                for (int i=0 ; i<numOfObjects ; i++){
                    if (helpers[i] == null){
                        // object not yet created
                        helpers[i] = new FacultyLoadHelper();
                        helpers[i].setFacultyName(fac);
                        helpers[i].setSubjectName(input_subject);
                        helpers[i].setFacultyCode(Utils.mapOfFaculty.get(fac));
                        Log.d(TAG, "onItemClick: code is "+Utils.mapOfFaculty.get(fac));

                        break;
                    }else {
                        if(helpers[i].getSubjectName().equals(input_subject)){
                            helpers[i].setFacultyName(fac);
                            helpers[i].setFacultyCode(Utils.mapOfFaculty.get(fac));
                            Log.d(TAG, "onItemClick: code is "+Utils.mapOfFaculty.get(fac));
                            break;
                        }
                    }

                }
                adapter = new FacultyAllocationAdapter(FacultyLoadActivity.this , helpers);
                recylerFacAllocation.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                autocomplete_staff.setText("");
                Utils.toast(FacultyLoadActivity.this , "Faculty saved...");


            }
        });


    }

    private void initialize() {

        Utils.loadHashMap();
        dialog = new ProgressDialog(FacultyLoadActivity.this);

        networkUtils = new NetworkUtils();
        recylerFacAllocation = (RecyclerView) findViewById(R.id.recyclerFacultyAllocation);
        adapter = new FacultyAllocationAdapter(FacultyLoadActivity.this , helpers);
        manager = new LinearLayoutManager(FacultyLoadActivity.this, LinearLayoutManager.VERTICAL, false);
        recylerFacAllocation.setLayoutManager(manager);
        recylerFacAllocation.hasFixedSize();
        recylerFacAllocation.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        spinner_subject = (Spinner) findViewById(R.id.spinner_subject);
        //  subject = new String [] {" ","Subject1", "Subject2", "Subject3", "Subject4", "Subject5", "Subject7","Subject1", "Subject1", "Subject1", "Subject1", "Subject1", "Subject1"};
        subject_adapter = new ArrayAdapter<String>(FacultyLoadActivity.this, android.R.layout.simple_spinner_item, subject);
        subject_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_subject.setAdapter(subject_adapter);


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
        staff = new String[]{ "Shailesh Thaware", "shubham purandare"};
        staff_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, staff);
        autocomplete_staff.setHint("Enter faculty name");
        autocomplete_staff.setThreshold(1);
        autocomplete_staff.setAdapter(staff_adapter);

    }

    public void setSubjects(){


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
              //
                //  spinner_subject = (Spinner) findViewById(R.id.spinner_subject);
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
        input_subject = spinner_subject.getSelectedItem().toString();
        Log.d(TAG, "onItemSelected: input_division is "+input_division);
        Log.d(TAG, "onItemSelected: input_semester = "+input_semester);
        Log.d(TAG, "onItemSelected: input_branch = "+input_branch);
        Log.d(TAG, "onItemSelected: input_year "+input_year);

        if (!input_division.equals(" ") && !input_branch.equals(" ") && !input_year.equals(" ") && !input_semester.equals(" ") && SubjectsLoaded){

            Log.d(TAG, "onItemSelected: fetching subjects ");
            dialog.setTitle("Fetching Subjects... ");
            dialog.show();
            String id = input_branch+input_year+input_semester;
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


            if (args[0].equals("0")){
                Log.d(TAG, "call: no data found");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        Toast.makeText(FacultyLoadActivity.this, "Subjects not found", Toast.LENGTH_SHORT).show();

                    }
                });
            }else{
                Log.d("obj",args[0].toString());
                final org.json.JSONArray[] array = {(org.json.JSONArray) args[0]};
                Log.d(TAG, "call: array is " + array);

                try {
                    final JSONObject obj = (JSONObject) array[0].get(0);
                    Log.d(TAG, "call: obj is " + obj);
                    subjetObject = obj;
                    Iterator<?> keys = obj.keys();
                    Log.d(TAG, "run: kes are "+keys);
                    numOfObjects = Integer.parseInt( subjetObject.getString("SubjectCount"));
                    helpers = new FacultyLoadHelper[numOfObjects];
                    Log.d(TAG, "call: num of obj set");
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
                    fetchPreviousEntry();
                    dialog.dismiss();
                    Log.d(TAG, "call: subject array is "+subject);


                }catch (Exception e){

                }

//            subject_adapter.notifyDataSetChanged();
                SubjectsLoaded = false;
                setSubjects();

            }




        }

        private void fetchPreviousEntry() {
            String id = input_branch+input_year+input_semester+input_division;
            Socket soc_Subj;
            try {
                soc_Subj = networkUtils.get();
                final JSONObject obj1 = new JSONObject();
                obj1.put("GrNumber",id);
                obj1.put("collectionName" , "FacultyAllocation");

                soc_Subj.emit("getAllData", obj1.toString());
                soc_Subj.on("Result", facultyListener );

            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }



        }
    };




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menuadd, menu);
        return true;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.saveButton:



                new AlertDialog.Builder(FacultyLoadActivity.this)
                        .setTitle("Save Data")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener()
                        {

                            public void onClick(DialogInterface dialog, int id)

                            {
                                try
                                {

                                    for (int i=0; i<helpers.length; i++){

                                        JSONObject j =new JSONObject();
                                        j.put("Subject", helpers[i].getSubjectName());
                                        j.put("FacultyCode", helpers[i].getFacultyCode());
                                        j.put("FacultyName" ,helpers[i].getFacultyName());
                                        store_data.put(j);
                                    }

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
                                    Utils.toast(FacultyLoadActivity.this , "Faculty Allocation saved successfully..");

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
        return super.onOptionsItemSelected(item);
    }


    Emitter.Listener facultyListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {


            if (args[0].equals("0")){
                Log.d(TAG, "call: no data found");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        Utils.toast(FacultyLoadActivity.this , "Previous entry of faculty allocation not  found");

                    }
                });
            }else{

                JSONArray array = (JSONArray) args[0];
                try {
                    JSONObject facultyObj = array.getJSONObject(0);

                    array = facultyObj.getJSONArray("object");
                    Log.d(TAG, "call: array is "+array);
                    helpers = new FacultyLoadHelper[array.length()];
                    for (int i=0 ; i<array.length() ; i++){
                        JSONObject obj1 = array.getJSONObject(i);
                        helpers[i] = new FacultyLoadHelper();
                        Utils.loadHashMap();
                        String name = Utils.mapFacultyID.get(obj1.getString("Faculty"));
                        helpers[i].setFacultyName(name);
                        helpers[i].setSubjectName(obj1.getString("Subject"));
                        Log.d(TAG, "call: obj is "+obj1);


                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter = new FacultyAllocationAdapter(FacultyLoadActivity.this , helpers);
                            recylerFacAllocation.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    };

    @Override
    public void onNothingSelected(AdapterView<?> adapterView)
    {

    }



}