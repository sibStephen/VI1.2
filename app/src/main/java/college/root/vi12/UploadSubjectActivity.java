package college.root.vi12;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import college.root.vi12.StudentProfile.UserProfile;
import io.socket.client.Socket;

public class UploadSubjectActivity extends AppCompatActivity  {

    EditText etSub1name ,etSub2name ,etSub3name ,etSub4name ,etSub5name ,etSub1Code,etSub2Code,
            etSub3Code,etSub4Code,etSub5Code;

    Spinner spDept , spYear,  spSemester;
    Button btnSubmit;
    String subj1Name, subj2Name, subj3Name, subj4Name, subj5Name, sub1Code,sub2Code,sub3Code
            ,sub4Code,sub5Code , branch , year , semester;
    String TAG = "Test";
    Toast toast;
    NetworkUtils networkUtils;
    Socket socket;
    

    List<String> department;
    List<String> Year;
    List<String> Semester;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_subject);

        initializeViews();
        initializeClasses();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                subj1Name = etSub1name.getText().toString();
                subj2Name = etSub2name.getText().toString();
                subj3Name = etSub3name.getText().toString();
                subj4Name = etSub4name.getText().toString();
                subj5Name = etSub5name.getText().toString();

                sub1Code = etSub1Code.getText().toString();
                sub2Code = etSub2Code.getText().toString();
                sub3Code = etSub3Code.getText().toString();
                sub4Code = etSub4Code.getText().toString();
                sub5Code = etSub5Code.getText().toString();


                if(TextUtils.isEmpty(subj1Name)||TextUtils.isEmpty(subj2Name)||TextUtils.isEmpty(subj3Name)||
                        TextUtils.isEmpty(subj4Name)||TextUtils.isEmpty(subj5Name)||
                        TextUtils.isEmpty(sub1Code)||TextUtils.isEmpty(sub2Code)||TextUtils.isEmpty(sub3Code)||
                        TextUtils.isEmpty(sub4Code)||TextUtils.isEmpty(sub5Code)||TextUtils.isEmpty(year)
                        || TextUtils.isEmpty(branch)){

                    toast.showToast(UploadSubjectActivity.this , "Please enter all the details..");

                }else {
                    // send data on server
                    JSONObject object = new JSONObject();
                    try {
                        object.put("Subj1Name" , subj1Name);
                        object.put("Subj2Name" , subj2Name);
                        object.put("Subj3Name" , subj3Name);
                        object.put("Subj4Name" , subj4Name);
                        object.put("Subj5Name" , subj5Name);
                        object.put("Subj1Code" , sub1Code);
                        object.put("Subj2Code" , sub2Code);
                        object.put("Subj3Code" , sub3Code);
                        object.put("Subj4Code" , sub4Code);
                        object.put("Subj5Code" , sub5Code);
                        object.put("CurrentYear" , "2017");


                        String[] contents = {"Subj1Name", "Subj2Name" , "Subj3Name" , "Subj4Name" , "Subj5Name"
                        ,"Subj1Code","Subj2Code","Subj3Code","Subj4Code","Subj5Code", "CurrentYear"};
                        StringBuilder sb = new StringBuilder();
                        for (int j=0 ; j<contents.length; j++){
                            Log.d(TAG, "onClick: "+contents[j]);
                            sb.append(contents[j]+",");
                        }
                        JSONObject finalObj = new JSONObject();
                        finalObj.put("obj" , object.toString());
                        finalObj.put("contents" , sb.toString());
                        finalObj.put("Length" , contents.length);
                        finalObj.put("collectionName" , "Subjects");
                        finalObj.put("grNumber" , branch+""+year+""+semester);

                        networkUtils.emitSocket("Allinfo",finalObj);
                        networkUtils.listener("Allinfo" , UploadSubjectActivity.this ,getApplicationContext()
                                , toast); //success  listener




                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    try {
                        socket = networkUtils.get();





                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }




            }
        });



    }

    public void initializeClasses(){
        toast = new Toast();
        networkUtils = new NetworkUtils();

    }

    public void initializeViews(){



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

        etSub1name = (EditText)findViewById(R.id.etSubj1Name);
        etSub2name = (EditText)findViewById(R.id.etSub2Name);
        etSub3name = (EditText)findViewById(R.id.etSub3Name);
        etSub4name = (EditText)findViewById(R.id.etSub4Name);
        etSub5name = (EditText)findViewById(R.id.etSub5Name);

        etSub1Code = (EditText)findViewById(R.id.etSubject1Code);
        etSub2Code = (EditText)findViewById(R.id.etSubject2Code);
        etSub3Code = (EditText)findViewById(R.id.etSubject3Code);
        etSub4Code = (EditText)findViewById(R.id.etSubject4Code);
        etSub5Code = (EditText)findViewById(R.id.etSubject5Code);

        spDept = (Spinner)findViewById(R.id.spDept);
        spYear = (Spinner)findViewById(R.id.spYear);
        spSemester = (Spinner)findViewById(R.id.spSem);

        btnSubmit = (Button)findViewById(R.id.btnSubmit);

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
