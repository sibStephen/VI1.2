package college.root.vi12.StudentProfile;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
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
 * {@link profile1.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link profile1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class profile1 extends Fragment {

    Realm realm;
    EditText name,surname,year,div,branch,grno;
    String oldgrno;
    Button save,back;
    Student_profile profile;
    Uri imageuri;
    String TAG = "Test";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public profile1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment profile1.
     */
    // TODO: Rename and change types and number of parameters
    public static profile1 newInstance(String param1, String param2) {
        profile1 fragment = new profile1();
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

        View view=inflater.inflate(R.layout.fragment_profile1, container, false);
        RealmConfiguration config = new RealmConfiguration.Builder(getContext()).schemaVersion(4).deleteRealmIfMigrationNeeded().build();
        realm.setDefaultConfiguration(config);


        realm = Realm.getDefaultInstance();
        name=(EditText)view.findViewById(R.id.etname);
        surname=(EditText)view.findViewById(R.id.etsurname);
        year=(EditText)view.findViewById(R.id.etyear);
        div=(EditText)view.findViewById(R.id.etdiv);
        branch=(EditText)view.findViewById(R.id.etbranch);

        save=(Button)view.findViewById(R.id.save);
        back=(Button)view.findViewById(R.id.back);

        profile = new Student_profile();
        profile = realm.where(Student_profile.class).findFirst();
        name.setText(profile.getName());
        surname.setText(profile.getSurname());
        year.setText(profile.getYear());
        div.setText(profile.getDiv());
        branch.setText(profile.getBranch());


        imageuri=Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + getResources().getResourcePackageName(R.drawable.profile_def)
                + '/' + getResources().getResourceTypeName(R.drawable.profile_def) + '/' + getResources().getResourceEntryName(R.drawable.profile_def) );


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
                    profile.setUid(0);
                    profile.setName(name.getText().toString());
                    profile.setSurname(surname.getText().toString());
                    profile.setYear(year.getText().toString());
                    profile.setDiv(div.getText().toString());
                    profile.setBranch(branch.getText().toString());
                    profile.setImagePath(String.valueOf(imageuri));
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
                    profile.setName(name.getText().toString());
                    profile.setSurname(surname.getText().toString());
                    profile.setYear(year.getText().toString());
                    profile.setDiv(div.getText().toString());
                    profile.setBranch(branch.getText().toString());
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


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),UserProfile.class);
                startActivity(intent);
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
