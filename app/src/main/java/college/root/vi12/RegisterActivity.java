package college.root.vi12;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.parse.interceptors.ParseStethoInterceptor;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.realm.Realm;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class RegisterActivity extends AppCompatActivity {

    Button btnReg;
    EditText etEmail, etUser, etPass, etGRNumber, etBranch, etYear, etFirstName , etLastName;
    String TAG = "Test";
    ProgressDialog progress;
    Realm realm;
    Thread threadRegister;
    Socket socket;
    String ipaddres;
    String GrNumber, email, password, username , year , branch, firstName , lastName;
    String ipaddress;
    IPAddess ipAddess;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setViews(); // initialize views


        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress = new ProgressDialog(RegisterActivity.this);
                progress.setMessage("Please wait...");
                progress.setCanceledOnTouchOutside(false);



                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                builder.setTitle("Confirm Registration ?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        progress.show();
                        GrNumber = etGRNumber.getText().toString();
                        email = etEmail.getText().toString();
                        branch = etBranch.getText().toString();
                        year = etYear.getText().toString();
                        username = etUser.getText().toString();
                        password = etPass.getText().toString();
                        firstName = etFirstName.getText().toString();
                        lastName = etLastName.getText().toString();

                        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(GrNumber) || TextUtils.isEmpty(password)
                                || TextUtils.isEmpty(username) || TextUtils.isEmpty(branch)
                                || TextUtils.isEmpty(firstName)|| TextUtils.isEmpty(lastName)) {
                            Toast.makeText(RegisterActivity.this, "Please enter all teh details", Toast.LENGTH_SHORT).show();

                        } else {
                            // save user on to server
                            Log.d(TAG, "onClick: about to start the thread");
                            threadRegister.start();

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

                // register user



            }
        });

        threadRegister  = new Thread(new Runnable() {
            @Override
            public void run() {


                try {
                    Log.d(TAG, "run: into the thread");
                 /*   ipAddess = new IPAddess();
                    ipAddess = realm.where(IPAddess.class).findFirst();
                    ipaddress = ipAddess.getIpaddress();*/
                    socket = IO.socket("http://192.168.1.38:8083/");
                    socket.connect();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                    Log.d(TAG, "run: error "+e.getMessage() );
                }
                if (!socket.connected()){
                    Log.d(TAG, "run: connected");
                    JSONObject object = new JSONObject();
                    try {
                        object.put("username", username);
                        object.put("password", password);
                        object.put("email", email);
                        object.put("grNumber", GrNumber);
                        object.put("branch", branch);
                        object.put("year", year);
                        object.put("FirstName" , firstName);
                        object.put("LastName" , lastName);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    socket.emit("register" , object.toString() );
                    socket.on("registerResult", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            // check if registration is successful
                            int authComplete = (int) args[0];
                            Log.d(TAG, "call: authComplete value is "+authComplete);
                            if (authComplete == 1){
                                Log.d(TAG, "call: register success..");
                                progress.dismiss();
                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                
                            }else {
                                progress.dismiss();
                                Log.d(TAG, "call: register error");
                                
                            }
                        }
                    });

                   



                }else{
                    Log.d(TAG, "run: not connected");
                }
               // socket.connect();

            }
        });

    }

    public  void setViews(){
        realm = Realm.getDefaultInstance();

        btnReg = (Button) findViewById(R.id.btnRegister);
        etEmail = (EditText) findViewById(R.id.etEmailReg);
        etPass = (EditText) findViewById(R.id.etPass);
        etUser = (EditText) findViewById(R.id.etUser);
        etBranch = (EditText) findViewById(R.id.etBranch);
        etGRNumber = (EditText) findViewById(R.id.etGrNumber);
        etYear = (EditText) findViewById(R.id.etYear);
        etFirstName = (EditText)findViewById(R.id.etFirstName);
        etLastName = (EditText)findViewById(R.id.etLastname);

    }
}