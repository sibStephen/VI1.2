package college.root.vi12.MySubjects;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import college.root.vi12.NetworkTasks.NetworkUtils;
import college.root.vi12.R;
import college.root.vi12.StudentProfile.Student_profile;
import college.root.vi12.Toast;
import io.realm.Realm;
import io.realm.RealmList;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MySubjectsActivity extends AppCompatActivity {

    RecyclerView mrecyclerView;
    RecyclerView.LayoutManager layoutManager;
    MySubjects mysubjects ;
    SubjectList subjectList ;
    Student_profile userProfile;
    Realm realm;
    NetworkUtils networkUtils;
    Toast toast;
    Student_profile profile;

    RealmList<MySubjects> subjectsRealmList;
    String TAG = "Test";
    Socket socket;

    String count = "";
    ArrayList<MySubjects> list ;

   public ArrayList<String> codes;
    Button btnSubmitSubjects;

    private  void initializeViews(){

        mrecyclerView = (RecyclerView) findViewById(R.id.recyclerForSubjects);
        mrecyclerView.hasFixedSize();
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mrecyclerView.setLayoutManager(layoutManager);

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


        userProfile = realm.where(Student_profile.class).findFirst();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_subjects);

        initializeViews();
        initializeClasses();
        initializeRealmStuff();

        try {
            socket = networkUtils.get();
            Log.d(TAG, "onCreate: Socket initialized properly");
        }catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, "onCreate: cannot initialize socket !!");

        }


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("GrNumber" , userProfile.getYear()+""+userProfile.getBranch()+""+userProfile.getSemester());
            jsonObject.put("collectionName" , "Subjects");

            Log.d(TAG, "onCreateView: object emitted  is "+jsonObject);

            socket.emit("getAllData", jsonObject.toString());
            socket.on("Result" ,SubjectsListener);




        } catch (JSONException e) {
            e.printStackTrace();
        }


        btnSubmitSubjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // send a socket to server containing the registered subject info


                Log.d(TAG, "onClick: Submit button clicked .... ");
                Log.d(TAG, "onClick: Contents of Codes array list are ");

                for (  int i = 0; i<list.size(); i++){
                    Log.i("Code is : ", ""+ codes.get(i));
                }

                JSONObject obj = new JSONObject();
                try {

                    realm = Realm.getDefaultInstance();

                    profile = realm.where(Student_profile.class).findFirst();


                    if (profile == null){
                        Log.d(TAG, "onClick: profile is n null");
                    }else{

                        obj.put("Array",codes.toString());
                        obj.put("grNumber" , profile.getGrno());

                        networkUtils = new NetworkUtils();
                        networkUtils.emitSocket("RegisterStudent",obj);
                        networkUtils.disconnectSocketAsync();
                        networkUtils.listener("RegisterStudent" , MySubjectsActivity.this , MySubjectsActivity.this, toast); //success  listener


                    }





                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });


    }// onCreate

        private  Emitter.Listener SubjectsListener = new Emitter.Listener() {
            @Override
            public void call(Object... args) {


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
                                    subjectList.setGrNumber(userProfile.getGrno());
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


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }






                        }
                    });



                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };



    }
