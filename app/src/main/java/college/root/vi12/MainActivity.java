package college.root.vi12;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Arrays;
import java.util.List;
import com.parse.ParseFacebookUtils;
import com.facebook.FacebookSdk;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity {

    Button btnLogin ;
    EditText etUser , etPass;
    String username , password;
    TextView tvReg , tvHome , tvUser , tvEmail;
    String TAG = "Test";
    ProgressDialog progress;
    Button btnFb;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        FacebookSdk.sdkInitialize(getApplicationContext());
        ParseFacebookUtils.initialize(this);

        realm = Realm.getDefaultInstance();
        final List<String> permissions = Arrays.asList("public_profile", "email");


        btnLogin =  (Button)findViewById(R.id.button);
        tvReg = (TextView)findViewById(R.id.tvReg);
        tvHome = (TextView)findViewById(R.id.textView);
        etUser = (EditText)findViewById(R.id.etEmail);
        etPass = (EditText)findViewById(R.id.etPass);
        btnFb = (Button)findViewById(R.id.fbButton);
        tvEmail =  (TextView)findViewById(R.id.tvEmail);
        tvUser = (TextView)findViewById(R.id.tvUser);

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

                ParseUser.logInInBackground(username, password, new LogInCallback() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        if (parseUser != null) {
                            progress.dismiss();
                            Toast.makeText(MainActivity.this , "Login successfull ", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this , HomePageActivity.class));
                            finish();
                        } else {
                            progress.dismiss();
                            Log.d(TAG, "error: "+e.getMessage());

                            Toast.makeText(MainActivity.this , "Login failed.. ", Toast.LENGTH_SHORT).show();

                            //Login Fail
                            //get error by calling e.getMessage()
                        }
                    }
                });


            }
        });




        // FACEBOOK LOGIN/ SIGNUP CODE HERE ...

        btnFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                ParseFacebookUtils.logInWithReadPermissionsInBackground(MainActivity.this, permissions, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException err) {
                        if (user == null) {
                            Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                        } else if (user.isNew()) {
                            Log.d("MyApp", "User signed up and logged in through Facebook!");
                            getUserDetailFromFB();
                        } else {
                            Log.d("MyApp", "User logged in through Facebook!");
                            getUserDetailFromParse();
                        }
                    }
                });
            }
        });



    }// end of onCreate

    void getUserDetailFromFB(){
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),new GraphRequest.GraphJSONObjectCallback(){
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try{
                    tvUser.setText(object.getString("name"));
                }catch(JSONException e){
                    e.printStackTrace();
                }
                try{
                    tvEmail.setText(object.getString("email"));
                }catch(JSONException e){
                    e.printStackTrace();
                }
                saveNewUser();
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields","name,email");
        request.setParameters(parameters);
        request.executeAsync();
    }
    void saveNewUser(){
        ParseUser user = ParseUser.getCurrentUser();
        user.setUsername(tvUser.getText().toString());
        user.setEmail(tvEmail.getText().toString());
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                    Toast.makeText(MainActivity.this , "Facebook login successfull", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getUserDetailFromParse(){
        ParseUser user = ParseUser.getCurrentUser();
        tvUser.setText(user.getUsername());
        tvEmail.setText(user.getEmail());
        Log.d(TAG, "getUserDetailFromParse: username is "+user.getUsername());
        Log.d(TAG, "getUserDetailFromParse: Email is "+user.getEmail());
    }
}
