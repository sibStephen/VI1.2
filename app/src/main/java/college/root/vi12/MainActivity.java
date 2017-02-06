package college.root.vi12;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import org.json.JSONException;
import org.json.JSONObject;

import college.root.vi12.StudentProfile.FormActivity;
import io.realm.Realm;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {

    Button btnLogin ;
    EditText etUser , etPass;
    String username , password;
    TextView tvReg , tvHome , tvUser , tvEmail;
    String TAG = "Test";
    ProgressDialog progress;
    Button btnFb;
    Realm realm;
    LoginButton loginButton;
    CallbackManager manager;
    FacebookCallback<LoginResult> mCallBack;
    Thread threadLogin;
    Socket socket;
   // List<NameValuePair> params;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(MainActivity.this);

        setContentView(R.layout.activity_main);


        ParseAnalytics.trackAppOpenedInBackground(getIntent());
//        ParseFacebookUtils.initialize(this);

        realm = Realm.getDefaultInstance();
        final List<String> permissions = Arrays.asList("public_profile", "email");


        btnLogin =  (Button)findViewById(R.id.button);
        tvReg = (TextView)findViewById(R.id.tvReg);
        tvHome = (TextView)findViewById(R.id.textView);
        etUser = (EditText)findViewById(R.id.etEmail);
        etPass = (EditText)findViewById(R.id.etPass);
        btnFb = (Button)findViewById(R.id.fbButton);
       // tvEmail =  (TextView)findViewById(R.id.tvEmail);
        //tvUser = (TextView)findViewById(R.id.tvUser);
        loginButton = (LoginButton)findViewById(R.id.fbLoginButton);


// link to register page .
        tvReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this , RegisterActivity.class));

            }
        });


        

// login code
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress = new ProgressDialog(MainActivity.this);
                progress.setMessage("Please wait...");
                progress.setCanceledOnTouchOutside(false);
                progress.show();
                username = etUser.getText().toString();
                password = etPass.getText().toString();



                if(!username.isEmpty() && !password.isEmpty()){
                    // send data to server
                    threadLogin.start();

                }else{
                    Toast.makeText(MainActivity.this , "Please fill out all the details ", Toast.LENGTH_SHORT).show();
                }


            }
        });



        // login thread
        threadLogin = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    socket = IO.socket("http://192.168.1.38:8083/");
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                    Log.d(TAG, "run: error "+e.getMessage() );
                }
                socket.connect();

                Log.d(TAG, "run:connected.........");
                //  socket.on()
                // socket.connect();
                if (!socket.connected()){

                    JSONObject object = new JSONObject();
                    try {
                        object.put("username", username);
                        object.put("password", password);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    socket.emit("login" , object.toString() );
                    progress.dismiss();
                }

                socket.on("loginResult", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        // check if user is authenticated
                        int isAuth = (int)args[0];
                        Log.d(TAG, "call: value is "+isAuth);
                        if (isAuth==1){

                            //startActivity(new Intent(MainActivity.this , FormActivity.class));
                            SharedPreferences sharedPreferences = getSharedPreferences("ShaPreferences", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=sharedPreferences.edit();

                            boolean  firstTime=sharedPreferences.getBoolean("first", true);

                            if(firstTime) {
                                editor.putBoolean("first",false);
                                //For commit the changes, Use either editor.commit(); or  editor.apply();.
                                editor.commit();
                                Intent intent = new Intent(MainActivity.this, FormActivity.class);
                                startActivity(intent);
                            }else {
                                Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
                                startActivity(intent);
                            }
                           // Toast.makeText(MainActivity.this , "Successfully logged in ..", Toast.LENGTH_SHORT).show();
                        }else {
                            //Toast.makeText(MainActivity.this , "Error logging in", Toast.LENGTH_SHORT).show();

                        }



                    }
                });

            }
        });

        // FACEBOOK LOGIN/ SIGNUP CODE HERE ...

        loginButton.setReadPermissions(permissions);
//        loginButton.registerCallback(manager,mCallBack);

        mCallBack = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {



                Profile profile = Profile.getCurrentProfile();
                Log.d(TAG, "onSuccess: profile name is "+profile.getFirstName());

                requestUserProfile(loginResult);

                Toast.makeText(MainActivity.this , "Welcome "+profile.getFirstName() , Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        };


    }// end of onCreate


    public void requestUserProfile(LoginResult loginResult){
        GraphRequest.newMeRequest(
                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject me, GraphResponse response) {
                        if (response.getError() != null) {
                            // handle error
                        } else {
                            try {
                                String email = response.getJSONObject().get("email").toString();
                                Log.e(TAG, email);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            String id = me.optString("id");
                            // send email and id to your web server
                            Log.e(TAG, response.getRawResponse());
                            Log.e(TAG, me.toString());
                        }
                    }
                }).executeAsync();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        manager.onActivityResult(requestCode , resultCode , data);
    }
}
