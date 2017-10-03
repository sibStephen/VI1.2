package college.root.vi12.Student.StudentProfile;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import college.root.vi12.AdminActivities.TimeTable.TimeTableDisplayActivity;
import college.root.vi12.Miscleneous.Toast;
import college.root.vi12.Miscleneous.Utils;
import college.root.vi12.NetworkTasks.CheckNetwork;
import college.root.vi12.NetworkTasks.JsontoSend;
import college.root.vi12.NetworkTasks.NetworkUtils;
import college.root.vi12.R;
import college.root.vi12.Student.MySubjects.MySubjectsActivity;
import college.root.vi12.Student.Realm.Student_profile;
import college.root.vi12.Student.Student_TT.Realm.TTRealmObject;
import college.root.vi12.Student.TTReceiver;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmResults;
import io.socket.client.Ack;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

//import android.icu.util.Calendar;

public class UserProfile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    Uri imageuri;
    Realm realm;
    Student_profile userProfile;
    TextView tvname,tvsurname,tvyear,tvdiv,tvbranch;
    CircleImageView profilePic;
    int GALLERY_REQUEST = 1;
    Uri mImageUri;
    Student_profile profile;
    String TAG = "Test";
    Socket socket;
    NetworkUtils networkUtils;
    Toast toast;
    String ttID;
    Context context ;
    public static JSONObject currentLecObject = null;
    TTRealmObject realmObject ;
    TextView  tvStlec, tvSttime, tvStLoc, tvStlecnext,
            tvSttimenext, tvStLocnext, tvStFaculty,tvStFacultynext , tvStdayNext;


    private void initializeViews() {

        profilePic = (CircleImageView) findViewById(R.id.profilepic);
        tvname = (TextView) findViewById(R.id.name);
        tvsurname = (TextView) findViewById(R.id.surname);
        tvyear = (TextView) findViewById(R.id.year);
        tvdiv = (TextView) findViewById(R.id.div);
        tvbranch = (TextView) findViewById(R.id.branch);



        tvStlec = (TextView)findViewById(R.id.tvStLec);
        tvSttime = (TextView)findViewById(R.id.tvSttime);
        tvStLoc = (TextView)findViewById(R.id.tvStloc);
        tvStFaculty = (TextView)findViewById(R.id.tvStFaculty);

        tvSttimenext = (TextView)findViewById(R.id.tvSttimenext);
        tvStlecnext = (TextView)findViewById(R.id.tvStLecnext);
        tvStLocnext = (TextView)findViewById(R.id.tvStlocnext);
        tvStFacultynext = (TextView)findViewById(R.id.tvStFacultynext);
        tvStdayNext = (TextView)findViewById(R.id.tvStdayNext);

        networkUtils = new NetworkUtils();
        toast = new Toast();
        realm = Realm.getDefaultInstance();





    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_profile, menu);

        realm = Realm.getDefaultInstance();
        profile = realm.where(Student_profile.class).findFirst();

        MenuItem menuItem = menu.findItem(R.id.settings);
        initializeMenu(menuItem);
        return true;

    }


    public  void initializeMenu(MenuItem item){
        if (profile != null){
            Log.d(TAG, "onOptionsItemSelected: profile not null");


            Log.d(TAG, "onOptionsItemSelected: "+profile.isAreNotificationsEnabled());
            if (profile.isAreNotificationsEnabled()){
                Log.d(TAG, "onOptionsItemSelected: value is true");

                item.setChecked(true);


            }else{
                Log.d(TAG, "onOptionsItemSelected: val is false ");
                item.setChecked(false);
            }

        }


    }

  @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {


            case R.id.settings:


                if (item.isChecked()){
                    Log.d(TAG, "onOptionsItemSelected: checkbox was checked");
                    item.setChecked(false);
                    profile = realm.where(Student_profile.class).findFirst();
                    if (profile != null){
                        realm.beginTransaction();
                        profile.setAreNotificationsEnabled(false);
                        realm.commitTransaction();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                realm.copyToRealmOrUpdate(profile);
                            }
                        });
                    }
                    android.widget.Toast.makeText(UserProfile.this , "Notifications disabled..", android.widget.Toast.LENGTH_SHORT).show();
                }else {
                    Log.d(TAG, "onOptionsItemSelected: checkbox was not checked ");
                    item.setChecked(true);
                    profile = realm.where(Student_profile.class).findFirst();
                    if (profile != null){
                        realm.beginTransaction();
                        profile.setAreNotificationsEnabled(true);
                        realm.commitTransaction();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                realm.copyToRealmOrUpdate(profile);
                            }
                        });
                    }
                    android.widget.Toast.makeText(UserProfile.this , "Notifications Enabled..", android.widget.Toast.LENGTH_SHORT).show();

                }


                break;
            case R.id.sync_server:

                Log.d(TAG, "onOptionsItemSelected: sync server called");

                CheckNetwork checkNetwork = new CheckNetwork();
                if (!CheckNetwork.isNetWorkAvailable(UserProfile.this)){

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

    @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_user_profile);


            //TODO add a student profile field named as 1) isTTLoaded 2) areSubjectsLoaded
            // TODO if internet connection exists then load TT from server else load locally.

            Calendar calendar =Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY , 8);
            Intent intent = new Intent(this , TTReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);

            manager.setRepeating(AlarmManager.RTC_WAKEUP , calendar.getTimeInMillis() , AlarmManager.INTERVAL_HOUR, pendingIntent);
            context = UserProfile.this;

            initializeViews();
            loadCurrentLecture();
            try {
                loadNextLectureUpdates();
            } catch (Exception e) {
                e.printStackTrace();
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


             userProfile = realm.where(Student_profile.class).findFirst();

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
            if (!flag) {
                Log.d("FLAG1:", String.valueOf(flag));

                if (userProfile.getImagePath() != null)
                    profilePic.setImageURI(Uri.parse(userProfile.getImagePath()));
            }

        }

    }

    private void loadNextLectureUpdates() throws Exception {

        String[] days = {"Monday", "Tuesday", "Wednesday", "Thrusday", "Friday", "Saturday"};
        int today = 0;
        boolean stillContinue = true;
        TTRealmObject ttobject;
        Log.d(TAG, ": in NextNecture");
        realm = Realm.getDefaultInstance();
        ttobject = realm.where(TTRealmObject.class).findFirst();
        if (ttobject == null) {
            Log.d(TAG, "onReceive: ttobject was null");

        } else {
            // check the current task here....

            SimpleDateFormat sdf = new SimpleDateFormat("mm");
            String currentMins = sdf.format(new Date());
            sdf = new SimpleDateFormat("HH");
            String currentHour = sdf.format(new Date());
            Log.d(TAG, "onReceive: current hour is " + currentHour + "." + currentMins);
            //   if (Integer.parseInt(currentDateandTime)>8 && Integer.parseInt(currentDateandTime)< 7)
            {

                // valid ... check lectures

                SimpleDateFormat format = new SimpleDateFormat("EEEE");
                Date d = new Date();
                String dayOfTheWeek = format.format(d);
                Log.d(TAG, "onReceive: today is " + dayOfTheWeek);
                int cDay = Utils.getNumberFromWeek(days, dayOfTheWeek);
                Log.d(TAG, "loadNextLectureUpdates: cDay is " + cDay);

                String str = ttobject.getJsonTTObject();

                JSONObject object = new JSONObject(str);
                Log.d(TAG, "onReceive: tt object is" + object);
                JSONArray array = object.getJSONArray(dayOfTheWeek);
                Log.d(TAG, "onReceive: tt for today is " + array);


                array = object.getJSONArray(days[cDay]);
                Log.d(TAG, "loadNextLectureUpdates: checking for day " + days[cDay]);


                int length = array.length();
                for (int i = 0; i < length; i++) {
                    JSONObject currentObject = array.getJSONObject(i);


                    String time = currentObject.getString("Time");
                    Log.d(TAG, "loadCurrentLecture: time is " + time);
                    int index = time.indexOf(".");
                    String hourOfLec = time.substring(0, index);
                    String minOfLec = time.substring(index + 1);
                    int hourOfLecIint = Integer.parseInt(hourOfLec);
                    int currentHourInt = (Integer.parseInt(currentHour));
                    if (hourOfLecIint > currentHourInt) {


                        if (hourOfLecIint > 12) {
                            hourOfLecIint -= 12;
                            time = String.valueOf(hourOfLecIint) + "." + minOfLec + " PM";
                        } else {
                            time = String.valueOf(hourOfLecIint) + "." + minOfLec + " AM";

                        }
                        tvSttimenext.setText(time);
                        tvStlecnext.setText(currentObject.getString("Subject"));
                        tvStFacultynext.setText(currentObject.getString("Staff"));
                        tvStLocnext.setText(currentObject.getString("Location"));
                        tvStdayNext.setText(days[cDay]);
                        Log.d(TAG, "TTNotification: current lecture is" + currentObject);
                      displayNotification(currentObject);

                        stillContinue = false;
                        break;
                    } else {
                        Log.d(TAG, "TTNotification: No lecture in this slot");

                    }


                }

                Log.d(TAG, "loadNextLectureUpdates: cday is " + cDay + 1);

                for (int j = cDay + 1; j < days.length; j++) {
                    Log.d(TAG, "loadNextLectureUpdates: in looopppppp");
                    if (!stillContinue)
                        break;

                    Log.d(TAG, " check timeslots in next days ");
                    array = object.getJSONArray(days[j]);
                    Log.d(TAG, "loadNextLectureUpdates: checking for day " + days[j]);
                    JSONObject currentObject = array.getJSONObject(j);


                    String time = currentObject.getString("Time");
                    Log.d(TAG, "loadCurrentLecture: time is " + time);
                    int index = time.indexOf(".");
                    String hourOfLec = time.substring(0, index);
                    String minOfLec = time.substring(index + 1);
                    int hourOfLecIint = Integer.parseInt(hourOfLec);
                    int nextHour = hourOfLecIint + 1;
                    String nextHourString = String.valueOf(nextHour);
                    int currentHourInt = (Integer.parseInt(currentHour));
                    if (hourOfLecIint > currentHourInt) {


                        if (hourOfLecIint > 12) {
                            hourOfLecIint -= 12;
                            time = String.valueOf(hourOfLecIint) + "." + minOfLec + " PM";
                        } else {
                            time = String.valueOf(hourOfLecIint) + "." + minOfLec + " AM";

                        }
                        tvSttimenext.setText(time);
                        tvStlecnext.setText(currentObject.getString("Subject"));
                        tvStLocnext.setText(currentObject.getString("Location"));
                        tvStFacultynext.setText(currentObject.getString("Staff"));
                        tvStdayNext.setText(days[j]);
                        Log.d(TAG, "TTNotification: current lecture is" + currentObject);
                       displayNotification(currentObject);
                        stillContinue = false;


                    }

                }


            }


        }
    }


    private void loadCurrentLecture() {


        TTRealmObject ttobject;
        Log.d(TAG, "TTNotification: in TTNotificaton");
        realm = Realm.getDefaultInstance();
        ttobject = realm.where(TTRealmObject.class).findFirst();
        if (ttobject == null){
            Log.d(TAG, "onReceive: ttobject was null");

        }else{
            // check the current task here....

            SimpleDateFormat sdf = new SimpleDateFormat("mm");
            String currentMins = sdf.format(new Date());
            sdf = new SimpleDateFormat("HH");
            String currentHour = sdf.format(new Date());
            Log.d(TAG, "onReceive: current hour is "+currentHour+"."+currentMins);
            {

                // valid ... check lectures

                SimpleDateFormat format = new SimpleDateFormat("EEEE");
                Date d = new Date();
                String dayOfTheWeek = format.format(d);
                Log.d(TAG, "onReceive: today is "+dayOfTheWeek);
                String  str = ttobject.getJsonTTObject();
                try {
                    JSONObject object = new JSONObject(str);
                    Log.d(TAG, "onReceive: tt object is"+object);
                    JSONArray array = object.getJSONArray(dayOfTheWeek);
                    Log.d(TAG, "onReceive: tt for today is "+array);

                    int length = array.length();
                     for (int i=0 ; i<length ; i++){
                        JSONObject currentObject = array.getJSONObject(i);

                        if (!currentObject.getString("Time").equals("Nill")){

                            String time = currentObject.getString("Time");
                            Log.d(TAG, "loadCurrentLecture: time is "+time);
                            int index = time.indexOf(".");
                            String hourOfLec = time.substring(0, index);
                            String minOfLec = time.substring(index+1);
                            int hourOfLecIint = Integer.parseInt(hourOfLec);


                            if (hourOfLecIint == Integer.parseInt(currentHour))
                                // hours are same check for mins
                            {

                                if (Integer.parseInt(currentMins)>= Integer.parseInt(minOfLec)) {


                                    currentLecObject = currentObject;
                                    Log.d(TAG, "loadCurrentLecture: current object is"+currentObject);
                                    tvStlec.setText(currentObject.getString("Subject"));
                                    tvStLoc.setText(currentObject.getString("Location"));
                                    if (hourOfLecIint > 12){
                                        hourOfLecIint -= 12;
                                        time = String.valueOf(hourOfLecIint) +"."+ minOfLec + " PM";
                                    }else{
                                        time = String.valueOf(hourOfLecIint) +"."+ minOfLec + " AM";

                                    }
                                    tvSttime.setText(time);
                                    tvStFaculty.setText(currentObject.getString("Staff"));
                                    Log.d(TAG, "TTNotification: current lecture is" + currentObject);
                                    break;
                                }
                            } if((hourOfLecIint +1)  == Integer.parseInt(currentHour)){
                                if (Integer.parseInt(currentMins)< Integer.parseInt(minOfLec)) {


                                    tvStlec.setText(currentObject.getString("Subject"));
                                    tvStLoc.setText(currentObject.getString("Location"));
                                    if (hourOfLecIint > 12){
                                        hourOfLecIint -= 12;
                                        time = String.valueOf(hourOfLecIint) +"."+ minOfLec + " PM";
                                    }else{
                                        time = String.valueOf(hourOfLecIint) +"."+ minOfLec + " AM";

                                    }
                                    tvSttime.setText(time);
                                    tvStFaculty.setText(currentObject.getString("Staff"));
                                    Log.d(TAG, "TTNotification: current lecture is" + currentObject);
                                    currentLecObject = currentObject;
                                    Log.d(TAG, "loadCurrentLecture: current object is"+currentObject);

                                    break;
                                }



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

    public void editPicture(View view){


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



            if(CheckNetwork.isNetWorkAvailable(this)){
                // if network available .... fetch recent TT

                realm = Realm.getDefaultInstance();

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

            }else{
                // else load the local TT

                Intent i1 = new Intent(UserProfile.this,TimeTableDisplayActivity.class);
                realm = Realm.getDefaultInstance();
                realmObject  = realm.where(TTRealmObject.class).findFirst();

                if (realmObject != null){
                    i1.putExtra("object",realmObject.getJsonTTObject());
                    i1.putExtra("id",ttID);
                    i1.putExtra("User" , "Student");
                    startActivity(i1);

                  }else{
                    Log.d(TAG, "onNavigationItemSelected: realmobject is null");
                }
                }




           // startActivity(new Intent(this , TimeTableDisplayActivity.class));
        }

            drawer.closeDrawer(GravityCompat.START);
        return true;
    }



   private Emitter.Listener ttlistener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {



            if (args[0].equals("0")){
                Log.d(TAG, "call: no data found");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        android.widget.Toast.makeText(UserProfile.this, "No previous TT entry found", android.widget.Toast.LENGTH_SHORT).show();

                    }
                });
            }  else {


                Log.d(TAG, "call: Result is " + args[0]);
                JSONArray array = (JSONArray) args[0];

                Log.d(TAG, "call: array is " + array);
                try {
                    JSONObject obj = array.getJSONObject(0);
                    Log.d(TAG, "call: obj is " + obj);


                    realm = Realm.getDefaultInstance();
                    realmObject = realm.where(TTRealmObject.class).findFirst();
                    if (realmObject == null) {

                        realmObject = new TTRealmObject();
                        realmObject.setId(obj.getString("_id"));
                        realmObject.setJsonTTObject(obj.toString());
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {

                                realm.copyToRealmOrUpdate(realmObject);
                            }
                        });

                    } else {

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


                    Intent i1 = new Intent(UserProfile.this, TimeTableDisplayActivity.class);

                    i1.putExtra("object", realmObject.getJsonTTObject());
                    i1.putExtra("id", ttID);
                    i1.putExtra("User", "Student");

                    startActivity(i1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    };


    public void displayNotification(JSONObject currentObject) throws JSONException {



        realm = Realm.getDefaultInstance();
        profile = realm.where(Student_profile.class).findFirst();
        if (profile != null){
            if (profile.isAreNotificationsEnabled()){
                Log.d(TAG, "loadNextLectureUpdates: value is true");
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                Intent intent1 = new Intent(UserProfile.this , UserProfile.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent= PendingIntent.getActivity(context , 100, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                        .setContentIntent(pendingIntent)
                        .setContentTitle("Next Lecture Updates")
                        .setContentText("Subject - "+currentObject.getString("Subject") +
                                "\n Staff - "+currentObject.getString("Staff") + "\n Location - "+currentObject.getString("Location")
                                +"\n Time - "+currentObject.getString("Time"))
                        .setSmallIcon(R.drawable.profile)
                        .setAutoCancel(true);

                notificationManager.notify(100, builder.build());
            }else{
                Log.d(TAG, "loadNextLectureUpdates: value is false");
            }

        }


    }




}
