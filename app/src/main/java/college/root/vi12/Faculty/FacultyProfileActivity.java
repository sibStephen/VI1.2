package college.root.vi12.Faculty;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
import college.root.vi12.StudentProfile.Student_TT.Realm.TTRealmObject;
import college.root.vi12.StudentProfile.UserProfile;
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
    FacultyProfileRealm profile;
    String TAG = "Test";
    NetworkUtils networkUtils;
    Toast toast;
    FacultyTTRealmObject realmObject;
    TextView tvFacdiv, tvFacyear, tvFaclec, tvFactime, tvFacLoc,tvFacdivnext, tvFacyearnext, tvFaclecnext,
            tvFactimenext, tvFacLocnext, tvFacDayNext;


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



        initializeViews();

        loadCurrentLectureUpdates();
        loadNextLectureUpdates();



        Log.d(TAG, "onCreate: calling TTnotif");
        TTNotification();

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

            profile = new FacultyProfileRealm();


        }
    }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.user_profile, menu);
            return true;

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {

            switch (item.getItemId()) {


                case R.id.settings:

                    CheckBox notifCheckBox = (CheckBox) item.getActionView();

                    profile = realm.where(FacultyProfileRealm.class).findFirst();

                    if (profile!= null){
                        if (profile.isNotificationEnabled()){
                            item.setChecked(true);
                        }else{
                            item.setChecked(false);
                        }
                    }
                    if (item.isChecked()) {
                        Log.d(TAG, "onOptionsItemSelected: checkbox was checked");
                        item.setChecked(false);
                        profile = realm.where(FacultyProfileRealm.class).findFirst();
                        if (profile != null) {
                            realm.beginTransaction();
                            profile.setNotificationEnabled(false);
                            realm.commitTransaction();
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.copyToRealmOrUpdate(profile);
                                }
                            });
                        }
                        android.widget.Toast.makeText(FacultyProfileActivity.this, "Notifications disabled..", android.widget.Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d(TAG, "onOptionsItemSelected: checkbox was not checked ");
                        item.setChecked(true);
                        profile = realm.where(FacultyProfileRealm.class).findFirst();
                        if (profile != null) {
                            realm.beginTransaction();
                            profile.setNotificationEnabled(true);
                            realm.commitTransaction();
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.copyToRealmOrUpdate(profile);
                                }
                            });
                        }
                        android.widget.Toast.makeText(FacultyProfileActivity.this, "Notifications Enabled..", android.widget.Toast.LENGTH_SHORT).show();

                    }


                    break;
            }




            return super.onOptionsItemSelected(item);
        }

        private void loadNextLectureUpdates() {

            String[] days = {"Monday" , "Tuesday" , "Wednesday" , "Thrusday" , "Friday", "Saturday"};
            int today = 0;
            boolean stillContinue = true;
            FacultyTTRealmObject ttobject;
            Log.d(TAG, ": in NextNecture");
            realm = Realm.getDefaultInstance();
            ttobject = realm.where(FacultyTTRealmObject.class).findFirst();
            if (ttobject == null){
                Log.d(TAG, "onReceive: ttobject was null");

            }else{
                // check the current task here....

                SimpleDateFormat sdf = new SimpleDateFormat("HH");
                String currentDateandTime = sdf.format(new Date());
                Log.d(TAG, "onReceive: current hour is "+currentDateandTime);
                //   if (Integer.parseInt(currentDateandTime)>8 && Integer.parseInt(currentDateandTime)< 7)
                {

                    // valid ... check lectures

                    SimpleDateFormat format = new SimpleDateFormat("EEEE");
                    Date d = new Date();
                    String dayOfTheWeek = format.format(d);
                    Log.d(TAG, "onReceive: today is "+dayOfTheWeek);
                    int cDay = getNumberFromWeek(days,dayOfTheWeek );

                    String  str = ttobject.getJsonTT();
                    try {
                        JSONObject object = new JSONObject(str);
                        Log.d(TAG, "onReceive: tt object is"+object);
                        JSONArray array = object.getJSONArray(dayOfTheWeek);
                        Log.d(TAG, "onReceive: tt for today is "+array);

                        for (int j= cDay ; j < days.length ;j++){
                            if (!stillContinue)
                                break;

                            array = object.getJSONArray(days[j]);
                            Log.d(TAG, "loadNextLectureUpdates: checking for day "+days[j]);


                            int length = array.length();
                            for (int i=0 ; i<length ; i++){
                                JSONObject currentObject = array.getJSONObject(i);


                                int time = Integer.parseInt(currentObject.getString("Time"));
                                int currentHour =  (Integer.parseInt(currentDateandTime ));
                                if (time > currentHour  ){


                                    tvFacdivnext.setText(currentObject.getString("Div"));
                                    tvFactimenext.setText(currentObject.getString("Time"));
                                    tvFaclecnext.setText(currentObject.getString("Subject"));
                                    tvFacLocnext.setText(currentObject.getString("Location"));
                                    tvFacyearnext.setText(currentObject.getString("Year"));
                                    tvFacDayNext.setText(days[j]);
                                    Log.d(TAG, "TTNotification: current lecture is"+currentObject);
                                    stillContinue = false;
                                    break;
                                }else{
                                    Log.d(TAG, "TTNotification: No lecture in this slot");

                                }


                            }


                        }



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }



        }

        private int getNumberFromWeek(String[] days, String dayOfTheWeek) {


            for (int i=0; i<days.length ; i++){
                if (days[i].equals(dayOfTheWeek)){
                    return  i;
                }
            }
            return 0;

        }

        private void loadCurrentLectureUpdates() {



            FacultyTTRealmObject ttobject;
            Log.d(TAG, "TTNotification: in TTNotificaton");
            realm = Realm.getDefaultInstance();
            ttobject = realm.where(FacultyTTRealmObject.class).findFirst();
            if (ttobject == null){
                Log.d(TAG, "onReceive: ttobject was null");

            }else{
                // check the current task here....

                SimpleDateFormat sdf = new SimpleDateFormat("HH");
                String currentDateandTime = sdf.format(new Date());
                Log.d(TAG, "onReceive: current hour is "+currentDateandTime);
                //   if (Integer.parseInt(currentDateandTime)>8 && Integer.parseInt(currentDateandTime)< 7)
                {

                    // valid ... check lectures

                    SimpleDateFormat format = new SimpleDateFormat("EEEE");
                    Date d = new Date();
                    String dayOfTheWeek = format.format(d);
                    Log.d(TAG, "onReceive: today is "+dayOfTheWeek);
                    String  str = ttobject.getJsonTT();
                    try {
                        JSONObject object = new JSONObject(str);
                        Log.d(TAG, "onReceive: tt object is"+object);
                        JSONArray array = object.getJSONArray(dayOfTheWeek);
                        Log.d(TAG, "onReceive: tt for today is "+array);

                        int length = array.length();
                        for (int i=0 ; i<length ; i++){
                            JSONObject currentObject = array.getJSONObject(i);


                            int time = Integer.parseInt(currentObject.getString("Time"));
                            if (time == Integer.parseInt(currentDateandTime)){


                                tvFacdiv.setText(currentObject.getString("Div"));
                                tvFactime.setText(currentObject.getString("Time"));
                                tvFaclec.setText(currentObject.getString("Subject"));
                                tvFacLoc.setText(currentObject.getString("Location"));
                                tvFacyear.setText(currentObject.getString("Year"));


                                Log.d(TAG, "TTNotification: current lecture is"+currentObject);
                            }else{
                                Log.d(TAG, "TTNotification: No lecture in this slot");

                            }


                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }



        }

        private void initializeViews() {

            profilePic = (CircleImageView) findViewById(R.id.facultyprofilepic);
            tvname = (TextView) findViewById(R.id.facultyname);
            tvsurname = (TextView) findViewById(R.id.facultySurname);
            tvbranch = (TextView) findViewById(R.id.facultybranch);

            tvFacdiv = (TextView)findViewById(R.id.tvFacDiv);
            tvFacyear = (TextView)findViewById(R.id.tvfacyear);
            tvFaclec = (TextView)findViewById(R.id.tvFacLec);
            tvFactime = (TextView)findViewById(R.id.tvfactime);
            tvFacLoc = (TextView)findViewById(R.id.tvFacloc);

            tvFacdivnext = (TextView)findViewById(R.id.tvFacDivnext);
            tvFacyearnext = (TextView)findViewById(R.id.tvfacyearnext);
            tvFactimenext = (TextView)findViewById(R.id.tvfactimenext);
            tvFaclecnext = (TextView)findViewById(R.id.tvFacLecnext);
            tvFacLocnext = (TextView)findViewById(R.id.tvFaclocnext);
            tvFacDayNext = (TextView)findViewById(R.id.tvFacDayNext);

            networkUtils = new NetworkUtils();
            toast = new Toast();
            realm = Realm.getDefaultInstance();





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
                    realm.beginTransaction();
                    realmObject.setId("123");
                    realmObject.setJsonTT(object.toString());
                    realm.commitTransaction();
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

    public void TTNotification(){



        FacultyTTRealmObject ttobject;
        Log.d(TAG, "TTNotification: in TTNotificaton");
        realm = Realm.getDefaultInstance();
        ttobject = realm.where(FacultyTTRealmObject.class).findFirst();
        if (ttobject == null){
            Log.d(TAG, "onReceive: ttobject was null");

        }else{
            // check the current task here....

            SimpleDateFormat sdf = new SimpleDateFormat("HH");
            String currentDateandTime = sdf.format(new Date());
            Log.d(TAG, "onReceive: current hour is "+currentDateandTime);
            //   if (Integer.parseInt(currentDateandTime)>8 && Integer.parseInt(currentDateandTime)< 7)
            {

                // valid ... check lectures

                SimpleDateFormat format = new SimpleDateFormat("EEEE");
                Date d = new Date();
                String dayOfTheWeek = format.format(d);
                Log.d(TAG, "onReceive: today is "+dayOfTheWeek);
                String  str = ttobject.getJsonTT();
                try {
                    JSONObject object = new JSONObject(str);
                    Log.d(TAG, "onReceive: tt object is"+object);
                    JSONArray array = object.getJSONArray(dayOfTheWeek);
                    Log.d(TAG, "onReceive: tt for today is "+array);

                    int length = array.length();
                    for (int i=0 ; i<length ; i++){
                        JSONObject currentObject = array.getJSONObject(i);


                        int time = Integer.parseInt(currentObject.getString("Time"));
                        if (time == Integer.parseInt(currentDateandTime)){
                            NotificationManager notificationManager = (NotificationManager) FacultyProfileActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);

                            Intent intent1 = new Intent(FacultyProfileActivity.this , UserProfile.class);
                            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            PendingIntent pendingIntent= PendingIntent.getActivity(FacultyProfileActivity.this , 100, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

                            NotificationCompat.Builder builder = new NotificationCompat.Builder(FacultyProfileActivity.this)
                                    .setContentIntent(pendingIntent)
                                    .setContentTitle("Next Lecture Updates")
                                    .setContentText("Subject - "+currentObject.getString("Subject") +
                                            "\n Staff - "+currentObject.getString("Staff") + "\n Location - "+currentObject.getString("Location")
                                            +"\n Time - "+currentObject.getString("Time"))
                                    .setSmallIcon(R.drawable.profile)
                                    .setAutoCancel(true);

                            notificationManager.notify(100, builder.build());
                            Log.d(TAG, "TTNotification: current lecture is"+currentObject);
                        }else{
                            Log.d(TAG, "TTNotification: No lecture in this slot");

                        }


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

    }

    }
