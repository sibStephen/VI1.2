package college.root.vi12;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import io.realm.Realm;

public class RegisterActivity extends AppCompatActivity {

    Button btnReg;
    EditText etEmail , etUser, etPass , etGRNumber , etBranch , etYear ;
    String TAG = "Test";
    ProgressDialog progress;
    Realm realm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        realm = Realm.getDefaultInstance();

        btnReg = (Button)findViewById(R.id.btnRegister);
        etEmail = (EditText)findViewById(R.id.etEmailReg);
        etPass = (EditText)findViewById(R.id.etPass);
        etUser = (EditText)findViewById(R.id.etUser);
        etBranch = (EditText)findViewById(R.id.etBranch);
        etGRNumber = (EditText)findViewById(R.id.etGrNumber);
        etYear = (EditText)findViewById(R.id.etYear);


        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress = new ProgressDialog(RegisterActivity.this);
                progress.setMessage("Please wait...");
                progress.setCanceledOnTouchOutside(false);
                progress.show();
                ParseUser user = new ParseUser();
                user.setEmail(etEmail.getText().toString());
                user.setUsername(etUser.getText().toString());
                user.setPassword(etPass.getText().toString());
                user.put("Branch" , etBranch.getText().toString());
                user.put("Year" , etYear.getText().toString());
                user.put("GR Number" , etGRNumber.getText().toString());
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            progress.dismiss();
                            Toast.makeText(RegisterActivity.this , "Registration success", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this , HomePageActivity.class));
                            //Register Successful
                            //you can display sth or do sth
                            RealmUser realmUser = new RealmUser();
                            realmUser.setEmail(etEmail.getText().toString());
                            realmUser.setYear(etYear.getText().toString());
                            realmUser.setBranch(etBranch.getText().toString());
                            realmUser.setGrNumber(Integer.parseInt(etGRNumber.getText().toString()));
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {

                                }
                            });

                        } else {
                            progress.dismiss();
                            Log.d(TAG, "error : "+e.getMessage());
                            Toast.makeText(RegisterActivity.this , "Registration failed", Toast.LENGTH_SHORT).show();

                            //Register Fail
                            //get error by calling e.getMessage()
                        }
                    }
                });
            }
        });



    }
}
