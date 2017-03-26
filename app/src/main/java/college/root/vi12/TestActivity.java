package college.root.vi12;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TestActivity extends AppCompatActivity {


    AutoCompleteTextView textIn;
    Button buttonAdd;
    LinearLayout container;
    String TAG ="Test";
    ImageButton imgBtnAdd;

    private static final String[] NUMBER = new String[]{
            "One", "Two", "Three", "Four", "Five",
            "Six", "Seven", "Eight", "Nine", "Ten"
    };
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, NUMBER);

        textIn = (AutoCompleteTextView) findViewById(R.id.textin);
        textIn.setAdapter(adapter);
        imgBtnAdd = (ImageButton)findViewById(R.id.imgBtnAdd);

        container = (LinearLayout) findViewById(R.id.container);

        imgBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                LayoutInflater layoutInflater =
                        (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View addView = layoutInflater.inflate(R.layout.row, null);
                AutoCompleteTextView textOut = (AutoCompleteTextView)addView.findViewById(R.id.actvnewsubj);
                textOut.setAdapter(adapter);
                textOut.setText(textIn.getText().toString());
               ImageButton imgBtnRemove = (ImageButton)findViewById(R.id.imgBtnremove);
                final View.OnClickListener thisListener = new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        ((LinearLayout)addView.getParent()).removeView(addView);

                        listAllAddView();
                    }
                };
                imgBtnRemove.setOnClickListener(thisListener);
                container.addView(addView);


                listAllAddView();

            }
        });
    }

    private void listAllAddView() {

        int childCount = container.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View thisChild = container.getChildAt(i);

            AutoCompleteTextView actvnewSubj = (AutoCompleteTextView) thisChild.findViewById(R.id.actvnewsubj);

            AutoCompleteTextView actvnewcode = (AutoCompleteTextView) thisChild.findViewById(R.id.actvnewCode);
            String newsubjname = actvnewSubj.getText().toString();
            String newsubjcode = actvnewcode.getText().toString();
            Log.d(TAG, "listAllAddView: new subject is "+newsubjname);
            Log.d(TAG, "listAllAddView: new subject code is "+newsubjcode);

        }
    }
}