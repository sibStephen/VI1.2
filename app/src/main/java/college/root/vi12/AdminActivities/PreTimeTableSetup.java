package college.root.vi12.AdminActivities;

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
import java.util.ArrayList;

import college.root.vi12.R;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class PreTimeTableSetup extends AppCompatActivity implements OnItemSelectedListener{
    Spinner spinner_branch,spinner_year,spinner_sem,spinner_division;
    String select_branch="",select_year="",select_sem="",select_division="";
    ArrayAdapter<String> adapter_branch;
    ArrayAdapter<String> adapter_year;
    ArrayAdapter<String> adapter_sem;
    ArrayAdapter<String> adapter_division;
    final String branch[]={" ","Computer","B2","B3","B4"};
    final String year[]={" ","FE","SE","TE","BE"};
    final String sem[]={" ","Sem1","Sem2"};
    final String division[]={" ","D1","D2","D3","D4"};
    private String IPAddr = "http://192.168.1.103:8083/";
    Socket socket_loc;
    JSONObject array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_time_table_setup);

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

    public void proceed(View view) {
        if(spinner_branch.getSelectedItem().toString()==" " || spinner_year.getSelectedItem().toString()==" " ||spinner_sem.getSelectedItem().toString()==" " ||spinner_division.getSelectedItem().toString()==" ")
        {
            Toast.makeText(this,"Please Enter Valid Information",Toast.LENGTH_LONG).show();
        }
        else
        {
            try {
                final JSONObject obj = new JSONObject();
                obj.put("grNumber","2017");
                obj.put("collectionName","RoomAllocation");
                socket_loc = IO.socket(IPAddr);
                socket_loc.connect();
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            socket_loc = IO.socket(IPAddr);
                            socket_loc.connect();
                            Log.d("run: ","is connected");
                            if(socket_loc.connected())
                            {
                                socket_loc.emit("getAllData",obj.toString());
                                socket_loc.on("Get_Rooms",roomListener);
                            }
                        } catch (URISyntaxException e) {

                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            next();

        }
    }

    private void next() {
        Intent time_table_setup_intent = new Intent(getApplicationContext(), TimeTableSetup.class);
        time_table_setup_intent.putExtra("Branch", select_branch);
        time_table_setup_intent.putExtra("Year", select_year);
        time_table_setup_intent.putExtra("Sem", select_sem);
        time_table_setup_intent.putExtra("Division", select_division);

       //time_table_setup_intent.putExtra("Rooms",array);
        startActivity(time_table_setup_intent);
    }

    private Emitter.Listener roomListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("obj",args[0].toString());
        }
    };
}


