package college.root.vi12.Faculty;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import college.root.vi12.AdminActivities.TimeTableDisplayActivity;
import college.root.vi12.Faculty.Realm.FacultyProfileRealm;
import college.root.vi12.Faculty.Realm.FacultySubjRealmObj;
import college.root.vi12.Faculty.Realm.FacultyTTRealmObject;
import college.root.vi12.Miscleneous.Toast;
import college.root.vi12.NetworkTasks.CheckNetwork;
import college.root.vi12.NetworkTasks.NetworkUtils;
import college.root.vi12.R;
import college.root.vi12.StudentProfile.EditProfileActivity;
import college.root.vi12.StudentProfile.Realm.Student_profile;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class FacultyProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
    {


    DrawerLayout drawer;
    Uri imageuri;
    Realm realm;
    FacultyProfileRealm userProfile;
    TextView tvname, tvsurname, tvyear, tvdiv, tvbranch;
    CircleImageView profilePic;
    int GALLERY_REQUEST = 1;
    Student_profile profile;
    String TAG = "Test";
    NetworkUtils networkUtils;
    Toast toast;
    FacultyTTRealmObject realmObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_profile);


        SharedPreferences sharedPreferences = getSharedPreferences("flag", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        drawer = (DrawerLayout) findViewById(R.id.facultydrawerLayout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.facultyToolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.app_name, R.string.app_name);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.facultynav_view);
        assert navigationView != null;
        boolean flag = sharedPreferences.getBoolean("flag", true);
        Log.d("FLAG:", String.valueOf(flag));
        navigationView.setNavigationItemSelectedListener(FacultyProfileActivity.this);
        networkUtils = new NetworkUtils();
        toast = new Toast();


        realm = Realm.getDefaultInstance();
        profilePic = (CircleImageView) findViewById(R.id.facultyprofilepic);
        tvname = (TextView) findViewById(R.id.facultyname);
        tvsurname = (TextView) findViewById(R.id.facultySurname);
        tvbranch = (TextView) findViewById(R.id.facultybranch);


        userProfile = realm.where(FacultyProfileRealm.class).findFirst();
        // realm.beginTransaction();

        if (userProfile == null) {
            Log.d(TAG, "onCreate: user profile is null ");
        } else {
            tvname.setText(userProfile.getName());
            tvsurname.setText(userProfile.getSurname());
            tvyear.setText(userProfile.getYear());
            tvdiv.setText(userProfile.getDiv());
            Log.d(TAG, "onCreate: name is " + userProfile.getName());
            Log.d(TAG, "onCreate: surname is " + userProfile.getSurname());


            if (flag) {
                imageuri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                        "://" + getResources().getResourcePackageName(R.drawable.profile_def)
                        + '/' + getResources().getResourceTypeName(R.drawable.profile_def) + '/' + getResources().getResourceEntryName(R.drawable.profile_def));

                profilePic.setImageURI(imageuri);

            }
            if (!flag) {
                Log.d("FLAG1:", String.valueOf(flag));

                if (userProfile.getImagePath() != null)
                    profilePic.setImageURI(Uri.parse(userProfile.getImagePath()));
            }

            profile = new Student_profile();


        }
    }


        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();

            if( id == R.id.profile) {

                Intent intent = new Intent(FacultyProfileActivity.this , EditProfileActivity.class);
                startActivity(intent);

            }

            if( id == R.id.subjects) {

                startActivity(new Intent(this , FacultySubjectsActivity.class));
            }

            if (id == R.id.timetable){


                if (CheckNetwork.isNetWorkAvailable(FacultyProfileActivity.this)){
                    // load TT from server
                    loadTTFromServer();
                }else{
                    // load local realm object
                    loadLocalTT();
                }
            }


            drawer.closeDrawer(GravityCompat.START);
            return true;
        }



    public void loadLocalTT(){

        realm = Realm.getDefaultInstance();
        realmObject = realm.where(FacultyTTRealmObject.class).findFirst();
        if (realmObject != null) {

            try {
                JSONObject object = new JSONObject(realmObject.getJsonTT());

                Intent i1 = new Intent(FacultyProfileActivity.this,TimeTableDisplayActivity.class);
                i1.putExtra("object",object.toString());
                i1.putExtra("id","123");
                i1.putExtra("User" , "Faculty");
                startActivity(i1);





            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    public void loadTTFromServer(){

        FacultySubjRealmObj realmSubjects ;
        Realm realm = Realm.getDefaultInstance();
        realmSubjects = realm.where(FacultySubjRealmObj.class).findFirst();
        JSONArray jsonArray = new JSONArray();
        Socket socket;
        networkUtils = new NetworkUtils();

        if (realmSubjects != null){

            String str1 = realmSubjects.getJsonSubjObj();
            try {
                JSONObject subjJson = new JSONObject(str1);
                String dept = "Computer";
                String sem = "Sem2";

                String[] objects = {"SE", "TE", "BE"};
                for (String str :  objects ) {

                    Log.d(TAG, "parseJson: str is" + str);
                    JSONArray array = subjJson.getJSONArray(str);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);

                        String div = obj.getString("Div");
                        String token = dept+str+sem+div;
                        if(!arrayContainstoken(jsonArray, token)){
                            jsonArray.put(token);

                        }
                    }
                }

                Log.d(TAG, "onNavigationItemSelected: array of tokens is "+jsonArray);
                JSONObject object = new JSONObject();
                object.put("ArrayOftokens", jsonArray);
                socket = networkUtils.get();
                socket.emit("fetchFacultyTT", object.toString());
                Log.d(TAG, "onNavigationItemSelected: data sent");
                socket.on("facultyFetchTTResult", TTResultListener);

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }


        }else{
            Log.d(TAG, "onNavigationItemSelected: Subject Realm object is null");
        }




    }

    public void editPicture(View view){


        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, GALLERY_REQUEST);
    }

    private boolean arrayContainstoken(JSONArray array, String token) throws JSONException {

            for (int i=0 ; i<array.length(); i++){
                if (array.getString(i).equals(token)){
                    return true;
                }
            }


            return false;
        }


        Emitter.Listener TTResultListener = new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                JSONObject object = (JSONObject) args[0];

                Log.d(TAG, "call: object is "+object);


                realmObject = new FacultyTTRealmObject();
                realm = Realm.getDefaultInstance();
                realmObject = realm.where(FacultyTTRealmObject.class).findFirst();
                if (realmObject == null){

                    realmObject = new FacultyTTRealmObject();
                    realmObject.setId("123");
                    realmObject.setJsonTT(object.toString());
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.copyToRealmOrUpdate(realmObject);

                        }
                    });



                }else{
                    realmObject.setId("123");
                    realmObject.setJsonTT(object.toString());
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.copyToRealmOrUpdate(realmObject);

                        }
                    });

                }

                Intent i1 = new Intent(FacultyProfileActivity.this,TimeTableDisplayActivity.class);
                i1.putExtra("object",object.toString());
                i1.putExtra("id","123");
                i1.putExtra("User" , "Faculty");
                startActivity(i1);



            }
        };
    }
