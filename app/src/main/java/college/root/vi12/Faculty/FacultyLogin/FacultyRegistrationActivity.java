package college.root.vi12.Faculty.FacultyLogin;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import college.root.vi12.Miscleneous.EncryptPassword;
import college.root.vi12.NetworkTasks.NetworkUtils;
import college.root.vi12.R;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by kaio-ken on 3/10/17.
 */

public class FacultyRegistrationActivity extends AppCompatActivity
{



    EditText username,password,verifypassword,email,firstname,lastname,eid;
    Button Signup;
    Spinner spbranch,spcollege,spdiscipline;
    String Username,Password,Verifypassword,Email,Firstname,Lastname,Eid,branch,college,mdisc,TAG;
    EncryptPassword encryptPassword;
    ProgressDialog progress;
    Socket socket = null;
    List<String> listOfBranch,list,listOfDepisc ;

    ArrayAdapter<String> dataAdapter,branchAdapter,discAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_faculty_registration);
        setViews(); // initialize views




        Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress = new ProgressDialog(FacultyRegistrationActivity.this);
                progress.setMessage("Please wait...");
                progress.setCanceledOnTouchOutside(false);

                AlertDialog.Builder builder = new AlertDialog.Builder(FacultyRegistrationActivity.this);
                builder.setTitle("Conform Registration");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progress.show();
                        Username = username.getText().toString();
                        Firstname = firstname.getText().toString();
                        Lastname = lastname.getText().toString();
                        Eid = eid.getText().toString();
                        Email = email.getText().toString();
                        Password = password.getText().toString();
                        Verifypassword = verifypassword.getText().toString();
                        branch = spbranch.getSelectedItem().toString();
                        college = spcollege.getSelectedItem().toString();
                        mdisc = spdiscipline.getSelectedItem().toString();

                        try {
                            String encryptedPass = encryptPassword.encrypt(Password);
                            Log.d(TAG, "onClick:Encrypted Password " + encryptedPass);
                            String decryptPass = encryptPassword.decrypt(encryptedPass);
                            Log.d(TAG, "onClick: Decrypted password is " + decryptPass);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (TextUtils.isEmpty(Email) || TextUtils.isEmpty(Eid) || TextUtils.isEmpty(Password)
                                || TextUtils.isEmpty(Username) || branch.equals("Enter Year") || college.equals("Enter Faculty")
                                || TextUtils.isEmpty(Firstname) || TextUtils.isEmpty(Lastname)) {
                            Toast.makeText(FacultyRegistrationActivity.this, "Please enter all teh details", Toast.LENGTH_SHORT).show();

                        } else {


                            Log.d(TAG, "onClick: about to start the thread");

                            Register register = new Register();
                            register.doInBackground();
                            }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {


                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();

            }
        });





    }


    public  void setViews(){
        encryptPassword = new EncryptPassword();

        Signup = (Button) findViewById(R.id.signup);
        eid = (EditText) findViewById(R.id.eid);
        lastname = (EditText) findViewById(R.id.lastname);
        firstname = (EditText) findViewById(R.id.firstname);
        username = (EditText) findViewById(R.id.username);
        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        spbranch = (Spinner) findViewById(R.id.Branch);
        spcollege = (Spinner) findViewById(R.id.College);
        spbranch = (Spinner) findViewById(R.id.Branch);
        spdiscipline = (Spinner) findViewById(R.id.spDiscipline);
        verifypassword = (EditText) findViewById(R.id.verifypassword);

        listOfBranch = new ArrayList<String>();
        listOfBranch.add("Enter Branch");
        listOfBranch.add("Computer");
        listOfBranch.add("Mechanical");
        listOfBranch.add("Civil");
        listOfBranch.add("E&TC");
        listOfBranch.add("IT");
        listOfBranch.add("Others");

        branchAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, listOfBranch);
        branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spbranch.setAdapter(branchAdapter);



        list = new ArrayList<String>();
        list.add("Enter Faculty");
        list.add("STEM");
        list.add("ADA");
        list.add("CML");
        list.add("JNC");
        list.add("HSS");
        list.add("Other");
        dataAdapter = new ArrayAdapter<String>(FacultyRegistrationActivity.this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spcollege.setAdapter(dataAdapter);


        listOfDepisc = new ArrayList<String>();
        listOfDepisc.add("Enter Discipline");
        listOfDepisc.add("Engineering");
        listOfDepisc.add("Science");
        listOfDepisc.add("Mathematics & Statistics");
        listOfDepisc.add("Other");

        discAdapter = new ArrayAdapter<String>(FacultyRegistrationActivity.this,
                android.R.layout.simple_spinner_item, listOfDepisc);
        discAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spdiscipline.setAdapter(discAdapter);




    }

    private  class Register extends AsyncTask<Void , Void , Void>{

        @Override
        protected Void doInBackground(Void... params) {

            try {
                Log.d(TAG, "run: into the thread");

                NetworkUtils networkUtils = new NetworkUtils();
                socket = networkUtils.get();
            } catch (URISyntaxException e) {
                e.printStackTrace();
                Log.d(TAG, "run: error " + e.getMessage());
            }
            if (!socket.connected()) {
                Log.d(TAG, "run: connected");
                JSONObject object = new JSONObject();
                try {
                    object.put("username", Username);
                    object.put("password", Password);
                    object.put("email", Email);
                    object.put("grNumber", Eid);
                    object.put("branch", branch);
                    object.put("FirstName", Firstname);
                    object.put("LastName", Lastname);
                    object.put("Faculty", college);
                    object.put("Discipline" , mdisc);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                socket.emit("register", object.toString());
                socket.on("registerResult", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        // check if registration is successful
                        int authComplete = (int) args[0];
                        Log.d(TAG, "call: authComplete value is " + authComplete);

                        if (authComplete == 1) {
                            Log.d(TAG, "call: register success..");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progress.dismiss();

                                }
                            });
                            startActivity(new Intent(FacultyRegistrationActivity.this, FacultyLogin.class));

                            finish();
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progress.dismiss();

                                }
                            });

                            Log.d(TAG, "call: register error");

                        }
                    }
                });


            } else {
                Log.d(TAG, "run: not connected");
            }
            // socket.connect();


            return null;
        }
    }

}
