package college.root.vi12;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

// cz.msebera.android.httpclient.NameValuePair;
//import cz.msebera.android.httpclient.message.BasicNameValuePair;
//import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.ParseAnalytics;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import com.facebook.FacebookSdk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import college.root.vi12.StudentProfile.FormActivity;
import college.root.vi12.StudentProfile.Student_profile;
import io.realm.Realm;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {

    Button btnLogin ;
    EditText etUser , etPass;
    String username , password;
    TextView tvReg , tvHome ;
    String TAG = "Test";
    ProgressDialog progress;

    Realm realm;
    LoginButton loginButton;
    CallbackManager manager;
    Thread threadLogin;
    Socket socket;
    String ipaddress;
   Student_profile profile;
    ProgressDialog dialog ;
    CheckBox cbShowPass;
    SharedPreferences.Editor editorLogin;
    SharedPreferences sharedPreferencesLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        realm = Realm.getDefaultInstance();

        sharedPreferencesLogin = getSharedPreferences("login", Context.MODE_PRIVATE);
        editorLogin = sharedPreferencesLogin.edit();

        boolean isLoggedIn = sharedPreferencesLogin.getBoolean("login", false);
        if (isLoggedIn){
            startActivity(new Intent(MainActivity.this, HomePageActivity.class));
            finish();
        }




        setviews(); // initialize views


// link to register page .
        tvReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                finish();

            }
        });


// login code
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress = new ProgressDialog(MainActivity.this);
                progress.setMessage("Please wait...");
                progress.setCanceledOnTouchOutside(false);


                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
                            Toast.makeText(MainActivity.this, "Please fill out all the details ", Toast.LENGTH_SHORT).show();
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
/*                    ipAddess = new IPAddess();
                    ipAddess = realm.where(IPAddess.class).findFirst();
                    ipaddress = ipAddess.getIpaddress();*/
                    //socket = IO.socket("http://192.168.1.38:8083");
                    NetworkUtils networkUtils = new NetworkUtils();
                    socket = networkUtils.get();
                    // college.root.vi12.Toast.makeText(getBaseContext() , "Its a toast from backgrond thread ", college.root.vi12.Toast.LENGTH_SHORT).show();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                    Log.d(TAG, "run: error " + e.getMessage());
                }
//                socket.connect();

                //  Log.d(TAG, "run:connected.........");
                //  socket.on()
                // socket.connect();
                if (!socket.connected()) {

                    JSONObject object = new JSONObject();
                    try {
                        object.put("username", username);
                        object.put("password", password);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    socket.emit("login", object.toString());
                    //  progress.dismiss();
                } else {
                    Log.d(TAG, "run: not connected");
                }

                socket.on("loginResult", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        // check if user is authenticated
//                        dialog = new ProgressDialog(MainActivity.this);
                        //                      dialog.setMessage("Please wait whiel we log you in...");
                        //                    dialog.show();
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
                                     //   final String firstName = userData.getString("firstName");
                                       // String lastName = userData.getString("lastName");
                                        String branch = userData.getString("branch");
                                        String year = userData.getString("year");
                                        String username = userData.getString("username");
                                        String password = userData.getString("password");
                                        String email = userData.getString("email");

                                        Log.d(TAG, "call: USER : ");
                                        Log.d(TAG, "call: username  " + username);
                                        Log.d(TAG, "call: password " + password);
                                   //     Log.d(TAG, "call: name " + firstName);
                                     //   Log.d(TAG, "call: last name " + lastName);

                                        SharedPreferences sharedPreferences = getSharedPreferences("ShaPreferences", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();

                                        boolean firstTime = sharedPreferences.getBoolean("first", true);

                                        if (firstTime) {
                                            editor.putBoolean("first", false);
                                            //For commit the changes, Use either editor.commit(); or  editor.apply();.
                                            editor.commit();


                                            editorLogin.putBoolean("login" , true);
                                            editorLogin.commit();

                                            realm = Realm.getDefaultInstance();
                                            profile = new Student_profile();
                                            profile = realm.where(Student_profile.class).findFirst();
                                            if (profile == null) {
                                                Log.d(TAG, "save: profile is null");
                                                //     profile = realm.createObject(Student_profile.class);
                                                realm.beginTransaction();
                                                profile = new Student_profile();

                                              //  profile.setName(firstName);
                                                //profile.setSurname(lastName);
                                                profile.setBranch(branch);
                                                profile.setYear(year);
                                                profile.setGrno(GrNumber);
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
                                                        Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
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



                                            Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                            //startActivity(new Intent(MainActivity.this , FormActivity.class));

                            // college.root.vi12.Toast.makeText(MainActivity.this , "Successfully logged in ..", college.root.vi12.Toast.LENGTH_SHORT).show();
                        } else {
                            editorLogin.putBoolean("login" , false);
                            Log.d(TAG, "call: error in login");
                            //    dialog.dismiss();
                            //college.root.vi12.Toast.makeText(MainActivity.this , "Error logging in", college.root.vi12.Toast.LENGTH_SHORT).show();

                        }


                    }
                });

            }
        });


    }

    public void setviews(){
        btnLogin =  (Button)findViewById(R.id.button);
        tvReg = (TextView)findViewById(R.id.tvReg);
        tvHome = (TextView)findViewById(R.id.textView);
        etUser = (EditText)findViewById(R.id.etEmail);
        etPass = (EditText)findViewById(R.id.etPass);
      //  btnFb = (Button)findViewById(R.id.fbButton);
        loginButton = (LoginButton)findViewById(R.id.fbLoginButton);
        cbShowPass = (CheckBox)findViewById(R.id.cbShowPass);

    }

}
