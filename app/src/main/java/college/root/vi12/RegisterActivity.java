package college.root.vi12;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class RegisterActivity extends AppCompatActivity {

    Button btnReg;
    EditText etEmail , etUser, etPass ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnReg = (Button)findViewById(R.id.btnRegister);
        etEmail = (EditText)findViewById(R.id.etEmailReg);
        etPass = (EditText)findViewById(R.id.etPass);
        etUser = (EditText)findViewById(R.id.etUser);

        ParseUser user = new ParseUser();
        user.setUsername(etUser.getText().toString());
        user.setPassword(etPass.getText().toString());
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(RegisterActivity.this , "Registration success", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this , HomePageActivity.class));
                    //Register Successful
                    //you can display sth or do sth
                } else {
                    Toast.makeText(RegisterActivity.this , "Registration failed", Toast.LENGTH_SHORT).show();

                    //Register Fail
                    //get error by calling e.getMessage()
                }
            }
        });
    }
}
