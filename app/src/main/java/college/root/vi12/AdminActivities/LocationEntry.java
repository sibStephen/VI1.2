package college.root.vi12.AdminActivities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import college.root.vi12.Miscleneous.Utils;
import college.root.vi12.NetworkTasks.NetworkUtils;
import college.root.vi12.R;

public class LocationEntry extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    private static final String TAG = "Test";
    Spinner spinner_branch,spinner_building,spinner_floor,spinner_room;
    String branch[] , building[],floor[],room[];
    String input_building = "",input_floor="",input_room="";
    String loca= "";
    String item[] = {"","","",""};
    ArrayAdapter<String> branch_adapter,building_adapter,floor_adapter,room_adapter;
    ArrayList<String> room_location = new ArrayList<>();
    JSONArray locations ;
    ArrayList<JSONObject> allLocations;
    NetworkUtils networkUtils;
    JSONObject finalObj ;
    TextView tvDisplayEntry;
    String curentBranch = "";


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflator =  getMenuInflater();
        inflator.inflate(R.menu.menulocationentry, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id =item.getItemId();
        if(id==R.id.save_location)
        {


            Log.d(TAG, "sendToServer: sending data to server");

            AlertDialog.Builder builder = new AlertDialog.Builder(LocationEntry.this);
            builder.setTitle("Confirm sending data ?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Log.d(TAG, "onClick: final obj contains "+finalObj);


                    try {
                        JSONArray array = new JSONArray(room_location);

                        finalObj.put(curentBranch , array);
                        tvDisplayEntry.setText(finalObj.toString());

                        finalObj.put("Timestamp",networkUtils.getLocalIpAddress()+" "+ new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(Calendar.getInstance().getTime() ));

                        String[] contents = {curentBranch,  "Timestamp"};
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
                        Utils.toast(LocationEntry.this , "Rooms saved successfully");
                        tvDisplayEntry.setText("");
                        startActivity(new Intent(LocationEntry.this , LocationEntry.class));
                        finish();

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
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_entry);


        setTitle("Upload Locations");

        locations = new JSONArray();
        allLocations = new ArrayList<>();
        networkUtils = new NetworkUtils();
        finalObj =  new JSONObject();

        tvDisplayEntry = (TextView) findViewById(R.id.tvDisplayLocation);

        spinner_branch = (Spinner) findViewById(R.id.spinner_branch);
        branch= new String[] {"Branch","Computer", "E&TC", "Civil", "IT" , "Mechanical", "E&AS"};
        branch_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, branch);
        branch_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_branch.setAdapter(branch_adapter);
        spinner_branch.setOnItemSelectedListener(this);


        spinner_building = (Spinner) findViewById(R.id.spinner_building);
        building = new String[] {"Building","A","B","C","D","E"};
        building_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,building);
        spinner_building.setAdapter(building_adapter);
        building_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_building.setOnItemSelectedListener(this);

        spinner_floor = (Spinner) findViewById(R.id.spinner_floor);
        floor = new String[] {"Floor","0","1","2","3","4"};
        floor_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,floor);
        spinner_floor.setAdapter(floor_adapter);
        floor_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_floor.setOnItemSelectedListener(this);

        spinner_room = (Spinner) findViewById(R.id.spinner_room);
        room = new String[] {"Rooms","01","02","03","04"};
        room_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,room);
        spinner_room.setAdapter(room_adapter);
        room_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_room.setOnItemSelectedListener(this);







    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {

        curentBranch = item[0] = spinner_branch.getSelectedItem().toString();
        item[1] = spinner_building.getSelectedItem().toString();
        item[2] = spinner_floor.getSelectedItem().toString();
        item[3] = spinner_room.getSelectedItem().toString();
        Log.d(TAG, "onItemSelected: " + spinner_room.getSelectedItem().toString());
        Log.d(TAG, "onItemSelected: " + spinner_branch.getSelectedItem().toString());

        if(room_location.isEmpty())
        {
            if(!item[2].equals("Floor") && !item[3].equals("Rooms") && !item[1].equals("Building")) {
                int f = Integer.parseInt(item[2]);
                Log.d(TAG, "onItemSelected: " + f );
                int r = Integer.parseInt(item[3]);
                Log.d(TAG, "onItemSelected: " + r );
                for (int i = 0; i <= f; i++) {
                    for (int j = 1; j <= r; j++) {
                        loca = (item[1] + i + "0" + j );
                        Log.d(TAG, "onItemSelected: " + loca);
                        room_location.add(loca);
                        tvDisplayEntry.setText(room_location.toString());
                    }
                }
            }
            Log.d(TAG, "added_location: final array list contains " + room_location);

        }
        else
        {
            input_building = spinner_building.getSelectedItem().toString();
            input_floor = spinner_floor.getSelectedItem().toString();
            input_room = spinner_room.getSelectedItem().toString();
            Log.d(TAG, "onItemSelected: " + input_building);
            Log.d(TAG, "onItemSelected: " + input_floor);
            Log.d(TAG, "onItemSelected: " + input_room);
        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent)

    {
    }

    public void Edit(View view)
    {
        final Dialog dialog = new Dialog(LocationEntry.this);
        dialog.setContentView(R.layout.dialogforrooms);
        dialog.setCancelable(true);

        final Spinner spRoom , spBuilding , spFloor;

        spBuilding = (Spinner) dialog.findViewById(R.id.sp);
        building = new String[] {"Building","A","B","C","D","E"};
        building_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,building);
        spBuilding.setAdapter(building_adapter);
        building_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBuilding.setOnItemSelectedListener(this);

        spFloor = (Spinner) dialog.findViewById(R.id.spinner7);
        floor = new String[] {"Floor","0","1","2","3","4"};
        floor_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,floor);
        spFloor.setAdapter(floor_adapter);
        floor_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFloor.setOnItemSelectedListener(this);

        spRoom = (Spinner) dialog.findViewById(R.id.spinner8);
        room = new String[] {"Rooms","01","02","03","04"};
        room_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,room);
        spRoom.setAdapter(room_adapter);
        room_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRoom.setOnItemSelectedListener(this);


        Button addLoc = (Button) dialog.findViewById(R.id.button2);
        Button remLoc = (Button) dialog.findViewById(R.id.button5);


        addLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                input_room = spRoom.getSelectedItem().toString();
                input_floor = spFloor.getSelectedItem().toString();
                input_building = spBuilding.getSelectedItem().toString();

                if(!input_building.equals("") || !input_floor.equals("") || !input_room.equals("")){
                    String ad_loc = input_building + input_floor + input_room;

                    tvDisplayEntry.setText(" ");
                    room_location.add(ad_loc);
                    tvDisplayEntry.setText(room_location.toString());

                    Log.d(TAG, "onItemSelected: " + spinner_branch.getSelectedItem().toString());
                    Log.d(TAG, "add_location: " + ad_loc);
                    Log.d(TAG, "added_location: final array list contains " + room_location);
                    Utils.toast(LocationEntry.this , "Loaction "+ad_loc + " added in list");
                    dialog.dismiss();
                }else {
                    Utils.toast(LocationEntry.this , "Enter a valid room...");
                    dialog.dismiss();
                }



            }
        });


        remLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                input_room = spRoom.getSelectedItem().toString();
                input_floor = spFloor.getSelectedItem().toString();
                input_building = spBuilding.getSelectedItem().toString();

                Log.d(TAG, "onItemSelected: " + spinner_branch.getSelectedItem().toString());

                if(!input_building.equals("") || !input_floor.equals("") || !input_room.equals("")) {

                    String ad_loc = input_building + input_floor + input_room;
                    room_location.remove(ad_loc);
                    tvDisplayEntry.setText(" ");
                    tvDisplayEntry.setText(room_location.toString());
                    Log.d(TAG, "remove_location: " + ad_loc);
                    Log.d(TAG, "removed_location: final array list contains " + room_location);
                    Utils.toast(LocationEntry.this , "Loaction "+ad_loc + " removed from list");
                    dialog.dismiss();
                }else {
                    Utils.toast(LocationEntry.this , "Enter a valid room...");
                    dialog.dismiss();

                }
            }
        });
        dialog.show();

    }


}