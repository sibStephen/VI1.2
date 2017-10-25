package college.root.vi12.Faculty.FacultySubjects;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import college.root.vi12.Faculty.FacultyProfile.FacultyProfileRealm;
import college.root.vi12.Miscleneous.Utils;
import college.root.vi12.NetworkTasks.NetworkUtils;
import college.root.vi12.R;
import io.realm.Realm;

public class RechedulingActivity extends AppCompatActivity {
    TextView tvSubj , tvFac , tvTime , tvLoc , tvYear , tvDiv , tvRescSubj;
    Button btnAccept , btnDecline;
    String TAG = "Test";
    Realm realm;
    FacultyProfileRealm profile;
    NetworkUtils utils;
    JSONObject object;
    FacultySubjRealmObj subjectRealm;

    String rescheduledSubject = " ", rescheduledSubjectCode=" ";
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

            Log.d(TAG, "onCreate: object is " + object);
            tvFac.setText(object.getString("Staff"));
            tvSubj.setText(object.getString("Subject"));
            tvLoc.setText(object.getString("Location"));
            tvTime.setText(object.getString("Time"));
            tvDiv.setText(object.getString("Div"));
            tvYear.setText(object.getString("Year"));
            getReschedulingSubject(object);
            if (!rescheduledSubject.equals(" ")){
                tvRescSubj.setText(rescheduledSubject);

            }else {
                tvRescSubj.setText("No Corresponding lecture found...");
            }

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
        tvDiv = (TextView) findViewById(R.id.tvRescDiv);
        tvYear = (TextView) findViewById(R.id.tvRescYear);
        tvRescSubj = (TextView) findViewById(R.id.tvrescsubfac);
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
                    object.put("RescSubject" , rescheduledSubject);
                    object.put("isAccepted" , "1");
                    object.put("RequestType" , "LecRescheduleResponse");
                    object.put("RescSubjCode", rescheduledSubjectCode);
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

    private  void getReschedulingSubject(JSONObject object){

        try {
            String year = object.getString("Year");
            String div = object.getString("Div");
            Realm realm = Realm.getDefaultInstance();
            profile = realm.where(FacultyProfileRealm.class).findFirst();
            if (profile != null){
                subjectRealm = realm.where(FacultySubjRealmObj.class).findFirst();
                if (subjectRealm != null){
                    String str = subjectRealm.getJsonSubjObj();
                    JSONObject facObj = new JSONObject(str);
                    JSONArray array = facObj.getJSONArray(year);
                    for (int i=0; i<array.length() ; i++){
                       JSONObject eachObject = array.getJSONObject(i);
                        if (eachObject.getString("Div").equals(div)){
                            Log.d(TAG, "getReschedulingSubject: match found");
                            rescheduledSubject = eachObject.getString("Subject");
                            rescheduledSubjectCode = eachObject.getString("SubjectCode");
                        }
                    }
                }
            }else {
                Utils.somethingIsNull(RechedulingActivity.this , "Faculty");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
