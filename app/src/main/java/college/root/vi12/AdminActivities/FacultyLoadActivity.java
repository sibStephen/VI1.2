package college.root.vi12.AdminActivities;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import college.root.vi12.R;


public class FacultyLoadActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener
{
    AutoCompleteTextView autocomplete_staff;

    ArrayList<JSONObject> store_data = new ArrayList<JSONObject>();
    String item[] = {"", "", "", "","",""};
    Spinner spinner_branch, spinner_division, spinner_year, spinner_semester ,spinner_subject;;
    String  year[] , branch[] , semester[] , division[] , staff[] ,  subject [] ;
    String input_branch , input_year , input_semester ,  input_division , input_subject , input_staff;

    JSONObject finalObj ;
    String sa;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_faculty_load);

        spinner_branch = (Spinner) findViewById(R.id.spinner_branch);
        ArrayAdapter<String> branch_adapter;
        branch = new String[]{" ", "B1", "B2", "B3", "B4"};
        branch_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, branch);
        branch_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_branch.setAdapter(branch_adapter);
        spinner_branch.setOnItemSelectedListener(this);


        spinner_year = (Spinner) findViewById(R.id.spinner_year);
        year = new String[]{" ", "F.E", "S.E", "T.E", "B.E"};
        ArrayAdapter<String> year_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, year);
        spinner_year.setAdapter(year_adapter);
        year_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_year.setOnItemSelectedListener(this);


        spinner_semester = (Spinner) findViewById(R.id.spinner_semester);
        semester = new String[]{" ", "Sem 1 ", "Sem2"};
        ArrayAdapter<String> semester_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, semester);
        spinner_semester.setAdapter(semester_adapter);
        semester_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_semester.setOnItemSelectedListener(this);


        spinner_division = (Spinner) findViewById(R.id.spinner_division);
        division = new String[]{" ", "D1", "D2", "D3", "D4"};
        ArrayAdapter<String> division_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, division);
        division_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_division.setAdapter(division_adapter);
        spinner_division.setOnItemSelectedListener(this);

        spinner_subject = (Spinner) findViewById(R.id.spinner_subject);
        ArrayAdapter<String> subject_adapter;
        subject = new String [] {" ","Subject1", "Subject1", "Subject1", "Subject1", "Subject1", "Subject1","Subject1", "Subject1", "Subject1", "Subject1", "Subject1", "Subject1"};
        subject_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, subject);
        subject_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_subject.setAdapter(subject_adapter);
        spinner_subject.setOnItemSelectedListener(this);


        autocomplete_staff = (AutoCompleteTextView) findViewById(R.id.autocomplete_staff);
        ArrayAdapter<String> staff_adapter;
        staff = new String[]{"Harsh kulkarni", "harshal", "shashi", "shubham purandare"};
        staff_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, staff);
        autocomplete_staff.setHint("Enter faculty name");
        autocomplete_staff.setThreshold(1);
        autocomplete_staff.setAdapter(staff_adapter);
        autocomplete_staff.setOnItemSelectedListener(this);


    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
    {
        item[0] = spinner_branch.getSelectedItem().toString();
        item[1] = spinner_year.getSelectedItem().toString();
        item[2] = spinner_semester.getSelectedItem().toString();
        item[3] = spinner_division.getSelectedItem().toString();

        input_division = spinner_division.getSelectedItem().toString();
        input_semester = spinner_semester.getSelectedItem().toString();
        input_branch = spinner_branch.getSelectedItem().toString();
        input_year = spinner_year.getSelectedItem().toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView)
    {

    }
    public void save (View view) throws InterruptedException

        {

            item[4] = spinner_subject.getSelectedItem().toString();

            input_subject = spinner_subject.getSelectedItem().toString();
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
                                    store_data.add(j);
                                    Log.d("tag", "onClick: " + store_data);

                                }
                                catch (JSONException e)
                                {
                                    e.printStackTrace();
                                }
                                dialog.cancel();
                            }
                        })


                        .setNegativeButton( "cancel ", new DialogInterface.OnClickListener()
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
                            finalObj =new JSONObject();
                            finalObj.put("_id", input_branch + input_year + input_semester + input_division);
                            finalObj.put("Subjects_Faculty", store_data);
                            Log.d("tag", "onClick: " + finalObj);
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