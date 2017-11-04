package college.root.vi12.Faculty.FacultyLogin;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import college.root.vi12.Faculty.FacultyProfile.FacultyProfileActivity;
import college.root.vi12.Faculty.FacultyProfile.FacultyProfileRealm;
import college.root.vi12.NetworkTasks.NetworkUtils;
import college.root.vi12.R;
import io.realm.Realm;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by kaio-ken on 3/10/17.
 */

public class FacultyLogin extends AppCompatActivity
{
    TextView register;
    Button btnsignin;
    EditText etUser,etPass;
    String username,password,TAG;
    ProgressDialog progress;

    Realm realm;
    Thread threadLogin;
    Socket socket;
    FacultyProfileRealm profile;
    CheckBox cbShowPass;
    Faculty_LoggedIn loggedIn;


    public boolean isLoggedIn(){
// TODO when user is logged in but his profile gets null then next tym after login the userprofile remains null "bug"

        boolean login = false;

        realm = Realm.getDefaultInstance();
        loggedIn = realm.where(Faculty_LoggedIn.class).findFirst();

        if(loggedIn == null){
            Log.d(TAG, "isLoggedIn: usr not logged in");
            login = false;

        }else {

            Log.d(TAG, "isLoggedIn: user logged in");
            if(loggedIn.isLoggedIn()){
                Log.d(TAG, "isLoggedIn: user has been logged in");
                login = true;
            }else{
                Log.d(TAG, "isLoggedIn: object exists but does not not have a true isLoggedIn value");

                login = false;
            }


        }


        return  login;

    }


    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_faculty_login);

        if (isLoggedIn()){
            Log.d(TAG, "onCreate: shared pref gave true ");
            startActivity(new Intent(FacultyLogin.this, FacultyProfileActivity.class));
            finish();
         }

        setviews(); // initialize views

        try{
            getSupportActionBar().hide();
        }catch (Exception e){
            e.printStackTrace();
        }

        cbShowPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(!isChecked){
                    // show password
                    etPass.setTransformationMethod(new PasswordTransformationMethod());
                }else{
                    etPass.setTransformationMethod(null);
                }


            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(FacultyLogin.this, FacultyRegistrationActivity.class));
                finish();

            }
        });


        btnsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress = new ProgressDialog(FacultyLogin.this);
                progress.setMessage("Please wait...");
                progress.setCanceledOnTouchOutside(false);


                AlertDialog.Builder builder = new AlertDialog.Builder(FacultyLogin.this);
                builder.setTitle("Confirm Login ?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //   progress.show();
                        username = etUser.getText().toString();
                        password = etPass.getText().toString();
                        if (!username.isEmpty() && !password.isEmpty()) {
                            // send data to server
                            threadLogin.start();

                        } else {
                            Toast.makeText(FacultyLogin.this, "Please fill out all the details ", Toast.LENGTH_SHORT).show();
                        }


                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();
                    }
                });
                builder.show();


            }
        });


        // login thread
        threadLogin = new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    NetworkUtils networkUtils = new NetworkUtils();
                    socket = networkUtils.get();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                    Log.d(TAG, "run: error " + e.getMessage());
                }
                if (!socket.connected()) {

                    JSONObject object = new JSONObject();
                    try {
                        object.put("username", username);
                        object.put("password", password);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    socket.emit("login", object.toString());
                } else {
                    Log.d(TAG, "run: not connected");
                }

                socket.on("loginResult", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        int isAuth = (int) args[0];
                        Log.d(TAG, "call: value is " + isAuth);
                        if (isAuth == 1) {
                            Log.d(TAG, "call: is Auth is 1 hence getting data from server");
                            // recieve the json data from server
                            socket.on("JSON", new Emitter.Listener() {
                                @Override
                                public void call(Object... args) {
                                    // json array is received of which 0th element is user data
                                    JSONArray array = (JSONArray) args[0];
                                    Log.d(TAG, "call: array is " + array);
                                    JSONObject userData = null;
                                    try {
                                        userData = array.getJSONObject(0);
                                        Log.d(TAG, "call: object is " + userData);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        String GrNumber = userData.getString("_id");
                                        final String firstName = userData.getString("firstName");
                                        String lastName = userData.getString("lastName");
                                        String branch = userData.getString("branch");
                                        String username = userData.getString("username");
                                        String password = userData.getString("password");
                                        String email = userData.getString("email");
                                        String eid = userData.getString("_id");

                                        //ArrayList<String> arrayList = (ArrayList<String>) userData.get("Contents");


                                        Log.d(TAG, "call: USER : ");
                                        Log.d(TAG, "call: username  " + username);
                                        Log.d(TAG, "call: password " + password);
                                        //     Log.d(TAG, "call: name " + firstName);
                                        //   Log.d(TAG, "call: last name " + lastName);

                                        // SharedPreferences sharedPreferences = getSharedPreferences("ShaPreferences", Context.MODE_PRIVATE);
                                        //SharedPreferences.Editor editor = sharedPreferences.edit();

                                        realm = Realm.getDefaultInstance();
                                        loggedIn = realm.where(Faculty_LoggedIn.class).findFirst();



                                        //boolean firstTime = sharedPreferences.getBoolean("first", true);

                                        if (loggedIn == null) {
                                            Log.d(TAG, "call: its the first time so load the class into realm");


                                            loggedIn = realm.where(Faculty_LoggedIn.class).findFirst();
                                            if (loggedIn == null){
                                                Log.d(TAG, "call: adding loggedin class for the first tym");

                                                realm.beginTransaction();
                                                loggedIn = new Faculty_LoggedIn();
                                                loggedIn.setId(1);
                                                loggedIn.setLoggedIn(true);
                                                realm.commitTransaction();
                                                realm.executeTransaction(new Realm.Transaction() {
                                                    @Override
                                                    public void execute(Realm realm) {
                                                        realm.copyToRealmOrUpdate(loggedIn);
                                                        Log.d(TAG, "execute: logged in realm obj set to true");

                                                    }
                                                });

                                            }



                                            realm = Realm.getDefaultInstance();
                                            profile = new FacultyProfileRealm();
                                            profile = realm.where(FacultyProfileRealm.class).findFirst();
                                            if (profile == null) {
                                                Log.d(TAG, "save: profile is null");
                                                //     profile = realm.createObject(Student_profile.class);
                                                realm.beginTransaction();
                                                profile = new FacultyProfileRealm();

                                                //  profile.setName(firstName);
                                                //profile.setSurname(lastName);
                                                profile.setBranch(branch);
                                                profile.setName(firstName);
                                                profile.setSurname(lastName);
                                                profile.setEid(eid);
                                                profile.setUsername(username);
                                                profile.setPassword(password);
                                                profile.setEmail_pri(email);
                                                //profile.setGrno(grno.getText().toString());

                                                realm.commitTransaction();
                                                realm.executeTransaction(new Realm.Transaction() {
                                                    @Override
                                                    public void execute(Realm realm) {
                                                        Log.d(TAG, "execute: realm saving data .....");
                                                        realm.copyToRealmOrUpdate(profile);
                                                        Log.d(TAG, "execute: realm saved data .....");
                                                        Intent intent = new Intent(FacultyLogin.this, FacultyProfileActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                        //threadLogin.interrupt();
                                                    }
                                                });



                                            } else {
                                                //   dialog.dismiss();
                                                Log.d(TAG, "call: there already exists a student profile information");
                                            }

                                        } else {


                                            Log.d(TAG, "call: its not the first time so direct intent to home page");

                                            Intent intent = new Intent(FacultyLogin.this, FacultyProfileActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                            //startActivity(new Intent(MainActivity.this , FormActivity.class));

                            // college.root.vi12.Miscleneous.Toast.makeText(MainActivity.this , "Successfully logged in ..", college.root.vi12.Miscleneous.Toast.LENGTH_SHORT).show();
                        } else {


                            realm = Realm.getDefaultInstance();
                            loggedIn = realm.where(Faculty_LoggedIn.class).findFirst();
                            if (loggedIn == null){
                                Log.d(TAG, "call: adding loggedin class for the first tym");

                                realm.beginTransaction();
                                loggedIn = new Faculty_LoggedIn();
                                loggedIn.setId(1);
                                loggedIn.setLoggedIn(false);
                                realm.commitTransaction();
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        realm.copyToRealmOrUpdate(loggedIn);
                                        Log.d(TAG, "execute: logged in realm obj set to true");

                                    }
                                });

                            }
                            //editorLogin.putBoolean("login" , false);
                            Log.d(TAG, "call: error in login");
                            //    dialog.dismiss();
                            //college.root.vi12.Miscleneous.Toast.makeText(MainActivity.this , "Error logging in", college.root.vi12.Miscleneous.Toast.LENGTH_SHORT).show();

                        }


                    }
                });

            }
        });


    }

    public void setviews(){
        btnsignin =  (Button)findViewById(R.id.signin);
        etUser = (EditText)findViewById(R.id.user);
        etPass = (EditText)findViewById(R.id.pass);
        cbShowPass = (CheckBox)findViewById(R.id.showpass);
        register = (TextView) findViewById(R.id.register);

    }






}


