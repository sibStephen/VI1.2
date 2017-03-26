package college.root.vi12;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import college.root.vi12.StudentProfile.Student_profile;
import io.realm.Realm;
import io.socket.client.IO;
import io.socket.client.Socket;


public class AttendanceFragment extends Fragment implements ProgressGenerator.OnCompleteListener{

    Socket socket;
    String TAG = "Test";
    String eos , cn , pcdp , dsp , se , eost , cnt , pcdpt , dspt , set;
    EditText etEOS , etPCDP, etDSPA, etSE , etCN;
    EditText etEOST , etPCDPT, etDSPAT, etSET , etCNT;
    Button btnSend;
    NetworkUtils networkUtils;
    college.root.vi12.Toast toast;
    Realm realm;
    Student_profile profile;
    Subjects subjects;



    public AttendanceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       networkUtils = new NetworkUtils();
        toast = new college.root.vi12.Toast();
        realm = Realm.getDefaultInstance();
        subjects = new Subjects();
        return inflater.inflate(R.layout.fragment_attendance, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ActionProcessButton btnSignIn = (ActionProcessButton) getActivity().findViewById(R.id.btnSignIn);
        btnSignIn.setMode(ActionProcessButton.Mode.PROGRESS);

        etCN = (EditText)getActivity().findViewById(R.id.etCn);
        etEOS = (EditText)getActivity().findViewById(R.id.etEos);
        etPCDP = (EditText)getActivity().findViewById(R.id.etPcdp);
        etSE = (EditText)getActivity().findViewById(R.id.etSe);
        etDSPA = (EditText)getActivity().findViewById(R.id.etDspa);
        btnSend = (Button)getActivity().findViewById(R.id.btnSendData);

        etDSPAT = (EditText)getActivity().findViewById(R.id.etDspaTotal);
        etPCDPT = (EditText)getActivity().findViewById(R.id.etPcdpTotal);
        etSET = (EditText)getActivity().findViewById(R.id.etSeTotal);
        etCNT = (EditText)getActivity().findViewById(R.id.etCnTotal);
        etEOST = (EditText)getActivity().findViewById(R.id.etEosTotal);

        profile = new Student_profile();
        profile = realm.where(Student_profile.class).findFirst();
        if (profile== null){
            toast.showToast(getActivity(),"profile is null" );
            Log.d(TAG, "onViewCreated: profile is null");
        }else {
            toast.showToast(getActivity(),"profile is not null" );

        }

        subjects = realm.where(Subjects.class).findFirst();
        if (subjects ==null){

            subjects = new Subjects();
            subjects.setGRNumber(profile.getGrno());
            Log.d(TAG, "onViewCreated: Subjects is null");
            subjects.setSubj1Name("EOC");
            subjects.setSubj2Name("PCDP");
            subjects.setSubj3Name("SE");
            subjects.setSubj4Name("CN");
            subjects.setSubj5Name("DSPA");
            Log.d(TAG, "onViewCreated: Entering subject names ");
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    realm.copyToRealmOrUpdate(subjects);
                    Log.d(TAG, "execute: Done Entering names ");
                }
            });
        }else {
            realm.beginTransaction();
            subjects.setSubj1Name("EOC");
            subjects.setSubj2Name("PCDP");
            subjects.setSubj3Name("SE");
            subjects.setSubj4Name("CN");
            subjects.setSubj5Name("DSPA");
            realm.commitTransaction();

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(subjects);
                    Log.d(TAG, "execute: Done Entering names ");


                }
            });




        }




        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    socket = networkUtils.get();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

                se = etSE.getText().toString();
                eos = etEOS.getText().toString();
                cn = etCN.getText().toString();
                pcdp = etPCDP.getText().toString();
                dsp = etDSPA.getText().toString();

                set = etSET.getText().toString();
                eost = etEOST.getText().toString();
                pcdpt = etPCDPT.getText().toString();
                cnt = etCNT.getText().toString();
                dspt = etDSPAT.getText().toString();

                realm.beginTransaction();

                subjects.setSubj1Total(eost);
                subjects.setSubj2Total(pcdpt);
                subjects.setSubj3Total(set);
                subjects.setSubj4Total(cnt);
                subjects.setSubj5Total(dspt);

                subjects.setSubj1Att(eos);
                subjects.setSubj1Att(pcdp);
                subjects.setSubj1Att(se);
                subjects.setSubj1Att(cn);
                subjects.setSubj1Att(dsp);
                realm.commitTransaction();
                Log.d(TAG, "onClick: updating attendance and total values ");
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.copyToRealmOrUpdate(subjects);
                    }
                });

                if (TextUtils.isEmpty(eos)||TextUtils.isEmpty(cn)||TextUtils.isEmpty(dsp)||
                        TextUtils.isEmpty(se)||TextUtils.isEmpty(pcdp)||TextUtils.isEmpty(set)||
                        TextUtils.isEmpty(eost)||TextUtils.isEmpty(pcdpt)||TextUtils.isEmpty(cnt)||
                        TextUtils.isEmpty(dspt)){

                    toast.showToast(getActivity() , "Please enter all the details");
                }else {


                    JSONObject object = new JSONObject();
                    try {
                        object.put(subjects.getSubj1Name() ,etEOS.getText().toString());
                        object.put(subjects.getSubj2Name() , etPCDP.getText().toString());
                        object.put(subjects.getSubj3Name() ,etSE.getText().toString());
                        object.put(subjects.getSubj4Name() , etCN.getText().toString());
                        object.put(subjects.getSubj5Name() , etDSPA.getText().toString());
                        object.put(subjects.getSubj1Name()+"_Total" ,subjects.getSubj1Total());
                        object.put(subjects.getSubj2Name()+"_Total" ,subjects.getSubj2Total());
                        object.put(subjects.getSubj3Name()+"_Total" ,subjects.getSubj3Total());
                        object.put(subjects.getSubj4Name()+"_Total" , subjects.getSubj4Total());
                        object.put(subjects.getSubj5Name()+"_Total" , subjects.getSubj5Total());




                        String[] contents = { subjects.getSubj1Name(), subjects.getSubj2Name() , subjects.getSubj3Name(),
                                subjects.getSubj4Name(), subjects.getSubj5Name(),
                                subjects.getSubj1Name()+"_Total" , subjects.getSubj2Name()+"_Total" ,
                                subjects.getSubj3Name()+"_Total", subjects.getSubj4Name()+"_Total",
                                subjects.getSubj5Name()+"_Total"};
                        StringBuilder sb = new StringBuilder();
                        for (int j=0 ; j<contents.length; j++){
                            Log.d(TAG, "onClick: "+contents[j]);
                            sb.append(contents[j]+",");
                        }
                        JSONObject finalObj = new JSONObject();
                        finalObj.put("obj" , object.toString());
                        finalObj.put("contents" , sb.toString());
                        finalObj.put("Length" , 10);
                        finalObj.put("collectionName" , "attendance");
                        finalObj.put("grNumber" , profile.getGrno());

                        networkUtils.emitSocket("Allinfo",finalObj);
                        networkUtils.listener("Allinfo" , getActivity() , getContext(), toast); //success  listener






                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }




            }
        });


















// no progress
        btnSignIn.setProgress(0);
// progressDrawable cover 50% of button width, progressText is shown
        btnSignIn.setProgress(50);
// progressDrawable cover 75% of button width, progressText is shown
        btnSignIn.setProgress(75);
// completeColor & completeText is shown
        btnSignIn.setProgress(100);

// you can display endless google like progress indicator
        btnSignIn.setMode(ActionProcessButton.Mode.ENDLESS);
// set progress > 0 to start progress indicator animation
        btnSignIn.setProgress(1);
    }

    @Override
    public void onComplete() {

        android.widget.Toast.makeText(getContext(), "Completed", Toast.LENGTH_SHORT).show();

    }
}
