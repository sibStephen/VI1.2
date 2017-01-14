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

public class RegisterActivity extends AppCompatActivity {

    Button btnReg;
    EditText etEmail , etUser, etPass ;
    String TAG = "Test";
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);



        btnReg = (Button)findViewById(R.id.btnRegister);
        etEmail = (EditText)findViewById(R.id.etEmailReg);
        etPass = (EditText)findViewById(R.id.etPass);
        etUser = (EditText)findViewById(R.id.etUser);

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
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            progress.dismiss();
                            Toast.makeText(RegisterActivity.this , "Registration success", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this , HomePageActivity.class));
                            //Register Successful
                            //you can display sth or do sth
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
