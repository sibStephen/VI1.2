package college.root.vi12;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class DecidingActivity extends AppCompatActivity {

    Button btnTeacher , btnStudent , btnAdmin;
    RelativeLayout root;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deciding);

        btnAdmin = (Button)findViewById(R.id.btnAdmin);
        btnTeacher = (Button)findViewById(R.id.btnTeacher);
        btnStudent = (Button)findViewById(R.id.btnStudent);


    }
}
