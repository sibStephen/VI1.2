package college.root.vi12.StudentProfile;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import college.root.vi12.R;
import io.realm.Realm;
import io.realm.RealmConfiguration;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentProfile2.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentProfile2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentProfile2 extends Fragment {


    Realm realm;
    EditText email_pri,email_sec,religion,mother_ton,birth,sub_caste,uni_area,full_name,pref,income,aadhar,nationality,blood,mobile,mstatus,emcontact;
    Button save;
    String TAG="Test";
    Student_profile profile;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentProfile2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment profile2.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentProfile2 newInstance(String param1, String param2) {
        FragmentProfile2 fragment = new FragmentProfile2();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_profile2, container, false);
        RealmConfiguration config = new RealmConfiguration.Builder(getContext()).schemaVersion(4).deleteRealmIfMigrationNeeded().build();
        realm.setDefaultConfiguration(config);


        realm = Realm.getDefaultInstance();
        email_pri=(EditText)view.findViewById(R.id.email_pri);
        email_sec=(EditText)view.findViewById(R.id.email_sec);
        religion=(EditText)view.findViewById(R.id.religion);
        mother_ton=(EditText)view.findViewById(R.id.montherton);
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

        profile = new Student_profile();
        profile = realm.where(Student_profile.class).findFirst();

        if(profile!=null) {
            email_pri.setText(profile.getEmail_pri());
            email_sec.setText(profile.getEmail_sec());
            religion.setText(profile.getReligion());
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
                    profile.setReligion(religion.getText().toString());
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
                    //  oldgrno=profile.getGrno();
                    profile = realm.where(Student_profile.class).findFirst();
                    realm.beginTransaction();
                    profile.setEmail_pri(email_pri.getText().toString());
                    profile.setEmail_sec(email_sec.getText().toString());
                    profile.setReligion(religion.getText().toString());
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
                }


            }
        });
        return view;


    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
