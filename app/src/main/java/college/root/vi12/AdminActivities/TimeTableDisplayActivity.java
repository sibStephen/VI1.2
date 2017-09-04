package college.root.vi12.AdminActivities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import college.root.vi12.NetworkTasks.NetworkUtils;
import college.root.vi12.R;
import io.socket.client.IO;
import io.socket.client.Socket;


public class TimeTableDisplayActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "CardViewActivity";
    JSONObject obj ;
    String id;
    String TAG = "logging";
    ArrayList<JSONObject> j1;
    NetworkUtils networkUtils;
    String user= null;
    Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_view);
        btnConfirm = (Button) findViewById(R.id.confirm);

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
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(this,8,GridLayoutManager.HORIZONTAL,false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MyRecyclerViewAdapter(j1);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        // mRecyclerView.setAdapter(mAdapter);

        ArrayList<String> days = new ArrayList<>();
        days.add("Monday");
        days.add("Tuesday");
        days.add("Wednesday");
        days.add("Thursday");
        days.add("Friday");
        days.add("Saturday");

        JSONArray array;
        try {
            for(int j=0;j<days.size();j++){
                JSONObject jobj = new JSONObject();
                array =  obj.getJSONArray(days.get(j));
                for (int i=0;i<array.length();i++) {
                    jobj = array.getJSONObject(i);
                    j1.add(jobj);
                    Log.d("array", "onCreate: " + j1);
                }
                if(array.length()!=8)
                {
                    while(j1.size()%8!=0)
                    {
                        jobj.put("Subject", "No lecture");
                        jobj.put("Time", "Nill");
                        jobj.put("Staff", "No Staff");
                        jobj.put("Location","No Location");
                        j1.add(jobj);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mAdapter = new MyRecyclerViewAdapter(j1);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((MyRecyclerViewAdapter) mAdapter).setOnItemClickListener(new MyRecyclerViewAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.i(LOG_TAG, " Clicked on Item " + position);
            }
        });
    }

    public void confirm(View view) throws JSONException {
        String[] contents = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
        StringBuilder sb = new StringBuilder();
        for(int j=0;j<contents.length;j++)
        {
            sb.append(contents[j]+",");
        }

        obj.put("obj",obj.toString());
        obj.put("contents",sb.toString());
        obj.put("Length",contents.length);
        obj.put("collectionName","Load_Time_Table");
        obj.put("grNumber",id);
        Log.d("final obj",obj.toString());

        networkUtils = new NetworkUtils();
        networkUtils.emitSocket("Allinfo" , obj);





    }
}

