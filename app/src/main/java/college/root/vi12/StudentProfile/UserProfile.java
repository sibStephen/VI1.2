package college.root.vi12.StudentProfile;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import college.root.vi12.AdminActivities.TimeTableDisplayActivity;
import college.root.vi12.MySubjects.MySubjectsActivity;
import college.root.vi12.NetworkTasks.CheckNetwork;
import college.root.vi12.NetworkTasks.JsontoSend;
import college.root.vi12.NetworkTasks.NetworkUtils;
import college.root.vi12.R;
import college.root.vi12.Miscleneous.Toast;
import college.root.vi12.StudentProfile.Student_TT.TTRealmObject;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.socket.client.Ack;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class UserProfile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    Uri imageuri;
    Realm realm;
    Student_profile userProfile;
    TextView tvname,tvsurname,tvyear,tvdiv,tvbranch,tvgrno , tvAttendance;
    CircleImageView profilePic;
    int GALLERY_REQUEST = 1;
    Uri mImageUri;
    Student_profile profile;
    String TAG = "Test";
    Socket socket;
    NetworkUtils networkUtils;
    Toast toast;
    String ttID;

    TTRealmObject realmObject ;



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_profile, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case R.id.sync_server:

                Log.d(TAG, "onOptionsItemSelected: sync server called");

                CheckNetwork checkNetwork = new CheckNetwork();
                if (!checkNetwork.isNetWorkAvailable(UserProfile.this)){

                    android.widget.Toast.makeText(UserProfile.this , "No Network available", android.widget.Toast.LENGTH_SHORT).show();

                    Log.d(TAG, "onOptionsItemSelected: no data to sync");
                }else {


                    NetworkUtils networkUtils = new NetworkUtils();

                    try {
                        socket = networkUtils.get();
                        Log.d(TAG, "onOptionsItemSelected: emitting socket");
                        socket.emit("Ping", "Ping message", new Ack() {
                            @Override
                            public void call(Object... args) {

                                if (args[0]=="1"){
                                    // Sync now !!
                                    Log.d(TAG, "call: agrs is "+args[0]);
                                    RealmResults<JsontoSend> realmResults = realm.where(JsontoSend.class).findAll();

                                    if (realmResults.isEmpty()){

                                        android.widget.Toast.makeText(UserProfile.this, "No data to sync", android.widget.Toast.LENGTH_SHORT).show();

                                        Log.d(TAG, "call: no data");
                                    }else {

                                        for (int i=0; i<realmResults.size() ; i++){
                                            // send data to server

                                            NetworkUtils utils = new NetworkUtils();
                                            JsontoSend jsontoSend = realmResults.get(i);
                                            Log.d(TAG, "call: jsondata is "+jsontoSend);
                                            try {
                                                Log.d(TAG, "onOptionsItemSelected: Collection name is" +jsontoSend.getCollection());
                                                Log.d(TAG, "onOptionsItemSelected: JsonObject is "+new JSONObject(jsontoSend.getJson()));
                                                utils.emitSocket(jsontoSend.getCollection(), new JSONObject(jsontoSend.getJson()));
                                                utils.listener("Allinfo" , UserProfile.this, UserProfile.this, new Toast());
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }


                                        }

                                    }



                                }

                            }
                        });



                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }





                }





        }


        return super.onOptionsItemSelected(item);


    }

        @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);


            //TODO add a student profile field named as 1) isTTLoaded 2) areSubjectsLoaded
            // TODO if internet connection exists then load TT from server else load locally.
            setContentView(R.layout.activity_user_profile);
            try {

                //ActionBar bar = getActionBar();

                //bar.setTitle("Profile");
            } catch (NullPointerException e) {
                Log.d(TAG, "onCreate: " + e.getMessage());
            }
            SharedPreferences sharedPreferences = getSharedPreferences("flag", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                    R.string.app_name, R.string.app_name);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            assert navigationView != null;
            navigationView.setNavigationItemSelectedListener(UserProfile.this);
            boolean flag = sharedPreferences.getBoolean("flag", true);
            Log.d("FLAG:", String.valueOf(flag));
            networkUtils = new NetworkUtils();
            toast = new Toast();

            RealmConfiguration config = new RealmConfiguration.Builder(this).schemaVersion(4).deleteRealmIfMigrationNeeded().build();
            realm.setDefaultConfiguration(config);

            realm = Realm.getDefaultInstance();


            try {
                socket = networkUtils.get();

            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "onCreate: cannot initialize socket !!");

            }

            profilePic = (CircleImageView) findViewById(R.id.profilepic);
            tvname = (TextView) findViewById(R.id.name);
            tvsurname = (TextView) findViewById(R.id.surname);
            tvyear = (TextView) findViewById(R.id.year);
            tvdiv = (TextView) findViewById(R.id.div);
            tvbranch = (TextView) findViewById(R.id.branch);

            userProfile = realm.where(Student_profile.class).findFirst();
            // realm.beginTransaction();

            if (userProfile == null) {
                Log.d(TAG, "onCreate: user profile is null ");
            } else {
                tvname.setText(userProfile.getName());
                tvsurname.setText(userProfile.getSurname());
                tvyear.setText(userProfile.getYear());
                tvdiv.setText(userProfile.getDiv());
                Log.d(TAG, "onCreate: name is "+userProfile.getName());
                Log.d(TAG, "onCreate: surname is "+userProfile.getSurname());



            if (flag) {
                imageuri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                        "://" + getResources().getResourcePackageName(R.drawable.profile_def)
                        + '/' + getResources().getResourceTypeName(R.drawable.profile_def) + '/' + getResources().getResourceEntryName(R.drawable.profile_def));

                profilePic.setImageURI(imageuri);

            }
