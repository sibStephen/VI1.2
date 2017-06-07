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
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import college.root.vi12.MySubjects.MySubjects;
import college.root.vi12.MySubjects.MySubjectsActivity;
import college.root.vi12.MySubjects.SubjectList;
import college.root.vi12.NetworkTasks.NetworkUtils;
import college.root.vi12.R;
import college.root.vi12.Toast;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.socket.client.Socket;

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
    MySubjects mysubjects ;
    SubjectList subjectList ;

    RealmList<MySubjects> subjectsRealmList;
    String count = "";




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

        RealmConfiguration config = new RealmConfiguration.Builder(this).schemaVersion(4).deleteRealmIfMigrationNeeded().build();
        realm.setDefaultConfiguration(config);

        realm = Realm.getDefaultInstance();



        try {
            socket = networkUtils.get();
            //  socket = networkUtils.getSocketAsync();
          //  networkUtils.listener("test" , UserProfile.this , getApplicationContext(), toast);

        }catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, "onCreate: cannot initialize socket !!");

        }

       // socket.on("AttendanceResult" , AttendanceHandler);

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



        try {
           // socket = networkUtils.get();
            JSONObject o = new JSONObject();
            o.put("GrNumber" , userProfile.getGrno());






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
            //finish();

        }

        if( id == R.id.subjects) {

            startActivity(new Intent(this , MySubjectsActivity.class));


        }

            drawer.closeDrawer(GravityCompat.START);
        return true;
    }









}
