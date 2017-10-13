package college.root.vi12.Student.MySubjects;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import college.root.vi12.AdminActivities.TimeTable.PreTimeTableSetup;
import college.root.vi12.MainActivity;
import college.root.vi12.Miscleneous.Utils;
import college.root.vi12.NetworkTasks.NetworkUtils;
import college.root.vi12.R;
import college.root.vi12.Student.Realm.Student_profile;
import college.root.vi12.Miscleneous.Toast;
import io.realm.Realm;
import io.realm.RealmList;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MySubjectsActivity extends AppCompatActivity {

    RecyclerView mrecyclerView;
    MySubjects mysubjects ;
    SubjectList subjectList ;
    Realm realm;
    NetworkUtils networkUtils;
    Toast toast;
    Student_profile profile;
    ProgressDialog dialog;
    RealmList<MySubjects> subjectsRealmList;
    String TAG = "Test";
    Socket socket;
    String count = "";
    ArrayList<MySubjects> list ;
    TextView tvDept, tvSem, tvYear;
    public ArrayList<String> codes;
    static Button btnSubmitSubjects;

    private  void initializeViews(){

        tvDept = (TextView) findViewById(R.id.tvSubDept);
        tvSem = (TextView) findViewById(R.id.tvSubSem);
        tvYear = (TextView) findViewById(R.id.tvSubYear);

        mrecyclerView = (RecyclerView) findViewById(R.id.recyclerForSubjects);
        mrecyclerView.hasFixedSize();
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mrecyclerView.setLayoutManager(layoutManager);

        dialog = new ProgressDialog(MySubjectsActivity.this); // Progress Dialog
        dialog.setCanceledOnTouchOutside(false);

        SubjectsAdapter adapter = new SubjectsAdapter(list , getApplicationContext());
        mrecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        btnSubmitSubjects = (Button) findViewById(R.id.btnSubmitSubjects);
        codes = new ArrayList<>();


    }

    private void initializeClasses(){

        networkUtils = new NetworkUtils();
        toast = new Toast();
        list = new ArrayList<>();


    }

    private void initializeRealmStuff(){

        realm = Realm.getDefaultInstance();

        subjectList = realm.where(SubjectList.class).findFirst();

        profile = realm.where(Student_profile.class).findFirst();
        if (profile == null){
            startActivity(new Intent(MySubjectsActivity.this, MainActivity.class));
        }else {

            tvYear.setText(profile.getYear());
            tvDept.setText(profile.getBranch());
            tvSem.setText(profile.getSemester());
        }


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_subjects);

        initializeViews();
        initializeClasses();
        initializeRealmStuff();
        realm = Realm.getDefaultInstance();

        profile = realm.where(Student_profile.class).findFirst();

        dialog.setTitle("Fetching Subjects....");
//        dialog.show();
        btnSubmitSubjects.setVisibility(View.INVISIBLE);


        if (profile.isSubjectsFetched()){
            // fetch locally stored subjects

           list = new ArrayList<>();
           RealmList<MySubjects> listOfSubjects = subjectList.getSubjectsRealmList();

          for (int i=0 ; i< listOfSubjects.size()  ; i++){

              list.add(listOfSubjects.get(i));
              SubjectsAdapter adapter = new SubjectsAdapter(list , MySubjectsActivity.this);
              mrecyclerView.setAdapter(adapter);
              adapter.notifyDataSetChanged();


          }



        }else {
            if (Utils.isNetWorkAvailable(MySubjectsActivity.this)){

                // fetch subjects from server
                JSONObject jsonObject = new JSONObject();

                try {
                    socket = networkUtils.get();

                    jsonObject.put("GrNumber" ,profile.getBranch()+ profile.getYear()+""+profile.getSemester());
                    jsonObject.put("collectionName" , "Subjects");
                    Log.d(TAG, "onCreateView: object emitted  is "+jsonObject);

                    socket.emit("getAllData", jsonObject.toString());
                    socket.on("Result" ,SubjectsListener);

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }


            }else{

                Utils.toast(MySubjectsActivity.this , "No Internet Connection");

            }

        }









        btnSubmitSubjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // send a socket to server containing the registered subject info


                Log.d(TAG, "onClick: Submit button clicked .... ");
                Log.d(TAG, "onClick: Contents of Codes array list are ");

                AlertDialog.Builder builder = new AlertDialog.Builder(MySubjectsActivity.this);
                builder.setTitle("Confirm Registration?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        for (  int i = 0; i<list.size(); i++){
//                            Log.i("Code is : ", ""+ codes.get(i));
                        }

                        JSONObject obj = new JSONObject();
                        try {



                            if (profile == null){
                                Log.d(TAG, "onClick: profile is n null");
                            }else{

                                obj.put("Array",codes.toString());
                                obj.put("grNumber" , profile.getGrno());
                                obj.put("div" , profile.getDiv());

                                networkUtils = new NetworkUtils();
                                networkUtils.emitSocket("RegisterStudent",obj);
                                toast.showToast(MySubjectsActivity.this , "registered Successfully...");
                             //   networkUtils.disconnectSocketAsync();
                                networkUtils.listener("RegisterStudent" , MySubjectsActivity.this , MySubjectsActivity.this, toast); //success  listener

                                // set subjects fetched to true

                                realm.beginTransaction();
                                profile.setSubjectsFetched(true);
                                realm.commitTransaction();
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        realm.copyToRealmOrUpdate(profile);
                                    }
                                });
                            }





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
        });


    }// onCreate

        private  Emitter.Listener SubjectsListener = new Emitter.Listener() {
            @Override
            public void call(Object... args) {


                if (args[0].equals("0")){
                    Log.d(TAG, "call: no data found");
                    runOnUiThread(new Runnable() {
                                      @Override
                                      public void run() {
                                          dialog.dismiss();

                                          android.widget.Toast.makeText(MySubjectsActivity.this, "Sorry, could not find corresponding data", android.widget.Toast.LENGTH_SHORT).show();

                                      }
                    });

                }else {
                    subjectsRealmList = new RealmList<>();

                    Log.d(TAG, "call: inside getResult listener");

                    final org.json.JSONArray[] array = {(org.json.JSONArray) args[0]};
                    Log.d(TAG, "call: array is "+array);

                    try {
                        final JSONObject obj = (JSONObject) array[0].get(0);
                        Log.d(TAG, "call: obj is "+obj);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {


                                    realm = Realm.getDefaultInstance();
                                    int i=0;
                                    // count[0] = obj.getString("SubjectCount");
                                    count = obj.getString("SubjectCount");
                                    Log.d(TAG, "run: count is "+obj.getString("SubjectCount"));
                                    Log.d(TAG, "run: count variavle is "+count);
                                    String[] name = new String[Integer.parseInt(count)];

                                    String[] codes = new String[Integer.parseInt(count)];
                                    Log.d(TAG, "run: String array of name declared with count "+count);


                                    Iterator<?> keys = obj.keys();
                                    Log.d(TAG, "run: kes are "+keys);

                                    while( keys.hasNext() ) {
                                        String key = (String) keys.next();
                                        Log.d(TAG, "run: key is "+key);
                                        Log.d(TAG, "run: and value of key is "+obj.getString(key));

                                        if (key.equals("_id") || key.equals("SubjectCount")){
                                            // do not store it in name array
                                        }else {



                                            mysubjects = new MySubjects();

                                            realm.beginTransaction();
                                            mysubjects.setSubjectName(key);
                                            mysubjects.setSubjectCode(obj.getString(key));
                                            realm.commitTransaction();
                                            realm.executeTransaction(new Realm.Transaction() {
                                                @Override
                                                public void execute(Realm realm) {
                                                    realm.copyToRealmOrUpdate(mysubjects);
                                                    Log.d(TAG, "execute: new Subject added...");
                                                }
                                            });
                                            list.add(mysubjects);
                                            subjectsRealmList.add(mysubjects);
                                            Log.d(TAG, "run: object added in realm list "+subjectsRealmList);

                                            name[i] = key;  // store the subject names in string array
                                            codes[i] = obj.getString(key);
                                            i++;

                                        }
                                    }// end of while loop

                                    for ( i = 0; i<list.size(); i++){
                                        Log.i("Member name: ", ""+ list.get(i));
                                    }
                                    // Log.d(TAG, "run:  Array list here contains : "+list);
                                    SubjectsAdapter adapter = new SubjectsAdapter(list , MySubjectsActivity.this);
                                    mrecyclerView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();




                                    Log.d(TAG, "run: names are "+ Arrays.toString(name));
                                    Log.d(TAG, "run: codes are "+Arrays.toString(codes));
                                    Log.d(TAG, "run: outside the loop ... realm list contains "+subjectsRealmList);

                                    subjectList = new SubjectList();
                                    subjectList = realm.where(SubjectList.class).findFirst();

                                    if (subjectList == null){

                                        Log.d(TAG, "run: list is empty");
                                        // list is empty
                                        subjectList = new SubjectList();
                                        realm.beginTransaction();

                                        subjectList.setCount(count);
                                        subjectList.setGrNumber(profile.getGrno());
                                        subjectList.setSubjectsRealmList(subjectsRealmList);

                                        realm.commitTransaction();

                                        realm.executeTransaction(new Realm.Transaction() {
                                            @Override
                                            public void execute(Realm realm) {
                                                realm.copyToRealmOrUpdate(subjectList);
                                                Log.d(TAG, "execute: Subject List stored successfully ");
                                            }
                                        });

                                    }else {
                                        Log.d(TAG, "run: list isnt empty");

                                    }


                                    subjectsRealmList = subjectList.getSubjectsRealmList();
                                    for ( i=0 ;i<subjectsRealmList.size() ; i++){
                                        Log.d(TAG, "run: Subject list is "+subjectsRealmList.get(i));
                                    }
                                    btnSubmitSubjects.setVisibility(View.VISIBLE);
                                    dialog.cancel();


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }






                            }
                        });



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            }
        };



    }
