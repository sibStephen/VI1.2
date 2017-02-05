package college.root.vi12;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class ProfileActivity extends AppCompatActivity {

    EditText etName , etPhone , etEmail , etGr;
    Button btnSave;
    String name , grnumber , email , phone;
    Thread threadProfile;
    Socket socket;
    String TAG = "Test";
    JSONObject object;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile2);

        etEmail = (EditText)findViewById(R.id.etEmail);
        etName = (EditText)findViewById(R.id.etName);
        etGr = (EditText)findViewById(R.id.etGr);
        etPhone = (EditText)findViewById(R.id.etPhone);
        btnSave = (Button) findViewById(R.id.btnSave);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Save details ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        name = etName.getText().toString();
                        phone = etPhone.getText().toString();
                        email= etEmail.getText().toString();
                        grnumber = etGr.getText().toString();

                        threadProfile.start();



                    }
                });
                builder.setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();
                    }
                });
                builder.show();

            }
        });

        threadProfile = new Thread(new Runnable() {
            @Override
            public void run() {

                 object = new JSONObject();
                try {
                    socket = IO.socket("http://192.168.1.38:8083/");
                    socket.connect();
                    if (!socket.connected()) {
                        Log.d(TAG, "run: connected ....");
                    }else{
                        Log.d(TAG, "run: not connected");
                    }
                        Log.d(TAG, "run: socket successfully defined in profile activity");

                    object.put("grNumber" , grnumber);
                    object.put("myname", name);
                    object.put("phoneNumber" , phone);
                    object.put("email" , email);
                    Log.d(TAG, "run:  student details are : "+object.toString());
                    socket.emit("profileInput" , object.toString());

                    Log.d(TAG, "run: profile successfully created");

                } catch (URISyntaxException e) {
                    e.printStackTrace();
                    Log.d(TAG, "Error: " +e.getMessage());
                } catch (JSONException e) {
                    Log.d(TAG, "JsonError: " +e.getMessage());
                    e.printStackTrace();
                }

            }
        });



    }
}
