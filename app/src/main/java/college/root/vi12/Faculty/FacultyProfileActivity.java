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

import college.root.vi12.Miscleneous.Toast;
import college.root.vi12.MySubjects.MySubjectsActivity;
import college.root.vi12.NetworkTasks.NetworkUtils;
import college.root.vi12.R;
import college.root.vi12.StudentProfile.EditProfileActivity;
import college.root.vi12.StudentProfile.Student_TT.TTRealmObject;
import college.root.vi12.StudentProfile.Student_profile;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.socket.client.Socket;

public class FacultyProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
    {


    DrawerLayout drawer;
    Uri imageuri;
    Realm realm;
    FacultyDB userProfile;
    TextView tvname, tvsurname, tvyear, tvdiv, tvbranch, tvgrno, tvAttendance;
    CircleImageView profilePic;
    int GALLERY_REQUEST = 1;
    Uri mImageUri;
    Student_profile profile;
    String TAG = "Test";
    Socket socket;
    NetworkUtils networkUtils;
    Toast toast;
    String ttID;

    TTRealmObject realmObject;

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


        userProfile = realm.where(FacultyDB.class).findFirst();
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

    public void editPicture(View view){


        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, GALLERY_REQUEST);
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

        }


            drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
