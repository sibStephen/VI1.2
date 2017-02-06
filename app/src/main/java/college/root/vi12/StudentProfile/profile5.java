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
 * {@link profile5.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link profile5#newInstance} factory method to
 * create an instance of this fragment.
 */
public class profile5 extends Fragment {

    Realm realm;
    EditText fname,mname,fprofession,fdesig,fworkplace,fmobile,femail,mprofession,mworkplace,mdesig,mmobile;
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

    public profile5() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment profile5.
     */
    // TODO: Rename and change types and number of parameters
    public static profile5 newInstance(String param1, String param2) {
        profile5 fragment = new profile5();
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
        View view=inflater.inflate(R.layout.fragment_profile5, container, false);
        RealmConfiguration config = new RealmConfiguration.Builder(getContext()).schemaVersion(4).deleteRealmIfMigrationNeeded().build();
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
                else
                {
                    //  oldgrno=profile.getGrno();
                    profile = realm.where(Student_profile.class).findFirst();
                    realm.beginTransaction();

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
