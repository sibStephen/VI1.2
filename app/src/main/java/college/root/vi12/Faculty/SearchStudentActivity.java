package college.root.vi12.Faculty;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import college.root.vi12.R;

public class SearchStudentActivity extends AppCompatActivity {


    ImageButton imgSearch;
    EditText etSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_student);
    }
}