//        tvbranch.setText(userProfile.getBranch());
            if (!flag) {
                Log.d("FLAG1:", String.valueOf(flag));

                if (userProfile.getImagePath() != null)
                    profilePic.setImageURI(Uri.parse(userProfile.getImagePath()));
            }

            // tvgrno.setText(userProfile.getGrno());
            profile = new Student_profile();


            try {
                // socket = networkUtils.get();
                JSONObject o = new JSONObject();
                o.put("GrNumber", userProfile.getGrno());


            } catch (JSONException e) {
                e.printStackTrace();
            }


            //realm.commitTransaction();


        }

    }

    public void editPicture(View view){

       // Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        //galleryIntent.setType("image/*");
        //startActivityForResult(galleryIntent, GALLERY_REQUEST);

        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, GALLERY_REQUEST);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

       SharedPreferences sharedPreferences = getSharedPreferences("flag", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            mImageUri = data.getData();
            profilePic.setImageURI(mImageUri);

            profile = realm.where(Student_profile.class).findFirst();
            realm.beginTransaction();
            profile.setImagePath(String.valueOf(mImageUri));
            editor.putBoolean("flag",false);
            editor.apply();
            editor.commit();
            realm.commitTransaction();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(profile);

                }
            });

           // realPath = getRealPathFromURI(this, data.getData());

        }

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();



        if( id == R.id.profile) {

            Intent intent = new Intent(UserProfile.this , EditProfileActivity.class);
            startActivity(intent);
            //finish();

        }

        if( id == R.id.subjects) {

            startActivity(new Intent(this , MySubjectsActivity.class));
        }

        if (id == R.id.timetable){
            profile = realm.where(Student_profile.class).findFirst();

            if (profile==null)
            {
                Log.d(TAG, "onNavigationItemSelected: profile is null");
            }

             ttID = profile.getBranch()+profile.getYear()+profile.getSemester()+profile.getDiv();

            NetworkUtils networkUtils = new NetworkUtils();

            try {
                socket = networkUtils.get();
                JSONObject object = new JSONObject();
                object.put("GrNumber", ttID );
                object.put("collectionName", "Load_Time_Table");
              //  networkUtils.emitSocket("getAllData" , object);

                socket.emit("getAllData" , object.toString());
                socket.on("Result" , ttlistener);




            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


           // startActivity(new Intent(this , TimeTableDisplayActivity.class));
        }

            drawer.closeDrawer(GravityCompat.START);
        return true;
    }



   private Emitter.Listener ttlistener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {


            Log.d(TAG, "call: Result is "+args[0]);
            JSONArray array = (JSONArray) args[0];

            Log.d(TAG, "call: array is "+array);
            try {
                JSONObject obj = array.getJSONObject(0);
                Log.d(TAG, "call: obj is "+obj);


                realm = Realm.getDefaultInstance();
                realmObject  = realm.where(TTRealmObject.class).findFirst();
                if (realmObject == null){

                    realmObject = new TTRealmObject();
                    realmObject.setId(obj.getString("_id"));
                    realmObject.setJsonTTObject(obj.toString());
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {

                            realm.copyToRealmOrUpdate(realmObject);
                        }
                    });

                }else{

                    realm.beginTransaction();
                    realmObject.setId(obj.getString("_id"));
                    realmObject.setJsonTTObject(obj.toString());
                    realm.commitTransaction();

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {

                            realm.copyToRealmOrUpdate(realmObject);
                        }
                    });


                }


                Intent i1 = new Intent(UserProfile.this,TimeTableDisplayActivity.class);

                i1.putExtra("object",realmObject.getJsonTTObject());
                i1.putExtra("id",ttID);
                startActivity(i1);

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };






}
