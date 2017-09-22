package college.root.vi12;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import college.root.vi12.Miscleneous.IPAddess;
import college.root.vi12.NetworkTasks.NetworkUtils;
import io.realm.Realm;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class RegisterActivity extends AppCompatActivity {

    Button btnReg;
    EditText etEmail, etUser, etPass, etGRNumber,  etFirstName , etLastName;
    String TAG = "Test";
    ProgressDialog progress;
    Realm realm;
    Thread threadRegister;
    Socket socket;
    String ipaddres;
    String GrNumber, email, password, username , year , branch, firstName , lastName, mdisc , mfaculty,
            mprogram;
    String ipaddress;
    IPAddess ipAddess;
    ArrayAdapter<String> dataAdapter,discAdapter, programAdapter, yearAdapter , branchAdapter;
    Spinner spDiscipline, spFaculty, spProgram, spYear, spBranch;


    EncryptPassword encryptPassword ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setViews(); // initialize views


        List<String> listOfYear = new ArrayList<String>();
        listOfYear.add("Enter Year");
        listOfYear.add("FE");
        listOfYear.add("SE");
        listOfYear.add("TE");
        listOfYear.add("BE");
        listOfYear.add("ME");
        yearAdapter = new ArrayAdapter<String>(RegisterActivity.this,
                android.R.layout.simple_spinner_item,listOfYear);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spYear.setAdapter(yearAdapter);

        List<String> listOfBranch = new ArrayList<String>();
        listOfBranch.add("Enter Branch");
        listOfBranch.add("Computer");
        listOfBranch.add("Mechanical");
        listOfBranch.add("Civil");
        listOfBranch.add("E&TC");
        listOfBranch.add("IT");
        listOfBranch.add("Others");

        branchAdapter = new ArrayAdapter<String>(RegisterActivity.this,
                android.R.layout.simple_spinner_item,listOfBranch);
        branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBranch.setAdapter(branchAdapter);



        List<String> list = new ArrayList<String>();
        list.add("Enter Faculty");
        list.add("STEM");
        list.add("ADA");
        list.add("CML");
        list.add("JNC");
        list.add("HSS");
        list.add("Other");
        dataAdapter = new ArrayAdapter<String>(RegisterActivity.this,
                android.R.layout.simple_spinner_item,list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFaculty.setAdapter(dataAdapter);

        List<String> listOfPrograms = new ArrayList<String>();
        listOfPrograms.add("Enter Program");
        listOfPrograms.add("B.Tech");
        listOfPrograms.add("M.Tech");
        listOfPrograms.add("Other");

        programAdapter = new ArrayAdapter<String>(RegisterActivity.this,
                android.R.layout.simple_spinner_item,listOfPrograms);
        programAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProgram.setAdapter(programAdapter);


        List<String> listOfDepisc = new ArrayList<String>();
        listOfDepisc.add("Enter Discipline");
        listOfDepisc.add("Engineering");
        listOfDepisc.add("Science");
        listOfDepisc.add("Mathematics & Statistics");
        listOfDepisc.add("Other");

        discAdapter = new ArrayAdapter<String>(RegisterActivity.this,
                android.R.layout.simple_spinner_item,listOfDepisc);
        discAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDiscipline.setAdapter(discAdapter);


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
                        branch = spBranch.getSelectedItem().toString();
                        year = spYear.getSelectedItem().toString();
                        username = etUser.getText().toString();
                        password = etPass.getText().toString();
                        firstName = etFirstName.getText().toString();
                        lastName = etLastName.getText().toString();
                        mdisc = spDiscipline.getSelectedItem().toString();
                        mfaculty = spFaculty.getSelectedItem().toString();
                        mprogram = spProgram.getSelectedItem().toString();

                        try {
                            String encryptedPass = encryptPassword.encrypt(password);
                            Log.d(TAG, "onClick:Encrypted Password "+encryptedPass);
                            String decryptPass = encryptPassword.decrypt(encryptedPass);
                            Log.d(TAG, "onClick: Decrypted password is "+decryptPass);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(GrNumber) || TextUtils.isEmpty(password)
                                || TextUtils.isEmpty(username) || year.equals("Enter Year") || branch.equals("Enter Branch")
                                || TextUtils.isEmpty(firstName)|| TextUtils.isEmpty(lastName) || mfaculty.equals("Enter Faculty")
                                || mdisc.equals("Enter Discipline") || mprogram.equals("Enter Program")) {
                            Toast.makeText(RegisterActivity.this, "Please enter all teh details", Toast.LENGTH_SHORT).show();

                        } else {
                            // save user on to server
                            Log.d(TAG, "onClick: about to start the thread");

                            if (!threadRegister.isAlive())
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

                    NetworkUtils networkUtils = new NetworkUtils();
                    socket = networkUtils.get();
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
                        object.put("Discipline" , mdisc);
                        object.put("Faculty" , mfaculty);
                        object.put("Program" , mprogram);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    socket.emit("register", object.toString());
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
                                finish();
                                
                            }else {
                                progress.dismiss();

                                Log.d(TAG, "call: register error");
                                threadRegister.interrupt();
                                
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
        encryptPassword = new EncryptPassword();

        btnReg = (Button) findViewById(R.id.btnRegister);
        etGRNumber = (EditText) findViewById(R.id.etEmailReg);
        etLastName = (EditText) findViewById(R.id.etPass);
        etFirstName = (EditText) findViewById(R.id.etUser);
        etUser = (EditText) findViewById(R.id.etGrNumber);
        etEmail = (EditText)findViewById(R.id.etFirstName);
        etPass = (EditText)findViewById(R.id.etLastname);
        spDiscipline = (Spinner) findViewById(R.id.spDiscipline);
        spFaculty = (Spinner) findViewById(R.id.spFaculty);
        spProgram = (Spinner) findViewById(R.id.spProgram);
        spYear = (Spinner) findViewById(R.id.spYearReg);
        spBranch = (Spinner) findViewById(R.id.spBranchReg);

    }
}