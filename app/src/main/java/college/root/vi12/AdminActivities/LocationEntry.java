package college.root.vi12.AdminActivities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import college.root.vi12.NetworkTasks.NetworkUtils;
import college.root.vi12.R;
import college.root.vi12.StudentProfile.Student_profile;

public class LocationEntry extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    private static final String TAG = "Test";
    Spinner spinner_branch,spinner_building,spinner_floor,spinner_room;
    String branch[] , building[],floor[],room[];
    String input_branch,input_building,input_floor,input_room;
    String item[] = {"","","",""};
    ArrayAdapter<String> branch_adapter,building_adapter,floor_adapter,room_adapter;
    ArrayList<String> locations ;
    ArrayList<JSONObject> allLocations;
    Button btnSendToServer;
    NetworkUtils networkUtils;
    JSONObject finalObj ;
    TextView tvDisplayEntry;

    //branch,year,sem,location: build.no,floor,room
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_entry);
        locations = new ArrayList<>();
        allLocations = new ArrayList<>();
        networkUtils = new NetworkUtils();
        finalObj =  new JSONObject();

        tvDisplayEntry = (TextView) findViewById(R.id.tvDisplayLocation);

        spinner_branch = (Spinner) findViewById(R.id.spinner_branch);
        branch= new String[] {"branch","Computer", "E&TC", "Civil", "IT" , "Mechanical", "E&AS"};
        branch_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, branch);
        branch_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_branch.setAdapter(branch_adapter);
        spinner_branch.setOnItemSelectedListener(this);


        spinner_building = (Spinner) findViewById(R.id.spinner_building);
        building = new String[] {"building","A","B","C","D","E"};
        building_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,building);
        spinner_building.setAdapter(building_adapter);
        building_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_building.setOnItemSelectedListener(this);

        spinner_floor = (Spinner) findViewById(R.id.spinner_floor);
        floor = new String[] {"floor","0","1","2","3","4"};
        floor_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,floor);
        spinner_floor.setAdapter(floor_adapter);
        floor_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_floor.setOnItemSelectedListener(this);

        spinner_room = (Spinner) findViewById(R.id.spinner_room);
        room = new String[] {"room","01","02","03","04"};
        room_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,room);
        spinner_room.setAdapter(room_adapter);
        room_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_room.setOnItemSelectedListener(this);
        btnSendToServer = (Button)findViewById(R.id.btnSendToServer);

    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {

        item[0] = spinner_branch.getSelectedItem().toString();
        item[1] = spinner_building.getSelectedItem().toString();
        item[2] = spinner_floor.getSelectedItem().toString();
        item[3] = spinner_room.getSelectedItem().toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)

    {
        //    Toast.makeText(this,"please enter valid information", Toast.LENGTH_LONG).show();
    }

    public void add_location(View view) throws InterruptedException

    {


        // spinner_branch.setOnItemSelectedListener(this);
        // spinner_year.setOnItemSelectedListener(this);
        //  spinner_semester.setOnItemSelectedListener(this);
        //  spinner_division.setOnItemSelectedListener(this);
        //   spinner_subject.setOnItemSelectedListener(this);
        //autocomplete_staff.setOnItemSelectedListener(this);


        input_branch = spinner_branch.getSelectedItem().toString();
        input_building = spinner_branch.getSelectedItem().toString();
        input_floor = spinner_floor.getSelectedItem().toString();
        input_room = spinner_room.getSelectedItem().toString();

        String message = "";
        String  loc = "";
        for(int i=1;i<item.length;i++) {
            message = message.concat(item[i] + ",");
            loc = loc.concat(item[i]);
           }
        locations.add(loc);
        tvDisplayEntry.setText(locations.toString());

        Log.d(TAG, "add_location: "+loc);
        Log.d(TAG, "add_location: final array list contains "+locations);
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();


    }

    public void save(View view){

        Log.d(TAG, "save: save method called ");

        try {
            finalObj.put(item[0] , locations);
            tvDisplayEntry.setText(finalObj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "save:  final obj is "+finalObj);
       // allLocations.add(finalObj);
        Log.d(TAG, "save: allLocations now contains "+allLocations);
        locations = new ArrayList<>();



    }

    public void sendToServer(View v) throws JSONException {
        Log.d(TAG, "sendToServer: sending data to server");

        AlertDialog.Builder builder = new AlertDialog.Builder(LocationEntry.this);
        builder.setTitle("Confirm sending data ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Log.d(TAG, "onClick: final obj contains "+finalObj);


                JSONObject sendObj = new JSONObject();
                try {
                    //sendObj.put("departments", allLocations  );

                    finalObj.put("Timestamp",networkUtils.getLocalIpAddress()+" "+ new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime() ));

                    String[] contents = {"Computer" , "E&TC" , "Civil" , "Mechanical", "E&AS" , "IT", "Timestamp"};
                    StringBuilder sb = new StringBuilder();
                    for (int j=0 ; j<contents.length; j++){
                        Log.d(TAG, "onClick: "+contents[j]);
                        sb.append(contents[j]+",");
                    }
                    JSONObject Obj = new JSONObject();
                    Obj.put("obj" , finalObj.toString());
                    Obj.put("contents" , sb.toString());
                    Obj.put("Length" , contents.length);
                    Obj.put("collectionName" , "RoomAllocation");
                    Obj.put("grNumber" ,new SimpleDateFormat("yyyy").format(Calendar.getInstance().getTime()) );


                   networkUtils.emitSocket("Allinfo" , Obj);

                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    {

                    }
                }


            }



        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });
        builder.show();



    }

}
