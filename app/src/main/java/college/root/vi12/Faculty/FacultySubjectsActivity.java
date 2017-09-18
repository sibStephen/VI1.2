package college.root.vi12.Faculty;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import college.root.vi12.Faculty.Realm.FacultySubjRealmObj;
import college.root.vi12.NetworkTasks.NetworkUtils;
import college.root.vi12.R;
import io.realm.Realm;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class FacultySubjectsActivity extends AppCompatActivity {


    String name = "shubham purandare";
    String sem = "Sem2";
    String branch = "Computer";
    NetworkUtils networkUtils ;
    String TAG = "Test";
    FacultySubjRealmObj realmObj;
    Realm realm;
    FacultySubj subjObj;
    ArrayList<FacultySubj> subjArrayList;
    RecyclerView mrecyclerView;
    ProgressDialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_subjects);

        Socket socket ;
        mrecyclerView = (RecyclerView) findViewById(R.id.facultySubRecycler);
        mrecyclerView.hasFixedSize();
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mrecyclerView.setLayoutManager(layoutManager);

        FacultySubAdapter adapter = new FacultySubAdapter(subjArrayList , this);
        mrecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        dialog = new ProgressDialog(this);
        dialog.setTitle("Please wait.....");
        dialog.show();


        networkUtils = new NetworkUtils();
        JSONObject object = new JSONObject();
        Log.d(TAG, "onCreate: called.....");
        try {
            object.put("Name", name);
            object.put("Sem" , sem);
            object.put("Branch", branch);

            socket = networkUtils.get();

            socket.emit("facultySubjects" , object.toString());
            Log.d(TAG, "onCreate: socket emitted");
            socket.on("FacultySubjResult", facultySubjListner);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


    }


    Emitter.Listener facultySubjListner = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            Log.d(TAG, "call:  "+args[0]);

            JSONObject obj = (JSONObject) args[0];
            realm = Realm.getDefaultInstance();
            realmObj = new FacultySubjRealmObj();
            realmObj.setEid("EID1");
            realmObj.setJsonSubjObj(obj.toString());
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    realm.copyToRealmOrUpdate(realmObj);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    updateUI();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });


                }
            });





        }
    };

    private void updateUI() throws JSONException {

        realm = Realm.getDefaultInstance();
        realmObj = realm.where(FacultySubjRealmObj.class).findFirst();

        if(realmObj != null){
//TODO realm data error
            String jsonString = realmObj.getJsonSubjObj();

            JSONObject subjectObj = new JSONObject(jsonString);
            Log.d(TAG, "updateUI: realm gave realm object :"+subjectObj);

            subjArrayList = parseJson(subjectObj);
            Log.d(TAG, "updateUI: ArrayList is "+subjArrayList);
            for (int i=0; i<subjArrayList.size(); i++){
                subjObj = new FacultySubj();
                subjObj = subjArrayList.get(i);
                Log.d(TAG, "updateUI: "+subjObj.getDiv()+" "+subjObj.getSubject());
              //  Log.d(TAG, "updateUI: "+subjArrayList.get(i));
            }
            FacultySubAdapter adapter = new FacultySubAdapter(subjArrayList , FacultySubjectsActivity.this);
            mrecyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            dialog.dismiss();


        }else {
            Log.d(TAG, "updateUI: realm object is null cant update UI");
        }




    }

    private ArrayList<FacultySubj> parseJson(JSONObject subjectObj) throws JSONException {

        ArrayList<FacultySubj> SubjArrayList = new ArrayList<>();

        String[] objects = {"SE", "TE", "BE"};
        for (String str :  objects ){

            Log.d(TAG, "parseJson: str is"+str);
            JSONArray array = subjectObj.getJSONArray(str);
            for (int i=0 ; i<array.length(); i++){
                JSONObject obj =  array.getJSONObject(i);
                subjObj = new FacultySubj();
                subjObj.setYear(str);
                subjObj.setDiv(obj.getString("Div"));
                subjObj.setSubject(obj.getString("Subject"));
                SubjArrayList.add(subjObj);
                Log.d(TAG, "parseJson: list size now is "+SubjArrayList.size());
                FacultySubAdapter adapter = new FacultySubAdapter(SubjArrayList , FacultySubjectsActivity.this);
                mrecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();


            }




        }

        return SubjArrayList;


    }
}
