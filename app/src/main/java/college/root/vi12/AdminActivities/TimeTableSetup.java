package college.root.vi12.AdminActivities;

import android.app.Dialog;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;

import college.root.vi12.R;
import io.socket.client.IO;
import io.socket.client.Socket;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Iterator;

public class TimeTableSetup extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    String selection[] = {"","","","","",""};
    String location="",timetable="";
    Spinner spinner_subject,spinner_staff,spinner_location;
    ArrayAdapter<String> adapter_subject;
    ArrayAdapter<String> adapter_staff;
    ArrayAdapter<String> adapter_location;
    final String subject[]= new String[20];
    String staff[] = new String[5];
    String temp_location[] = new String[49];
    TableLayout table;
    TextView tv;
    CheckBox ckb1,ckb2,ckb3,ckb4,ckb5,ckb6,ckb7,ckb8,ckb9,ckb10,ckb11,ckb12,ckb13,ckb14,ckb15,ckb16,ckb17,ckb18,ckb19,ckb20,ckb21,ckb22,ckb23,ckb24,ckb25,ckb26,ckb27,ckb28,ckb29,ckb30,ckb31,ckb32,ckb33,ckb34,ckb35,ckb36,ckb37,ckb38,ckb39,ckb40,ckb41,ckb42,ckb43,ckb44,ckb45,ckb46,ckb47,ckb48;
    String data = "";
    int array[] = new int [49];
    JSONObject roomObject,subjectObject,final_obj, obj, staffObject;
    JSONArray Monday,Tuesday,Wednesday,Thursday,Friday,Saturday;
    Button send_button;
    String TAG = "test";
    String temp_room[]={"Enter room ","room1","room2","room3"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table_setup);
        Monday = new JSONArray();
        Tuesday = new JSONArray();
        Wednesday = new JSONArray();
        Thursday = new JSONArray();
        Friday = new JSONArray();
        Saturday = new JSONArray();
        final_obj = new JSONObject();
        send_button = (Button)findViewById(R.id.final_push);

        Bundle extras = getIntent().getExtras();

        TextView tv_branch = (TextView)findViewById(R.id.tv_branch_id);
        TextView tv_year = (TextView)findViewById(R.id.tv_year_id);
        TextView tv_sem = (TextView)findViewById(R.id.tv_sem_id);
        TextView tv_division = (TextView)findViewById(R.id.tv_division_id);

        tv_branch.setText(extras.getString("Branch"));
        tv_year.setText(extras.getString("Year"));
        tv_sem.setText(extras.getString("Sem"));
        tv_division.setText(extras.getString("Division"));


        //temp_room=extras.getStringArray("Rooms");
        selection[0]=extras.getString("Branch");
        selection[1]=extras.getString("Year");
        selection[2]=extras.getString("Sem");
        selection[3]=extras.getString("Division");

        try {
            subjectObject =  new JSONObject();
            roomObject = new JSONObject();
            // Bundle extras = getIntent().getExtras();
            //obj = (JSONObject) extras.getString("obj");

            String str = getIntent().getStringExtra("StaffObject");
            staffObject = new JSONObject(str);
            Log.d(TAG, "onCreate: staffobject is "+subjectObject);


             str = getIntent().getStringExtra("SubjectObject");
            Log.d(TAG, "str" + str);

            subjectObject = new JSONObject(str);
            String str1 = getIntent().getStringExtra("RoomObject");
            Log.d(TAG, "str" + str);
            String count = subjectObject.getString("SubjectCount");


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



                    roomObject = new JSONObject(str1);


          //  Log.d(TAG, "onCreate: "+obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }


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

         ckb1 = (CheckBox) findViewById(R.id.checkbox_1);
         ckb2 = (CheckBox) findViewById(R.id.checkbox_2);
         ckb3 = (CheckBox) findViewById(R.id.checkbox_3);
         ckb4 = (CheckBox) findViewById(R.id.checkbox_4);
         ckb5 = (CheckBox) findViewById(R.id.checkbox_5);
         ckb6 = (CheckBox) findViewById(R.id.checkbox_6);
         ckb7 = (CheckBox) findViewById(R.id.checkbox_7);
         ckb8 = (CheckBox) findViewById(R.id.checkbox_8);
         ckb9 = (CheckBox) findViewById(R.id.checkbox_9);
         ckb10 = (CheckBox) findViewById(R.id.checkbox_10);
         ckb11 = (CheckBox) findViewById(R.id.checkbox_11);
         ckb12 = (CheckBox) findViewById(R.id.checkbox_12);
         ckb13 = (CheckBox) findViewById(R.id.checkbox_13);
         ckb14 = (CheckBox) findViewById(R.id.checkbox_14);
         ckb15 = (CheckBox) findViewById(R.id.checkbox_15);
         ckb16 = (CheckBox) findViewById(R.id.checkbox_16);
         ckb17 = (CheckBox) findViewById(R.id.checkbox_17);
         ckb18 = (CheckBox) findViewById(R.id.checkbox_18);
         ckb19 = (CheckBox) findViewById(R.id.checkbox_19);
         ckb20 = (CheckBox) findViewById(R.id.checkbox_20);
         ckb21 = (CheckBox) findViewById(R.id.checkbox_21);
         ckb22 = (CheckBox) findViewById(R.id.checkbox_22);
         ckb23 = (CheckBox) findViewById(R.id.checkbox_23);
         ckb24 = (CheckBox) findViewById(R.id.checkbox_24);
         ckb25 = (CheckBox) findViewById(R.id.checkbox_25);
         ckb26 = (CheckBox) findViewById(R.id.checkbox_26);
         ckb27 = (CheckBox) findViewById(R.id.checkbox_27);
         ckb28 = (CheckBox) findViewById(R.id.checkbox_28);
         ckb29 = (CheckBox) findViewById(R.id.checkbox_29);
         ckb30 = (CheckBox) findViewById(R.id.checkbox_30);
         ckb31 = (CheckBox) findViewById(R.id.checkbox_31);
         ckb32 = (CheckBox) findViewById(R.id.checkbox_32);
         ckb33 = (CheckBox) findViewById(R.id.checkbox_33);
         ckb34 = (CheckBox) findViewById(R.id.checkbox_34);
         ckb35 = (CheckBox) findViewById(R.id.checkbox_35);
         ckb36 = (CheckBox) findViewById(R.id.checkbox_36);
         ckb37 = (CheckBox) findViewById(R.id.checkbox_37);
         ckb38 = (CheckBox) findViewById(R.id.checkbox_38);
         ckb39 = (CheckBox) findViewById(R.id.checkbox_39);
         ckb40 = (CheckBox) findViewById(R.id.checkbox_40);
         ckb41 = (CheckBox) findViewById(R.id.checkbox_41);
         ckb42 = (CheckBox) findViewById(R.id.checkbox_42);
         ckb44 = (CheckBox) findViewById(R.id.checkbox_44);
         ckb43 = (CheckBox) findViewById(R.id.checkbox_43);
         ckb45 = (CheckBox) findViewById(R.id.checkbox_45);
         ckb46 = (CheckBox) findViewById(R.id.checkbox_46);
         ckb47 = (CheckBox) findViewById(R.id.checkbox_47);
         ckb48 = (CheckBox) findViewById(R.id.checkbox_48);

         for(int i=1;i<49;i++) {
             array[i] = 0;
             temp_location[i] = " ";
         }
     }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
     //   selection[4]=spinner_staff.getSelectedItem().toString();
        selection[5]=spinner_subject.getSelectedItem().toString();

        try {
            JSONArray array = staffObject.getJSONArray("object");

            int k=0;
            for (int i=0 ; i <array.length() ; i++){
                JSONObject obj = array.getJSONObject(i);
                if (obj.getString("Subject").equals(selection[5]) ){
                    staff[k++] = obj.getString("Faculty");


                }
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void onCheckBoxClicked(View view) {
        // TODO loop this toooooooooooooooooooooo
        // TODO backup array to "array"
            switch (view.getId())
            {
                case R.id.checkbox_1:
                    if(ckb1.isChecked())
                    {
                        array[1]=1;
                       openDialog(view,1);

                    }
                    else
                    {
                        array[1]=0;
                    }
                    break;
                case R.id.checkbox_2:
                    if(ckb2.isChecked())
                    {
                        array[2]=1;
                        openDialog(view,2);
                    }
                    else
                    {
                        array[2]=0;
                    }
                    break;
                case R.id.checkbox_3:
                    if(ckb3.isChecked())
                    {
                        array[3]=1;
                        openDialog(view,3);
                    }
                    else
                    {
                        array[3]=0;
                    }
                    break;
                case R.id.checkbox_4:
                    if(ckb4.isChecked())
                    {
                        array[4]=1;
                        openDialog(view,4);
                    }
                    else
                    {
                        array[4]=0;
                    }break;

                case R.id.checkbox_5:
                    if(ckb5.isChecked())
                    {
                        array[5]=1;openDialog(view,5);
                    }
                    else
                    {
                        array[5]=0;
                    }
                    break;
                case R.id.checkbox_6:
                    if(ckb6.isChecked())
                    {
                        array[6]=1;openDialog(view,6);
                    }
                    else
                    {
                        array[6]=0;
                    }
                    break;
                case R.id.checkbox_7:
                    if(ckb7.isChecked())
                    {
                        array[7]=1;openDialog(view,7);
                    }
                    else
                    {
                        array[7]=0;
                    }
                    break;
                case R.id.checkbox_8:
                    if(ckb8.isChecked())
                    {
                        array[8]=1;openDialog(view,8);
                    }
                    else
                    {
                        array[8]=0;
                    }
                    break;
                case R.id.checkbox_9:
                    if(ckb9.isChecked())
                    {
                        array[9]=1;openDialog(view,9);
                    }
                    else
                    {
                        array[9]=0;
                    }
                    break;
                case R.id.checkbox_10:
                    if(ckb10.isChecked())
                    {
                        array[10]=1;openDialog(view,10);
                    }
                    else
                    {
                        array[10]=0;
                    }
                    break;
                case R.id.checkbox_11:
                    if(ckb11.isChecked())
                    {
                        array[11]=1;openDialog(view,11);
                    }
                    else
                    {
                        array[11]=0;
                    }
                    break;
                case R.id.checkbox_12:
                    if(ckb12.isChecked())
                    {
                        array[12]=1;openDialog(view,12);
                    }
                    else
                    {
                        array[12]=0;
                    }
                    break;
                case R.id.checkbox_13:
                    if(ckb13.isChecked())
                    {
                        array[13]=1;openDialog(view,13);
                    }
                    else
                    {
                        array[13]=0;
                    }
                    break;
                case R.id.checkbox_14:
                    if(ckb14.isChecked())
                    {
                        array[14]=1;openDialog(view,14);
                    }
                    else
                    {
                        array[14]=0;
                    }
                    break;
                case R.id.checkbox_15:
                    if(ckb15.isChecked())
                    {
                        array[15]=1;openDialog(view,15);
                    }
                    else
                    {
                        array[15]=0;
                    }
                    break;
                case R.id.checkbox_16:
                    if(ckb16.isChecked())
                    {
                        array[16]=1;openDialog(view,16);
                    }
                    else
                    {
                        array[16]=0;
                    }
                    break;
                case R.id.checkbox_17:
                    if(ckb17.isChecked())
                    {
                        array[17]=1;openDialog(view,17);
                    }
                    else
                    {
                        array[17]=0;
                    }
                    break;
                case R.id.checkbox_18:
                    if(ckb18.isChecked())
                    {
                        array[18]=1;openDialog(view,18);
                    }
                    else
                    {
                        array[18]=0;
                    }
                    break;
                case R.id.checkbox_19:
                    if(ckb19.isChecked())
                    {
                        array[19]=1;openDialog(view,19);
                    }
                    else
                    {
                        array[19]=0;
                    }
                    break;
                case R.id.checkbox_20:
                    if(ckb20.isChecked())
                    {
                        array[20]=1;openDialog(view,20);
                    }
                    else
                    {
                        array[20]=0;
                    }
                    break;
                case R.id.checkbox_21:
                    if(ckb21.isChecked())
                    {
                        array[21]=1;openDialog(view,21);
                    }
                    else
                    {
                        array[21]=0;
                    }
                    break;
                case R.id.checkbox_22:
                    if(ckb22.isChecked())
                    {
                        array[22]=1;openDialog(view,22);
                    }
                    else
                    {
                        array[22]=0;
                    }
                    break;
                case R.id.checkbox_23:
                    if(ckb23.isChecked())
                    {
                        array[23]=1;openDialog(view,23);
                    }
                    else
                    {
                        array[23]=0;
                    }
                    break;
                case R.id.checkbox_24:
                    if(ckb24.isChecked())
                    {
                        array[24]=1;openDialog(view,24);
                    }
                    else
                    {
                        array[24]=0;
                    }
                    break;
                case R.id.checkbox_25:
                    if(ckb25.isChecked())
                    {
                        array[25]=1;openDialog(view,25);
                    }
                    else
                    {
                        array[25]=0;
                    }
                    break;
                case R.id.checkbox_26:
                    if(ckb26.isChecked())
                    {
                        array[26]=1;openDialog(view,26);
                    }
                    else
                    {
                        array[26]=0;
                    }
                    break;
                case R.id.checkbox_27:
                    if(ckb27.isChecked())
                    {
                        array[27]=1;openDialog(view,27);
                    }
                    else
                    {
                        array[27]=0;
                    }
                    break;
                case R.id.checkbox_28:
                    if(ckb28.isChecked())
                    {
                        array[28]=1;openDialog(view,28);
                    }
                    else
                    {
                        array[28]=0;
                    }
                    break;
                case R.id.checkbox_29:
                    if(ckb29.isChecked())
                    {
                        array[29]=1;openDialog(view,29);
                    }
                    else
                    {
                        array[29]=0;
                    }
                    break;
                case R.id.checkbox_30:
                    if(ckb30.isChecked())
                    {
                        array[30]=1;openDialog(view,30);
                    }
                    else
                    {
                        array[30]=0;
                    }
                    break;
                case R.id.checkbox_31:
                    if(ckb31.isChecked())
                    {
                        array[31]=1;openDialog(view,31);
                    }
                    else
                    {
                        array[31]=0;
                    }
                    break;
                case R.id.checkbox_32:
                    if(ckb32.isChecked())
                    {
                        array[32]=1;openDialog(view,32);
                    }
                    else
                    {
                        array[32]=0;
                    }
                    break;
                case R.id.checkbox_33:
                    if(ckb33.isChecked())
                    {
                        array[33]=1;openDialog(view,33);
                    }
                    else
                    {
                        array[33]=0;
                    }
                    break;
                case R.id.checkbox_34:
                    if(ckb34.isChecked())
                    {
                        array[34]=1;openDialog(view,34);
                    }
                    else
                    {
                        array[34]=0;
                    }
                    break;
                case R.id.checkbox_35:
                    if(ckb35.isChecked())
                    {
                        array[35]=1;openDialog(view,35);
                    }
                    else
                    {
                        array[35]=0;
                    }
                    break;
                case R.id.checkbox_36:
                    if(ckb36.isChecked())
                    {
                        array[36]=1;openDialog(view,36);
                    }
                    else
                    {
                        array[36]=0;
                    }
                    break;
                case R.id.checkbox_37:
                    if(ckb37.isChecked())
                    {
                        array[37]=1;openDialog(view,37);
                    }
                    else
                    {
                        array[37]=0;
                    }
                    break;
                case R.id.checkbox_38:
                    if(ckb38.isChecked())
                    {
                        array[38]=1;openDialog(view,38);
                    }
                    else
                    {
                        array[38]=0;
                    }
                    break;
                case R.id.checkbox_39:
                    if(ckb39.isChecked())
                    {
                        array[39]=1;openDialog(view,39);
                    }
                    else
                    {
                        array[39]=0;
                    }
                    break;
                case R.id.checkbox_40:
                    if(ckb40.isChecked())
                    {
                        array[40]=1;openDialog(view,40);
                    }
                    else
                    {
                        array[40]=0;
                    }
                    break;
                case R.id.checkbox_41:
                    if(ckb41.isChecked()) {
                        array[41] = 1;openDialog(view,41);
                    }
                    else
                    {
                        array[41]=0;
                    }
                    break;
                case R.id.checkbox_42:
                    if(ckb42.isChecked())
                    {
                        array[42]=1;openDialog(view,42);
                    }
                    else
                    {
                        array[42]=0;
                    }
                    break;
                case R.id.checkbox_43:
                    if(ckb43.isChecked())
                    {
                        array[43]=1;openDialog(view,43);
                    }
                    else
                    {
                        array[43]=0;
                    }
                    break;
                case R.id.checkbox_44:
                    if(ckb44.isChecked())
                    {
                        array[44]=1;openDialog(view,44);
                    }
                    else
                    {
                        array[44]=0;
                    }
                    break;
                case R.id.checkbox_45:
                    if(ckb45.isChecked())
                    {
                        array[45]=1;openDialog(view,45);
                    }
                    else
                    {
                        array[45]=0;
                    }
                    break;
                case R.id.checkbox_46:
                    if(ckb46.isChecked())
                    {
                        array[46]=1;openDialog(view,46);
                    }
                    else
                    {
                        array[46]=0;
                    }
                    break;
                case R.id.checkbox_47:
                    if(ckb47.isChecked())
                    {
                        array[47]=1;openDialog(view,47);
                    }
                    else
                    {
                        array[47]=0;
                    }
                    break;
                case R.id.checkbox_48:
                    if(ckb48.isChecked())
                    {
                        array[48]=1;openDialog(view,48);
                    }
                    else
                    {
                        array[48]=0;
                    }
                    break;

        }
    }

    private void openDialog(View view,final int i) {
        final Dialog dialog = new Dialog(TimeTableSetup.this);
        dialog.setContentView(R.layout.dialogforlocation);
        dialog.setCancelable(true);
        spinner_location = (Spinner) dialog.findViewById(R.id.spinner_location);

        adapter_location = new ArrayAdapter<String>(TimeTableSetup.this,android.R.layout.simple_spinner_dropdown_item,temp_room);
        adapter_location.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_location.setAdapter(adapter_location);

        Button button = (Button) dialog.findViewById(R.id.save);
        dialog.show();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location = spinner_location.getSelectedItem().toString();
                temp_location[i]=location;
                dialog.cancel();
            }
        });
    }

    private String addToArray(int i, int j,int pos) {
        String day,time,temp;
        day = convertIntToDay(j);
        time = convertIntToTime(i);
        temp=day+time;
        try {
            obj = new JSONObject();
            obj.put("Subject", spinner_subject.getSelectedItem().toString());
            obj.put("Time", time);
            obj.put("Staff", spinner_staff.getSelectedItem().toString());
            obj.put("Location",temp_location[pos].toString());
        }
            catch (JSONException e) {
                e.printStackTrace();
            }
        switch(day)
        {
            case "Monday":
                Monday.put(obj);
                Log.i("Object Monday",Monday.toString());
                break;
            case "Tuesday":
                Tuesday.put(obj);
                Log.i("Object Tuesday",Tuesday.toString());
                break;
            case "Wednesday":
                Wednesday.put(obj);
                Log.i("Object Wednesday",Wednesday.toString());
                break;
            case "Thursday":
                JSONArray Thursday =new JSONArray();
                Thursday.put(obj);
                Log.i("Object Thursday",Thursday.toString());
                break;
            case "Friday":
                Friday.put(obj);
                Log.i("Object Friday",Friday.toString());
                break;
            case "Saturday":
                Saturday.put(obj);
                Log.i("Object Saturday",Saturday.toString());
                break;
            default:
                Toast.makeText(this,"Invalid",Toast.LENGTH_LONG).show();
        }
         return temp;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void finalize(View view) {
        // TODO loop this .
        if(spinner_subject.getSelectedItem().toString()==" " || spinner_staff.getSelectedItem().toString()==" " || timetable==" ")
        {
            Toast.makeText(this,"Please Enter Valid Text",Toast.LENGTH_LONG).show();
        }
        else
        {
        for(int i=1;i<49;i++)
        {
            switch(i)
            {
                case 1:
                    if(array[1]==1)
                    {
                        timetable = timetable + addToArray(1,1,1);
                    }
                    break;
                case 2:
                    if(array[2]==1)
                    {
                        timetable = timetable + addToArray(1,2,2);
                    }
                    break;
                case 3:
                    if(array[3]==1)
                    {
                        timetable = timetable + addToArray(1,3,3);
                    }
                    break;
                case 4:
                    if(array[4]==1)
                    {
                        timetable = timetable + addToArray(1,4,4);
                    }
                    break;
                case 5:
                    if(array[5]==1)
                    {
                        timetable = timetable + addToArray(1,5,5);
                    }
                    break;
                case 6:
                    if(array[6]==1)
                    {
                        timetable = timetable + addToArray(1,6,6);
                    }
                    break;
                case 7:
                    if(array[7]==1)
                    {
                        timetable = timetable + addToArray(2,1,7);
                    }
                    break;
                case 8:
                    if(array[8]==1)
                    {
                        timetable = timetable + addToArray(2,2,8);
                    }
                    break;
                case 9:
                    if(array[9]==1)
                    {
                        timetable = timetable + addToArray(2,3,9);
                    }
                    break;
                case 10:
                    if(array[10]==1)
                    {
                        timetable = timetable + addToArray(2,4,10);
                    }
                    break;
                case 11:
                    if(array[11]==1)
                    {
                        timetable = timetable + addToArray(2,5,11);
                    }
                    break;
                case 12:
                    if(array[12]==1)
                    {
                        timetable = timetable + addToArray(2,6,12);
                    }
                    break;
                case 13:
                    if(array[13]==1)
                    {
                        timetable = timetable + addToArray(3,1,13);
                    }
                    break;
                case 14:
                    if(array[14]==1)
                    {
                        timetable = timetable + addToArray(3,2,14);
                    }
                    break;
                case 15:
                    if(array[15]==1)
                    {
                        timetable = timetable + addToArray(3,3,15);
                    }
                    break;
                case 16:
                    if(array[16]==1)
                    {
                        timetable = timetable + addToArray(3,4,16);
                    }
                    break;
                case 17:
                    if(array[17]==1)
                    {
                        timetable = timetable + addToArray(3,5,17);
                    }
                    break;
                case 18:
                    if(array[18]==1)
                    {
                        timetable = timetable + addToArray(3,6,18);
                    }
                    break;
                case 19:
                    if(array[19]==1)
                    {
                        timetable = timetable + addToArray(4,1,19);
                    }
                    break;
                case 20:
                    if(array[20]==1)
                    {
                        timetable = timetable + addToArray(4,2,20);
                    }
                    break;
                case 21:
                    if(array[21]==1)
                    {
                        timetable = timetable + addToArray(4,3,21);
                    }
                    break;
                case 22:
                    if(array[22]==1)
                    {
                        timetable = timetable + addToArray(4,4,22);
                    }
                    break;
                case 23:
                    if(array[23]==1)
                    {
                        timetable = timetable + addToArray(4,5,23);
                    }
                    break;
                case 24:
                    if(array[24]==1)
                    {
                        timetable = timetable + addToArray(4,6,24);
                    }
                    break;
                case 25:
                    if(array[25]==1)
                    {
                        timetable = timetable + addToArray(5,1,25);

                    }
                    break;
                case 26:
                    if(array[26]==1)
                    {
                        timetable = timetable + addToArray(5,2,26);
                    }
                    break;
                case 27:
                    if(array[27]==1)
                    {
                        timetable = timetable + addToArray(5,3,27);
                    }
                    break;
                case 28:
                    if(array[28]==1)
                    {
                        timetable = timetable + addToArray(5,4,28);
                    }
                    break;
                case 29:
                    if(array[29]==1)
                    {
                        timetable = timetable + addToArray(5,5,29);
                    }
                    break;
                case 30:
                    if(array[30]==1)
                    {
                        timetable = timetable + addToArray(5,6,30);
                    }
                    break;
                case 31:
                    if(array[31]==1)
                    {
                        timetable = timetable + addToArray(6,1,31);
                    }
                    break;
                case 32:
                    if(array[32]==1)
                    {
                        timetable = timetable + addToArray(6,2,32);
                    }
                    break;
                case 33:
                    if(array[33]==1)
                    {
                        timetable = timetable + addToArray(6,3,33);
                    }
                    break;
                case 34:
                    if(array[34]==1)
                    {
                        timetable = timetable + addToArray(6,4,34);
                    }
                    break;
                case 35:
                    if(array[35]==1)
                    {
                        timetable = timetable + addToArray(6,5,35);
                    }
                    break;
                case 36:
                    if(array[36]==1)
                    {
                        timetable = timetable + addToArray(6,6,36);
                    }
                    break;
                case 37:
                    if(array[37]==1)
                    {
                        timetable = timetable + addToArray(7,1,37);
                    }
                    break;
                case 38:
                    if(array[38]==1)
                    {
                        timetable = timetable + addToArray(7,2,38);
                    }
                    break;
                case 39:
                    if(array[39]==1)
                    {
                        timetable = timetable + addToArray(7,3,39);
                    }
                    break;
                case 40:
                    if(array[40]==1)
                    {
                        timetable = timetable + addToArray(7,4,40);
                    }
                    break;
                case 41:
                    if(array[41]==1)
                    {
                        timetable = timetable + addToArray(7,5,41);
                    }
                    break;
                case 42:
                    if(array[42]==1)
                    {
                        timetable = timetable + addToArray(7,6,42);
                    }
                    break;
                case 43:
                    if(array[43]==1)
                    {
                        timetable = timetable + addToArray(8,1,43);
                    }
                    break;
                case 44:
                    if(array[44]==1)
                    {
                        timetable = timetable + addToArray(8,2,44);
                    }
                    break;
                case 45:
                    if(array[45]==1)
                    {
                        timetable = timetable + addToArray(8,3,45);
                    }
                    break;
                case 46:
                    if(array[46]==1)
                    {
                        timetable = timetable + addToArray(8,4,46);
                    }
                    break;
                case 47:
                    if(array[47]==1)
                    {
                        timetable = timetable + addToArray(8,5,47);
                    }
                    break;
                case 48:
                    if(array[48]==1)
                    {
                        timetable = timetable + addToArray(8,6,48);
                    }
                    break;
            }
        }
           /* data = Arrays.toString(selection);
            data = data + "\n" + timetable + "\n" + location + "\n";
            data = "";*/
        }
    }

    private String convertIntToTime(int i) {
        switch(i) {
            case 1:
                return "8";
            case 2:
                return "9";
            case 3:
                return "10";
            case 4:
                return "11";
            case 5:
                return "12";
            case 6:
                return "13";
            case 7:
                return "14";
            case 8:
                return "15";
            default:
                return "Error";

        }
    }

    private String convertIntToDay(int j) {
        switch(j)
        {
            case 1:
                return "Monday";
            case 2:
                return "Tuesday";
            case 3:
                return "Wednesday";
            case 4:
                return "Thursday";
            case 5:
                return "Friday";
            case 6:
                return "Saturday";
            default:
                return "Error";
        }
    }

    public void push_to_server(View view) {
        try {

            final_obj.put("id",selection[0]+selection[1]+selection[2]+selection[3]);
            final_obj.put("Monday",Monday);
            final_obj.put("Tuesday",Tuesday);
            final_obj.put("Wednesday",Wednesday);
            final_obj.put("Thursday",Thursday);
            final_obj.put("Friday",Friday);
            final_obj.put("Saturday",Saturday);
            Intent i1 = new Intent(TimeTableSetup.this,TimeTableDisplayActivity.class);

            i1.putExtra("object",final_obj.toString());
            i1.putExtra("id",selection[0]+selection[1]+selection[2]+selection[3]);
            i1.putExtra("User" , "Admin");
            startActivity(i1);

        }
        catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
