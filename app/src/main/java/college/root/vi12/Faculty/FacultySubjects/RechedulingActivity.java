package college.root.vi12.Faculty.FacultySubjects;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import college.root.vi12.Faculty.FacultyProfile.FacultyProfileRealm;
import college.root.vi12.Miscleneous.Utils;
import college.root.vi12.NetworkTasks.NetworkUtils;
import college.root.vi12.R;
import io.realm.Realm;

public class RechedulingActivity extends AppCompatActivity {
    TextView tvSubj , tvFac , tvTime , tvLoc;
    Button btnAccept , btnDecline;
    String TAG = "Test";
    Realm realm;
    FacultyProfileRealm profile;
    NetworkUtils utils;
    JSONObject object;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lec_resc);

        initializeViews();
        setDataToViews();
        initializeRealmStuff();

    }

    private void initializeRealmStuff() {
        realm = Realm.getDefaultInstance();
        profile = realm.where(FacultyProfileRealm.class).findFirst();
    }

    private void setDataToViews() {


        String data =  getIntent().getStringExtra("jsondata");
        try {
           object = new JSONObject(data);

            Log.d(TAG, "onCreate: object is "+object);
            tvFac.setText(object.getString("Staff"));
            tvSubj.setText(object.getString("Subject"));
            tvLoc.setText(object.getString("Location"));
            tvTime.setText(object.getString("Time"));
        } catch (JSONException e) {
            e.printStackTrace();
        }}

    private void initializeViews() {

        tvFac = (TextView) findViewById(R.id.tvrescfaculty);
        tvSubj = (TextView) findViewById(R.id.tvrescSubject);
        tvLoc = (TextView) findViewById(R.id.tvrescloc);
        tvTime = (TextView) findViewById(R.id.tvresctime);
        btnAccept = (Button) findViewById(R.id.btnrescAccept);
        btnDecline = (Button) findViewById(R.id.btnDecclineReq);
        utils = new NetworkUtils();
    }

    public void onClickAccept(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(RechedulingActivity.this);
        builder.setTitle("Accept?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                try {
                    object.put("RescFaculty" , profile.getName()+ " "+profile.getSurname());
                    object.put("RescFacEID" , profile.getEid());
                    object.put("RescSubject" , "Rescheduled Subject");
                    object.put("isAccepted" , "1");
                    object.put("RequestType" , "LecRescheduleResponse");
                    utils.emitSocket("RespondToReq" , object);
                    Utils.toast(RechedulingActivity.this , "Request accepted...");
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                dialog.dismiss();
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

    public void onClickDecline(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RechedulingActivity.this);
        builder.setTitle("Decline?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                JSONObject o = new JSONObject();
                try {
                    o.put("isAccepted", "0");
                    o.put("EID" , profile.getEid());
                    o.put("RequestType" , "LecRescheduleResponse");
                    utils.emitSocket("RespondToReq" , o);
                    Utils.toast(RechedulingActivity.this , "Request accepted...");
                    finish();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
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
}
