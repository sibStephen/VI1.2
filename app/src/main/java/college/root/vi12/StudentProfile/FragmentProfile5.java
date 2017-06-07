package college.root.vi12.StudentProfile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import college.root.vi12.NetworkTasks.NetworkUtils;
import college.root.vi12.R;
import college.root.vi12.Toast;
import io.realm.Realm;
import io.realm.RealmConfiguration;



public class FragmentProfile5 extends Fragment {

    Realm realm;
    EditText fname,mname,fprofession,fdesig,fworkplace,fmobile,femail,mprofession,mworkplace,mdesig,mmobile;
    Button save;
    String TAG="Test";
    Student_profile profile;
    String Mname , Fname , Fprofession , Fdesig, Fworkplace,Fmobile,Femail,Mprofession,Mworkplace,Mdesig,Mmobile;
    NetworkUtils networkUtils;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_profile5, container, false);
        final RealmConfiguration config = new RealmConfiguration.Builder(getContext()).schemaVersion(4).deleteRealmIfMigrationNeeded().build();
        realm.setDefaultConfiguration(config);


        realm = Realm.getDefaultInstance();
        fname=(EditText)view.findViewById(R.id.fathername);
        mname=(EditText)view.findViewById(R.id.mothername);
        fprofession=(EditText)view.findViewById(R.id.fprofession);
        fdesig=(EditText)view.findViewById(R.id.designation);
        fworkplace=(EditText)view.findViewById(R.id.workplace);
        fmobile=(EditText)view.findViewById(R.id.fmobile);
        femail=(EditText)view.findViewById(R.id.femail);
        mprofession=(EditText)view.findViewById(R.id.mprofession);
        mworkplace=(EditText)view.findViewById(R.id.mworkplace);
        mdesig=(EditText)view.findViewById(R.id.mdesignation);
        mmobile=(EditText)view.findViewById(R.id.mmobile);

        profile = new Student_profile();
        profile = realm.where(Student_profile.class).findFirst();

        if(profile!=null) {
           fname.setText(profile.getFname());
             mname.setText(profile.getMname());
            fprofession.setText(profile.getFprofession());
            fdesig.setText(profile.getFdesig());
            fworkplace.setText(profile.getFworkplace());
            fmobile.setText(profile.getFworkplace());
            femail.setText(profile.getFemail());
            mprofession.setText(profile.getMprofession());
            mworkplace.setText(profile.getMworkplace());
            mdesig.setText(profile.getMdesig());
            mmobile.setText(profile.getMmobile());

        }


        save=(Button)view.findViewById(R.id.save_parent);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                profile = new Student_profile();
                profile = realm.where(Student_profile.class).findFirst();

                Fname = fname.getText().toString();
                Mname = mname.getText().toString();
                Fprofession = fprofession.getText().toString();
                Mprofession = mprofession.getText().toString();
                Fdesig = fdesig.getText().toString();
                Fworkplace = fworkplace.getText().toString();
                Fmobile = fmobile.getText().toString();
                Femail = femail.getText().toString();
                Mworkplace = mworkplace.getText().toString();
                Mdesig = mdesig.getText().toString();
                Mmobile = mmobile.getText().toString();
                Log.d(TAG, "onClick:  Strings extracted ");


                if(profile==null) {
                    Log.d(TAG, "save: profile is null");
                    //     profile = realm.createObject(Student_profile.class);
                    realm.beginTransaction();
                    profile = new Student_profile();

                    profile.setFname(fname.getText().toString());
                    profile.setMname(mname.getText().toString());
                    profile.setFprofession(fprofession.getText().toString());
                    profile.setFdesig(fdesig.getText().toString());
                    profile.setFworkplace(fworkplace.getText().toString());
                    profile.setFmobile(fmobile.getText().toString());
                    profile.setFemail(femail.getText().toString());
                    profile.setMprofession(mprofession.getText().toString());
                    profile.setMworkplace(mworkplace.getText().toString());
                    profile.setMdesig(mdesig.getText().toString());
                    profile.setMmobile(mmobile.getText().toString());


                    realm.commitTransaction();

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.copyToRealmOrUpdate(profile);
                        }
                    });
//            realm.commitTransaction();
                }
                else {
                    Toast toast = new Toast();
                    toast.showProgressDialog(getActivity(), "Saving details....");

                    //  oldgrno=profile.getGrno();
                    profile = realm.where(Student_profile.class).findFirst();
                    realm.beginTransaction();
// TODO 1) do not write main logic in onCreateView instead write in onViewCreated
                    profile.setFname(fname.getText().toString());
                    profile.setMname(mname.getText().toString());
                    profile.setFprofession(fprofession.getText().toString());
                    profile.setFdesig(fdesig.getText().toString());
                    profile.setFworkplace(fworkplace.getText().toString());
                    profile.setFmobile(fmobile.getText().toString());
                    profile.setFemail(femail.getText().toString());
                    profile.setMprofession(mprofession.getText().toString());
                    profile.setMworkplace(mworkplace.getText().toString());
                    profile.setMdesig(mdesig.getText().toString());
                    profile.setMmobile(mmobile.getText().toString());
                    realm.commitTransaction();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.copyToRealmOrUpdate(profile);

                        }
                    });


                    JSONObject parentInfo = new JSONObject();
                    try {
                        networkUtils = new NetworkUtils();
                        parentInfo.put("fname", Fname);
                        parentInfo.put("mname", Mname);
                        parentInfo.put("fprofession", Fprofession);
                        parentInfo.put("fdesig", Fdesig);
                        parentInfo.put("fworkplace", Fworkplace);
                        parentInfo.put("fmobile", Fmobile);
                        parentInfo.put("femail", Femail);
                        parentInfo.put("mprofession", Mprofession);
                        parentInfo.put("mworkplace", Mworkplace);
                        parentInfo.put("mdesig", Mdesig);
                        parentInfo.put("mmobile", Mmobile);


                        Log.d(TAG, "onClick: GrNumber is " + profile.getGrno());
                        String[] contents = {"fname", "mname", "fprofession", "fdesig", "fworkplace", "fmobile",
                                "femail", "mprofession", "mworkplace", "mdesig", "mmobile"};

                        StringBuilder sb = new StringBuilder();


                        for (int i = 0; i < 11; i++) {
                            Log.d(TAG, "onClick: " + contents[i]);
                            sb.append(contents[i] + ",");
                        }
                        JSONObject finalObj = new JSONObject();
                        finalObj.put("obj", parentInfo.toString());
                        finalObj.put("contents", sb.toString());
                        finalObj.put("Length", 11);
                        finalObj.put("collectionName", "parentInfo");
                        finalObj.put("grNumber", profile.getGrno());

                        networkUtils.emitSocket("Allinfo", finalObj);
                        networkUtils.listener("Allinfo", getActivity(), getContext(), toast); //success  listener


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        return view;

    }


}
