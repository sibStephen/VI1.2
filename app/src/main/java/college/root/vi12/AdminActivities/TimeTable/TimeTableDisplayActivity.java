package college.root.vi12.AdminActivities.TimeTable;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import college.root.vi12.NetworkTasks.NetworkUtils;
import college.root.vi12.R;


public class TimeTableDisplayActivity extends AppCompatActivity {

    public static String user= null;
   static TextView tvday;
    private static String LOG_TAG = "CardViewActivity";
    JSONObject obj ;
    String id;
    String TAG = "Test";
    ArrayList<JSONObject> j1;
    NetworkUtils networkUtils;
    Button btnConfirm;
    ArrayList<String> days;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_view);
        btnConfirm = (Button) findViewById(R.id.confirm);
        tvday = (TextView) findViewById(R.id.tv_day);


        Log.d(TAG, "onCreate: ");
        j1 = new ArrayList<>();
        try {
            obj =  new JSONObject();
           // Bundle extras = getIntent().getExtras();
            //obj = (JSONObject) extras.getString("obj");
            String str = getIntent().getStringExtra("object");
            Log.d(TAG, "str" + str);
            id = getIntent().getStringExtra("id");
            user = getIntent().getStringExtra("User");
            if(user.equals("Admin")){
                btnConfirm.setVisibility(View.VISIBLE);

            }else {
                btnConfirm.setVisibility(View.GONE);

            }




            obj = new JSONObject(str);
            Log.d(TAG, "onCreate: "+obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(TAG, "onCreate: "+obj);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 9, GridLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        RecyclerView.Adapter mAdapter = new MyRecyclerViewAdapter(j1, TimeTableDisplayActivity.this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        // mRecyclerView.setAdapter(mAdapter);

        days = new ArrayList<>();
        days.add("Monday");
        days.add("Tuesday");
        days.add("Wednesday");
        days.add("Thursday");
        days.add("Friday");
        days.add("Saturday");

        JSONArray array;
        try {
            int count;
            for(int j=0;j<days.size();j++){
                JSONObject jobj = new JSONObject();
                Log.d(TAG, "onCreate: checking for "+days.get(j));
                array =  obj.getJSONArray(days.get(j));
                count= array.length();
                if (count == 0){
                    // empty array....
                    Log.d(TAG, "onCreate: array is empty hence adding 7 empty entries");
                    for (int i=0;i<TableActivity.time.length;i++){
                        jobj.put("Subject", "");
                        jobj.put("Time", "");
                        jobj.put("Staff", "");
                        jobj.put("Location","");
                        j1.add(jobj);
                    }
                }else {
                    for (int i=0;i<array.length();i++) {
                        jobj = array.getJSONObject(i);
                        j1.add(jobj);
                       // count++;
                        Log.d("array", "onCreate: " + j1);
                    }
                    Log.d(TAG, "onCreate: day is "+days.get(j) + " size is "+array.length());
                    if(count<9)
                    {
                        Log.d(TAG, "onCreate: inside if count is "+count);
                        while(count < 9)
                        {
                            Log.d(TAG, "onCreate: inside while");
                            jobj.put("Subject", "");
                            jobj.put("Time", "");
                            jobj.put("Staff", "");
                            jobj.put("Location","");
                            jobj.put("Div" , "");
                            jobj.put("Year" , "");
                            j1.add(jobj);
                            count++;
                            Log.d(TAG, "onCreate: now length is "+j1.size());

                        }
                    }
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mAdapter = new MyRecyclerViewAdapter(j1 , TimeTableDisplayActivity.this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }



    public void confirm(View view) throws JSONException {
        AlertDialog.Builder builder = new AlertDialog.Builder(TimeTableDisplayActivity.this);
        builder.setTitle("Confirm TimeTable?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String[] contents = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
                StringBuilder sb = new StringBuilder();
                for(int j=0;j<contents.length;j++)
                {
                    sb.append(contents[j]+",");
                }

                try {
                    obj.put("obj",obj.toString());
                    obj.put("contents",sb.toString());
                    obj.put("Length",contents.length);
                    obj.put("collectionName","Load_Time_Table");
                    obj.put("grNumber",id);
                    Log.d("final obj",obj.toString());

                    networkUtils = new NetworkUtils();
                    networkUtils.emitSocket("Allinfo" , obj);
                    Toast.makeText(TimeTableDisplayActivity.this , "TimeTable saved ...", Toast.LENGTH_SHORT).show();
                    finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                }




            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        builder.show();
    }
}

