package college.root.vi12.StudentProfile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import college.root.vi12.NetworkTasks.NetworkUtils;
import college.root.vi12.R;
import college.root.vi12.Miscleneous.Toast;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.socket.client.Socket;


public class FragmentProfile2 extends Fragment {


    Realm realm;
    EditText email_pri,email_sec,birth,sub_caste,uni_area,full_name,
            pref,income,aadhar,nationality,blood,mobile,mstatus,emcontact;
    Button save;
    String TAG="Test";
    Student_profile profile;
    NetworkUtils networkUtils;
    Spinner spReligion;
    Socket socket;
    int religion;
    AutoCompleteTextView mother_ton;
    String[] language ={"Marathi" , "English" , "Hindi"};


    String semail_pri,semail_sec,sreligion,smother_ton,sbirth,ssub_caste,suni_area,sfull_name,spref,sincome,saadhar,snationality,sblood,smobile,smstatus,semcontact;
    public FragmentProfile2() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_profile2, container, false);
        ((EditProfileActivity)getActivity()).setActionBarTitle("All Details");




        RealmConfiguration config = new RealmConfiguration.Builder(getContext()).schemaVersion(4).deleteRealmIfMigrationNeeded().build();
        realm.setDefaultConfiguration(config);
        spReligion = (Spinner)view.findViewById(R.id.religion);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext() ,R.array.religion, android.R.layout.simple_dropdown_item_1line );
        spReligion.setAdapter(adapter);
        spReligion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                religion = adapterView.getSelectedItemPosition();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        realm = Realm.getDefaultInstance();
        email_pri=(EditText)view.findViewById(R.id.email_pri);
        email_sec=(EditText)view.findViewById(R.id.email_sec);
        spReligion=(Spinner)view.findViewById(R.id.religion);
        mother_ton=(AutoCompleteTextView)view.findViewById(R.id.actvMotherTongue);
        birth=(EditText)view.findViewById(R.id.birth);
        sub_caste=(EditText)view.findViewById(R.id.sub_caste);
        uni_area=(EditText)view.findViewById(R.id.uni_area);
        full_name=(EditText)view.findViewById(R.id.full_name);
        pref=(EditText)view.findViewById(R.id.pref);
        income=(EditText)view.findViewById(R.id.income);
        aadhar=(EditText)view.findViewById(R.id.aadhar);
        nationality=(EditText)view.findViewById(R.id.nationality);
        blood=(EditText)view.findViewById(R.id.blood);
        mobile=(EditText)view.findViewById(R.id.mobile);
        mstatus=(EditText)view.findViewById(R.id.mstatus);
        emcontact=(EditText)view.findViewById(R.id.emcontact);





        ArrayAdapter<String> languageAdapter = new ArrayAdapter<String>
                (getContext(),android.R.layout.select_dialog_item,language);

        mother_ton.setThreshold(1);
        mother_ton.setAdapter(languageAdapter);

        profile = new Student_profile();
        profile = realm.where(Student_profile.class).findFirst();

        if(profile!=null) {
            email_pri.setText(profile.getEmail_pri());
            email_sec.setText(profile.getEmail_sec());
            mother_ton.setText(profile.getMother_ton());
            birth.setText(profile.getBirth_place());
            sub_caste.setText(profile.getSub_caste());
            uni_area.setText(profile.getUni_area());
            full_name.setText(profile.getFull_name());
            pref.setText(profile.getPref_no());
            income.setText(profile.getIncome());
            aadhar.setText(profile.getAadhar());
            nationality.setText(profile.getNationality());
            blood.setText(profile.getBlood());
            mobile.setText(profile.getMobile());
            mstatus.setText(profile.getMstatus());
            emcontact.setText(profile.getEmcontact());
        }


        save=(Button)view.findViewById(R.id.save_details);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                profile = new Student_profile();
                profile = realm.where(Student_profile.class).findFirst();
                if(profile==null) {
                    Log.d(TAG, "save: profile is null");
                    //     profile = realm.createObject(Student_profile.class);
                    realm.beginTransaction();
                    profile = new Student_profile();

                    profile.setEmail_pri(email_pri.getText().toString());
                    profile.setEmail_sec(email_sec.getText().toString());
                    profile.setMother_ton(mother_ton.getText().toString());
                    profile.setBirth_place(birth.getText().toString());
                    profile.setSub_caste(sub_caste.getText().toString());
                    profile.setUni_area(uni_area.getText().toString());
                    profile.setFull_name(full_name.getText().toString());
                    profile.setPref_no(pref.getText().toString());
                    profile.setIncome(income.getText().toString());
                    profile.setAadhar(aadhar.getText().toString());
                    profile.setNationality(nationality.getText().toString());
                    profile.setBlood(blood.getText().toString());
                    profile.setMobile(mobile.getText().toString());
                    profile.setMstatus(mstatus.getText().toString());
                    profile.setEmcontact(emcontact.getText().toString());

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

                    Toast toast = new Toast();
                    toast.showProgressDialog(getActivity() , "Saving details....");
                    //  oldgrno=profile.getGrno();
                    profile = realm.where(Student_profile.class).findFirst();
                    realm.beginTransaction();
                    profile.setEmail_pri(email_pri.getText().toString());
                    profile.setEmail_sec(email_sec.getText().toString());

                    profile.setMother_ton(mother_ton.getText().toString());
                    profile.setBirth_place(birth.getText().toString());
                    profile.setSub_caste(sub_caste.getText().toString());
                    profile.setUni_area(uni_area.getText().toString());
                    profile.setFull_name(full_name.getText().toString());
                    profile.setPref_no(pref.getText().toString());
                    profile.setIncome(income.getText().toString());
                    profile.setAadhar(aadhar.getText().toString());
                    profile.setNationality(nationality.getText().toString());
                    profile.setBlood(blood.getText().toString());
                    profile.setMobile(mobile.getText().toString());
                    profile.setMstatus(mstatus.getText().toString());
                    profile.setEmcontact(emcontact.getText().toString());
                    realm.commitTransaction();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.copyToRealmOrUpdate(profile);

                        }
                    });


                    semcontact= emcontact.getText().toString();
                    semail_pri= email_pri.getText().toString();
                    semail_sec= email_sec.getText().toString();
                    saadhar= aadhar.getText().toString();
                    sbirth= birth.getText().toString();
                    sfull_name= full_name.getText().toString();
                    sincome= income.getText().toString();
                    smobile= mobile.getText().toString();
                    smother_ton= mother_ton.getText().toString();
                    smstatus= mstatus.getText().toString();
                    snationality= nationality.getText().toString();
                    ssub_caste= sub_caste.getText().toString();

                    sblood= blood.getText().toString();
                    spref= pref.getText().toString();
                    suni_area = uni_area.getText().toString();

                    try {
                        networkUtils = new NetworkUtils();
                        socket = networkUtils.initializeSocketAsync();
                        JSONObject basicUserDetails = new JSONObject();
                        basicUserDetails.put("semcontact" , semcontact);
                        basicUserDetails.put("semail_pri" , semail_pri);
                        basicUserDetails.put("semail_sec" , semail_sec);
                        basicUserDetails.put("saadhar" , saadhar);
                        basicUserDetails.put("sbirth" , sbirth);
                        basicUserDetails.put("sfull_name" , sfull_name);
                        basicUserDetails.put("sincome" , sincome);
                        basicUserDetails.put("smobile" , smobile);
                        basicUserDetails.put("smother_ton" , smother_ton);
                        basicUserDetails.put("smstatus" , smstatus);
                        basicUserDetails.put("snationality" , snationality);
                        basicUserDetails.put("ssub_caste" , ssub_caste);
                        basicUserDetails.put("sreligion" , sreligion);
                        basicUserDetails.put("sblood" , sblood);
                        basicUserDetails.put("spref" , spref);
                        basicUserDetails.put("suni_area" , suni_area);





                        String[] contents = {"semcontact" , "semail_pri" , "semail_sec", "saadhar"
                                , "sfull_name" , "sincome" , "sfull_name" , "smobile"
                                , "smother_ton" , "smstatus" , "snationality"
                                , "sreligion", "sreligion", "sblood", "spref", "suni_area"};
                        StringBuilder sb = new StringBuilder();
                        for (int j=0 ; j<contents.length; j++){
                            Log.d(TAG, "onClick: "+contents[j]);
                            sb.append(contents[j]+",");
                        }
                        JSONObject finalObj = new JSONObject();
                        finalObj.put("obj" , basicUserDetails.toString());
                        finalObj.put("contents" , sb.toString());
                        finalObj.put("Length" , contents.length);
                        finalObj.put("collectionName" , "personalDetails");
                        finalObj.put("grNumber" , profile.getGrno());

                        networkUtils.emitSocket("Allinfo",finalObj);

                        networkUtils.disconnectSocketAsync();
                        networkUtils.listener("Allinfo" , getActivity() , getContext(), toast); //success  listener







                    }  catch (JSONException e) {
                        Log.d(TAG, "onClick: json error " + e.getMessage());
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }


            }
        });
        return view;


    }


}
