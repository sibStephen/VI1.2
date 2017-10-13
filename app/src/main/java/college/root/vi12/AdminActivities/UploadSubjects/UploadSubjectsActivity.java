package college.root.vi12.AdminActivities.UploadSubjects;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import college.root.vi12.Miscleneous.Toast;
import college.root.vi12.Miscleneous.Utils;
import college.root.vi12.NetworkTasks.NetworkUtils;
import college.root.vi12.R;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class UploadSubjectsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    LinearLayout container;
    String TAG ="Test";
    ImageButton imgBtnRemove;
    Spinner spDept , spYear,  spSemester;
    List<String> department;
    List<String> Year;
    List<String> Semester;
    String  branch , year , semester;
    NetworkUtils networkUtils ;
    Toast toast;
    ImageButton mimageBtnAdd;
    ProgressDialog dialog;



    private static final String[] NUMBER = new String[]{
            "EOS" , "PCDP" , "CN" , "DSPA" , "SE"
    };
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_subjects);

        container = (LinearLayout) findViewById(R.id.container);

        initializeViews();
        spDept.setOnItemSelectedListener(this);
        spSemester.setOnItemSelectedListener(this);
        spYear.setOnItemSelectedListener(this);

        networkUtils = new NetworkUtils();
        toast = new Toast();



        mimageBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater layoutInflater =
                        (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View addView = layoutInflater.inflate(R.layout.row, null);
                AutoCompleteTextView textOut = (AutoCompleteTextView)addView.findViewById(R.id.actvnewsubj);
                textOut.setAdapter(adapter);
                //textOut.setText(textIn.getText().toString());
                imgBtnRemove = (ImageButton)addView.findViewById(R.id.imgBtnremove);
                imgBtnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((LinearLayout)addView.getParent()).removeView(addView);


                    }
                });

                container.addView(addView);


            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menuadd, menu);
        return true;

    }


    public void addViews(final String name , final String code){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LayoutInflater layoutInflater =
                        (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View addView = layoutInflater.inflate(R.layout.row, null);
                AutoCompleteTextView textOut = (AutoCompleteTextView)addView.findViewById(R.id.actvnewsubj);
                AutoCompleteTextView textCode = (AutoCompleteTextView)addView.findViewById(R.id.actvnewCode);

                textOut.setAdapter(adapter);
                imgBtnRemove = (ImageButton)addView.findViewById(R.id.imgBtnremove);
                imgBtnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((LinearLayout)addView.getParent()).removeView(addView);


                    }
                });


                container.addView(addView);

                textOut.setText(name);
                textCode.setText(code);
            }
        });

    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.saveButton:

                // save code

                AlertDialog.Builder builder = new AlertDialog.Builder(UploadSubjectsActivity.this);
                builder.setTitle("Save Subject group?");
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();
                    }
                });

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listAllAddView();

                    }
                });
                builder.show();
                break;
        }
                return super.onOptionsItemSelected(item);
    }

    private void listAllAddView() {

        JSONObject subjectGroup = new JSONObject();


        int childCount = container.getChildCount();
        String[] contents = new String[childCount+1];

        for (int i = 0; i < childCount; i++) {
            View thisChild = container.getChildAt(i);

            AutoCompleteTextView actvnewSubj = (AutoCompleteTextView) thisChild.findViewById(R.id.actvnewsubj);

            AutoCompleteTextView actvnewcode = (AutoCompleteTextView) thisChild.findViewById(R.id.actvnewCode);
            String newsubjname = actvnewSubj.getText().toString();
            String newsubjcode = actvnewcode.getText().toString();
            Log.d(TAG, "listAllAddView: new subject is "+newsubjname);
            Log.d(TAG, "listAllAddView: new subject code is "+newsubjcode);
            contents[i] = newsubjname;
            try {
                subjectGroup.put(newsubjname , newsubjcode);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }// end of FOR

        contents[childCount] = "SubjectCount";

        try {
            subjectGroup.put("SubjectCount" , String.valueOf(childCount));
            subjectGroup.put("Timestamp",networkUtils.getLocalIpAddress()+" "+ new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime() ));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "listAllAddView: JSON Object is "+subjectGroup);


        StringBuilder sb = new StringBuilder();
        for (int j=0 ; j<contents.length; j++){
            Log.d(TAG, "onClick: "+contents[j]);
            sb.append(contents[j]+",");
        }
        JSONObject finalObj = new JSONObject();
        try {
            finalObj.put("obj" , subjectGroup.toString());
            finalObj.put("contents" , sb.toString());
            finalObj.put("Length" , contents.length);
            finalObj.put("collectionName" , "Subjects");
            finalObj.put("grNumber" , branch+""+year+""+semester);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        Iterator<?> keys = finalObj.keys();

        while( keys.hasNext() ) {
            String key = (String)keys.next();
            try {
                Log.d(TAG, "listAllAddView: key extracted is "+key);
                Log.d(TAG, "listAllAddView: value extracted is "+finalObj.get(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                if ( finalObj.get(key) instanceof JSONObject ) {

                    Log.d(TAG, "listAllAddView: key is "+ finalObj.get(key));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }




        networkUtils.emitSocket("Allinfo",finalObj);
        networkUtils.listener("Allinfo" , UploadSubjectsActivity.this ,getApplicationContext()
                , toast); //success  listener



    }// end of function




    public void initializeViews(){

        mimageBtnAdd = (ImageButton) findViewById(R.id.imgBtnAdd);

        dialog = new ProgressDialog(UploadSubjectsActivity.this);
        dialog.setTitle("Please wait.....");

        department = new ArrayList<String>();
        department.add(" ");
        department.add("Computer");
        department.add("Mechanical");
        department.add("E&TC");
        department.add("Civil");
        department.add("IT");

        Year = new ArrayList<String>();
        Year.add(" ");
        Year.add("FE");
        Year.add("SE");
        Year.add("TE");
        Year.add("BE");
        Year.add("ME");

        Semester = new ArrayList<>();
        Semester.add(" ");
        Semester.add("Sem1");
        Semester.add("Sem2");


        spDept = (Spinner)findViewById(R.id.spDept);
        spYear = (Spinner)findViewById(R.id.spYear);
        spSemester = (Spinner)findViewById(R.id.spSem);

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, NUMBER);



        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,department );
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spDept.setAdapter(dataAdapter);
        ArrayAdapter<String> adapterYear = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,Year );
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spYear.setAdapter(adapterYear);

        ArrayAdapter<String> adapterSem = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,Semester );
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spSemester.setAdapter(adapterSem);




    }
    private void fetchSubjects() throws URISyntaxException, JSONException {

            //   dialog.show();

        Socket soc_Subj ;
        soc_Subj = networkUtils.get();
        final JSONObject obj1 = new JSONObject();
        obj1.put("GrNumber",year+branch+semester);

        obj1.put("collectionName" , "Subjects");

        soc_Subj.emit("getAllData", obj1.toString());
        soc_Subj.on("Result", subjectListener );


    }
    Emitter.Listener subjectListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            if (!args[0].equals("0") ){

                try {
                    final org.json.JSONArray[] array = {(org.json.JSONArray) args[0]};

                    final JSONObject obj = (JSONObject) array[0].get(0);
                    int i=0;
                    // count[0] = obj.getString("SubjectCount");
                    String  count = obj.getString("SubjectCount");
                    Log.d(TAG, "run: count is "+obj.getString("SubjectCount"));
                    Log.d(TAG, "run: count variavle is "+count);
                    Log.d(TAG, "run: String array of name declared with count "+count);


                    Iterator<?> keys = obj.keys();
                    Log.d(TAG, "run: kes are "+keys);

                    while( keys.hasNext() ) {
                        String key = (String) keys.next();
                        Log.d(TAG, "run: key is " + key);
                        Log.d(TAG, "run: and value of key is " + obj.getString(key));

                        if (key.equals("_id") || key.equals("SubjectCount")) {
                            // do not store it in name array
                        } else {

                            addViews(key , obj.getString(key));

                        }
                    }
                //    dialog.dismiss();




                        } catch (JSONException e) {
                    e.printStackTrace();
                }


            }else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Utils.toast(UploadSubjectsActivity.this , "Subjects not yet loaded...");
                 //       dialog.dismiss();

                        container.removeAllViews();

                    }
                });
            }

        }
            };

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Log.d(TAG, "onItemSelected: called...");
        year = spYear.getSelectedItem().toString();
        branch = spDept.getSelectedItem().toString();
        semester = spSemester.getSelectedItem().toString();
        Log.d(TAG, "onItemSelected: branch is "+branch);
        Log.d(TAG, "onItemSelected: sem is "+semester);
        Log.d(TAG, "onItemSelected: year is "+year);
        if (!year.equals(" ") && !semester.equals(" ") && !branch.equals(" ")){

            try {
                fetchSubjects();
            }  catch (JSONException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}