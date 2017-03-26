package college.root.vi12.StudentProfile;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.EmbossMaskFilter;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.JSONArray;

import java.net.URISyntaxException;

import college.root.vi12.NetworkUtils;
import college.root.vi12.R;
import college.root.vi12.Subjects;
import college.root.vi12.Toast;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.socket.client.Ack;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class UserProfile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    Uri imageuri;
    Realm realm;
    String realPath;
    Student_profile userProfile;
    int uid ;
    TextView tvname,tvsurname,tvyear,tvdiv,tvbranch,tvgrno , tvAttendance;
    CircleImageView profilePic;
    int GALLERY_REQUEST = 1;
    Uri mImageUri;
    Student_profile profile;
    String TAG = "Test";
    Socket socket;
    NetworkUtils networkUtils;
    Toast toast;
    Subjects subjects;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_profile);
        SharedPreferences sharedPreferences = getSharedPreferences("flag", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar1);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.app_name, R.string.app_name);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(UserProfile.this);
        boolean  flag=sharedPreferences.getBoolean("flag", true);
        Log.d("FLAG:", String.valueOf(flag));
        networkUtils = new NetworkUtils();
        toast = new Toast();
        subjects = new Subjects();

        RealmConfiguration config = new RealmConfiguration.Builder(this).schemaVersion(4).deleteRealmIfMigrationNeeded().build();
        realm.setDefaultConfiguration(config);

        realm = Realm.getDefaultInstance();

        subjects = realm.where(Subjects.class).findFirst();


        try {
            socket = networkUtils.get();
            //  socket = networkUtils.getSocketAsync();
          //  networkUtils.listener("test" , UserProfile.this , getApplicationContext(), toast);

        }catch (Exception e){

        }

        socket.on("AttendanceResult" , AttendanceHandler);
//

      profilePic = (CircleImageView)findViewById(R.id.profilepic);
        tvname=(TextView)findViewById(R.id.name);
        tvsurname=(TextView)findViewById(R.id.surname);
        tvyear=(TextView)findViewById(R.id.year);
        tvdiv=(TextView)findViewById(R.id.div);
        tvbranch=(TextView)findViewById(R.id.branch);
        tvgrno=(TextView)findViewById(R.id.grno);
        tvAttendance = (TextView)findViewById(R.id.tvAtten);

       // uid=userProfile.getUid();
       userProfile = realm.where(Student_profile.class).findFirst();
        // realm.beginTransaction();

        if (userProfile == null){
            Log.d(TAG, "onCreate: user profile is null ");
        }else {
            tvname.setText(userProfile.getName());
            tvsurname.setText(userProfile.getSurname());
            tvyear.setText(userProfile.getYear());
            tvdiv.setText(userProfile.getDiv());
        }


        if(flag)
        {
            imageuri=Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                    "://" + getResources().getResourcePackageName(R.drawable.profile_def)
                    + '/' + getResources().getResourceTypeName(R.drawable.profile_def) + '/' + getResources().getResourceEntryName(R.drawable.profile_def) );

            profilePic.setImageURI(imageuri);

        }
//        tvbranch.setText(userProfile.getBranch());
        if(!flag) {
           Log.d("FLAG1:", String.valueOf(flag));
            profilePic.setImageURI(Uri.parse(userProfile.getImagePath()));
        }

        tvgrno.setText(userProfile.getGrno());
        profile =new Student_profile();
        tvAttendance.setText(subjects.getMyTotalAtendance());



        try {
           // socket = networkUtils.get();
            JSONObject o = new JSONObject();
            o.put("GrNumber" , userProfile.getGrno());
            socket.emit("AttendanceReq", o.toString(), new Ack() {
                @Override
                public void call(Object... args) {

                    boolean ack= (boolean)args[0];
                    if (ack){
                        toast.showToast(UserProfile.this , "Ack received");

                    }

                }
            });





        } catch (JSONException e) {
            e.printStackTrace();
        }


        //realm.commitTransaction();






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
            finish();

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private Emitter.Listener AttendanceHandler = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "call: In Attendance listener ");

            org.json.JSONArray array =(org.json.JSONArray) args[0];
            try {
                final JSONObject object = array.getJSONObject(0);
                Log.d(TAG, "call: result obtained is "+object);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            realm = Realm.getDefaultInstance();


                            realm.beginTransaction();
                            subjects.setSubj1Att((String) object.get(subjects.getSubj1Name()));
                            subjects.setSubj2Att((String) object.get(subjects.getSubj2Name()));
                        subjects.setSubj3Att((String) object.get(subjects.getSubj3Name()));
                        subjects.setSubj4Att((String) object.get(subjects.getSubj4Name()));
                        subjects.setSubj5Att((String) object.get(subjects.getSubj5Name()));

                        subjects.setSubj1Total(object.getString(subjects.getSubj1Name()+"_Total"));
                        subjects.setSubj2Total(object.getString(subjects.getSubj2Name()+"_Total"));
                        subjects.setSubj3Total(object.getString(subjects.getSubj3Name()+"_Total"));
                        subjects.setSubj4Total(object.getString(subjects.getSubj4Name()+"_Total"));
                        subjects.setSubj5Total(object.getString(subjects.getSubj5Name()+"_Total"));
                            realm.commitTransaction();

                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.copyToRealmOrUpdate(subjects);
                                    Log.d(TAG, "execute: Data updated successfully !!");
                                }
                            });

                          } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        int totalAtt = Integer.parseInt(subjects.getSubj1Total()) +
                                Integer.parseInt(subjects.getSubj2Total()) +
                                Integer.parseInt(subjects.getSubj3Total()) +
                                Integer.parseInt(subjects.getSubj4Total()) +
                                Integer.parseInt(subjects.getSubj5Total()) ;

                        int currentAtt = Integer.parseInt(subjects.getSubj1Att())+
                                Integer.parseInt(subjects.getSubj2Att())+
                                Integer.parseInt(subjects.getSubj3Att())+
                                Integer.parseInt(subjects.getSubj4Att())+
                                Integer.parseInt(subjects.getSubj5Att());

                        float attendPercentage =((float) currentAtt*100)/(float)totalAtt;
                        float remainder = (currentAtt*100)%totalAtt;
                        float finalAtt = attendPercentage+remainder;
                        Log.d(TAG, "run: Curent attendance is "+currentAtt);
                        Log.d(TAG, "run: Total att is "+totalAtt);
                        Log.d(TAG, "run: aviad" + currentAtt/totalAtt);
                        Log.d(TAG, "call: Attendance % is "+ finalAtt + "%");
                        realm.beginTransaction();
                        subjects.setMyTotalAtendance(String.valueOf(attendPercentage));
                        realm.commitTransaction();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                realm.copyToRealmOrUpdate(subjects);
                            }
                        });

                        tvAttendance.setText(String.valueOf(attendPercentage));


                    }
                });


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

}
