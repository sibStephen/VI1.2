package college.root.vi12.Student.StudentProfile;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import college.root.vi12.Miscleneous.Toast;
import college.root.vi12.NetworkTasks.NetworkUtils;
import college.root.vi12.R;
import college.root.vi12.Student.Realm.Student_profile;
import io.realm.Realm;
import io.socket.client.Socket;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentProfile4 extends Fragment {

    Realm realm;
    EditText ssc_maths,ssc_science,hsc_eng,hsc_phy,hsc_chem,hsc_it,ssc_total,hsc_total,hsc_maths;
    String oldgrno;
    Button save,back;
    String TAG="Test";
    Student_profile profile;
    NetworkUtils networkUtils;
    Socket socket;
    Toast toast;
    public FragmentProfile4() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_profile4, container, false);
        ((EditProfileActivity)getActivity()).setActionBarTitle("Academic Details");



        realm = Realm.getDefaultInstance();
        ssc_maths=(EditText)view.findViewById(R.id.ssc_maths);
        ssc_science=(EditText)view.findViewById(R.id.ssc_science);
        ssc_total=(EditText)view.findViewById(R.id.ssc_total);
        hsc_chem=(EditText)view.findViewById(R.id.hsc_chem);
        hsc_eng=(EditText)view.findViewById(R.id.hsc_eng);
        hsc_it=(EditText)view.findViewById(R.id.hsc_it);
        hsc_phy=(EditText)view.findViewById(R.id.hsc_phy);
        hsc_maths=(EditText)view.findViewById(R.id.hsc_maths);
        hsc_total=(EditText)view.findViewById(R.id.hsc_total);

        profile = new Student_profile();
        profile = realm.where(Student_profile.class).findFirst();

            ssc_maths.setText(profile.getSsc_maths());
            ssc_science.setText(profile.getSsc_sci());
            ssc_total.setText(profile.getSsc_total());
            hsc_maths.setText(profile.getHsc_maths());
            hsc_chem.setText(profile.getHsc_chem());
            hsc_phy.setText(profile.getHsc_phy());
            hsc_eng.setText(profile.getHsc_eng());
            hsc_it.setText(profile.getHsc_it());
            hsc_total.setText(profile.getHsc_total());


        save=(Button)view.findViewById(R.id.save_academic);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Save data ?");
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();
                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        profile = new Student_profile();
                        profile = realm.where(Student_profile.class).findFirst();
                        if(profile==null) {
                            Log.d(TAG, "save: profile is null");
                            //     profile = realm.createObject(Student_profile.class);
                            realm.beginTransaction();
                            profile = new Student_profile();

                            profile.setSsc_maths(ssc_maths.getText().toString());
                            profile.setSsc_sci(ssc_science.getText().toString());
                            profile.setSsc_total(ssc_total.getText().toString());
                            profile.setHsc_chem(hsc_chem.getText().toString());
                            profile.setHsc_eng(hsc_eng.getText().toString());
                            profile.setHsc_it(hsc_it.getText().toString());
                            profile.setHsc_phy(hsc_phy.getText().toString());
                            profile.setHsc_maths(hsc_maths.getText().toString());

                            realm.commitTransaction();

                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.copyToRealmOrUpdate(profile);
                                }
                            });
//            realm.commitTransaction();
                        }
                        else
                        {
                            //  oldgrno=profile.getGrno();
                            profile = realm.where(Student_profile.class).findFirst();
                            realm.beginTransaction();
                            profile.setSsc_maths(ssc_maths.getText().toString());
                            profile.setSsc_sci(ssc_science.getText().toString());
                            profile.setSsc_total(ssc_total.getText().toString());
                            profile.setHsc_chem(hsc_chem.getText().toString());
                            profile.setHsc_eng(hsc_eng.getText().toString());
                            profile.setHsc_it(hsc_it.getText().toString());
                            profile.setHsc_phy(hsc_phy.getText().toString());
                            profile.setHsc_maths(hsc_maths.getText().toString());
                            realm.commitTransaction();
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.copyToRealmOrUpdate(profile);

                                }
                            });
                        }



                        try {

                            networkUtils = new NetworkUtils();

                            JSONObject basicUserDetails = new JSONObject();
                            basicUserDetails.put("ssc_maths" , ssc_maths.getText().toString());
                            basicUserDetails.put("ssc_science" , ssc_science.getText().toString());
                            basicUserDetails.put("ssc_total" , ssc_total.getText().toString());
                            basicUserDetails.put("hsc_chem" , hsc_chem.getText().toString());
                            basicUserDetails.put("hsc_eng" , hsc_eng.getText().toString());
                            basicUserDetails.put("hsc_it" , hsc_it.getText().toString());
                            basicUserDetails.put("hsc_phy" , hsc_phy.getText().toString());
                            basicUserDetails.put("hsc_maths" , hsc_maths.getText().toString());
                            basicUserDetails.put("Timestamp",networkUtils.getLocalIpAddress()+" "+ new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime() ));


                            String[] contents = {"ssc_maths" , "ssc_science" , "ssc_total", "hsc_chem"
                                    , "hsc_eng","hsc_it","hsc_phy","hsc_maths","Timestamp" };
                            StringBuilder sb = new StringBuilder();
                            for (int j=0 ; j<contents.length; j++){
                                Log.d(TAG, "onClick: "+contents[j]);
                                sb.append(contents[j]+",");
                            }
                            JSONObject finalObj = new JSONObject();
                            finalObj.put("obj" , basicUserDetails.toString());
                            finalObj.put("contents" , sb.toString());
                            finalObj.put("Length" , contents.length);
                            finalObj.put("collectionName" , "academicDetails");
                            finalObj.put("grNumber" , profile.getGrno());

                            networkUtils.emitSocket("Allinfo",finalObj);

                            networkUtils.disconnectSocketAsync();
                            toast = new Toast();
                            networkUtils.listener("Allinfo" , getActivity() , getContext(), toast); //success  listener







                        }  catch (JSONException e) {
                            Log.d(TAG, "onClick: json error "+e.getMessage());
                        }

                    }
                });
                builder.show();






            }
        });


        return view;
    }

}
