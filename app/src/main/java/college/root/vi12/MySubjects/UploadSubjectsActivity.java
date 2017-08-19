package college.root.vi12.MySubjects;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import college.root.vi12.NetworkTasks.NetworkUtils;
import college.root.vi12.R;
import college.root.vi12.Miscleneous.Toast;

public class UploadSubjectsActivity extends AppCompatActivity {


    AutoCompleteTextView textIn;
    Button buttonAdd, btnSave;
    LinearLayout container;
    String TAG ="Test";
    ImageButton imgBtnAdd;
    ImageButton imgBtnRemove;
    Spinner spDept , spYear,  spSemester;
    List<String> department;
    List<String> Year;
    List<String> Semester;
    String  branch , year , semester;
    NetworkUtils networkUtils ;
    Toast toast;
    ImageButton mimageBtnAdd;


    private static final String[] NUMBER = new String[]{
            "EOS" , "PCDP" , "CN" , "DSPA" , "SE"
    };
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_subjects);

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, NUMBER);






        container = (LinearLayout) findViewById(R.id.container);
        btnSave = (Button)findViewById(R.id.btnSaveSubjs);

        initializeViews();

        networkUtils = new NetworkUtils();
        toast = new Toast();

        btnSave.setVisibility(View.INVISIBLE);

        mimageBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater layoutInflater =
                        (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View addView = layoutInflater.inflate(R.layout.row, null);
                AutoCompleteTextView textOut = (AutoCompleteTextView)addView.findViewById(R.id.actvnewsubj);
                textOut.setAdapter(adapter);
                //textOut.setText(textIn.getText().toString());
                imgBtnRemove = (ImageButton)findViewById(R.id.imgBtnremove);
                final View.OnClickListener thisListener = new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        ((LinearLayout)addView.getParent()).removeView(addView);

                         listAllAddView();
                    }
                };
                // imgBtnRemove.setOnClickListener(thisListener);
                container.addView(addView);
                btnSave.setVisibility(View.VISIBLE);


            }
        });


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


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
                }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menuadd, menu);
        return true;

    }






    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.add:




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


        department = new ArrayList<String>();
        department.add("Computer");
        department.add("Mechanical");
        department.add("E&TC");
        department.add("Civil");
        department.add("IT");

        Year = new ArrayList<String>();
        Year.add("FE");
        Year.add("SE");
        Year.add("TE");
        Year.add("BE");
        Year.add("ME");

        Semester = new ArrayList<>();
        Semester.add("Sem1");
        Semester.add("Sem2");


        spDept = (Spinner)findViewById(R.id.spDept);
        spYear = (Spinner)findViewById(R.id.spYear);
        spSemester = (Spinner)findViewById(R.id.spSem);


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


        spSemester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                semester = adapterView.getItemAtPosition(position).toString();
                Log.d(TAG, "onItemSelected: Semester is selected with "+semester);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                branch  = adapterView.getItemAtPosition(position).toString();
                Log.d(TAG, "onItemSelected: Branch is selected with "+branch);


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        spDept.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                year = adapterView.getItemAtPosition(position).toString();
                Log.d(TAG, "onItemSelected: year is selected with "+year);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

}