package college.root.vi12;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    Button btnLogin ;
    EditText etUser , etPass;
    String username , password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        btnLogin =  (Button)findViewById(R.id.button);

        etUser = (EditText)findViewById(R.id.etEmail);
        etPass = (EditText)findViewById(R.id.etPass);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = etUser.getText().toString();
                password = etPass.getText().toString();

                ParseUser.logInInBackground(username, password, new LogInCallback() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        if (parseUser != null) {
                            Toast.makeText(MainActivity.this , "Login successfull ", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this , HomePageActivity.class));
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this , "Login failed.. ", Toast.LENGTH_SHORT).show();

                            //Login Fail
                            //get error by calling e.getMessage()
                        }
                    }
                });


            }
        });






    }// end of onCreate
}
