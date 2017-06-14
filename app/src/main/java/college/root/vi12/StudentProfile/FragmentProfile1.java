package college.root.vi12.StudentProfile;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import college.root.vi12.NetworkTasks.CheckNetwork;
import college.root.vi12.NetworkTasks.NetworkUtils;
import college.root.vi12.R;
import college.root.vi12.Toast;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.socket.client.Socket;

import static college.root.vi12.R.id.spinner2;


public class FragmentProfile1 extends Fragment {

    Realm realm;
    EditText name,surname,year,div,branch,grno , etSem;
    Button save,back;
    Student_profile profile;
    Uri imageuri;
    String TAG = "Test";
    String myname , mysurname , myyear, mydiv, mybranch, mygrno , sem;
    Socket socket;
    college.root.vi12.Toast toast;
    NetworkUtils networkUtils;
    Spinner spGender , spBranch, spSem, spYear , spDiv;
    ArrayAdapter<String>   yearAdapter;
    ArrayAdapter<String> SemAdapter;
    ArrayAdapter<String> dataAdapter,divAdapter,branchAdapter;


    public FragmentProfile1() {
        // Required empty public constructor
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        final RealmConfiguration config = new RealmConfiguration.Builder(getContext()).schemaVersion(4).deleteRealmIfMigrationNeeded().build();
        realm.setDefaultConfiguration(config);

        networkUtils = new NetworkUtils() ;



        //getActivity().getString(R.array.religion);
        try {
            socket = networkUtils.get();

            Log.d(TAG, "onCreateView: listener called");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


        CheckNetwork checkNetwork = new CheckNetwork();
        boolean isNetWorkAvailable = checkNetwork.isNetWorkAvailable(getActivity());
        if (!isNetWorkAvailable){
            toast = new Toast();
            toast.showToast(getActivity() , "No internet connection");
        }

        realm = Realm.getDefaultInstance();
        name=(EditText)view.findViewById(R.id.etname);
        surname=(EditText)view.findViewById(R.id.etsurname);
        spBranch = (Spinner)view.findViewById(R.id.spBranch);
        spGender = (Spinner)view.findViewById(R.id.spgender);
        spSem = (Spinner)view.findViewById(R.id.spSem);
        spDiv = (Spinner)view.findViewById(R.id.spDiv);
        spYear = (Spinner)view.findViewById(R.id.spYear);
        List<String> list = new ArrayList<String>();
        list.add("Male");
        list.add("Female");
        dataAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item,list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGender.setAdapter(dataAdapter);

        List<String> listOfBranches = new ArrayList<String>();
        listOfBranches.add("Computer");
        listOfBranches.add("IT");
        listOfBranches.add("Mechanical");
        listOfBranches.add("Civil");
        listOfBranches.add("E&TC");
        listOfBranches.add("Other");
        branchAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item,listOfBranches);
        branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBranch.setAdapter(branchAdapter);

        List<String> listOfSem = new ArrayList<String>();
        listOfSem.add("Sem1");
        listOfSem.add("Sem2");

        SemAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item,listOfSem);
        SemAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSem.setAdapter(SemAdapter);

        List<String> lstOfYear = new ArrayList<String>();
        lstOfYear.add("FE");
        lstOfYear.add("SE");
        lstOfYear.add("TE");
        lstOfYear.add("BE");
        lstOfYear.add("ME");


        yearAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item,lstOfYear);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
     //   spYear.setAdapter(yearAdapter);


        List<String> listOfDiv = new ArrayList<String>();
        listOfDiv.add("A");
        listOfDiv.add("B");
        listOfDiv.add("C");

        divAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item,listOfDiv);
        divAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDiv.setAdapter(divAdapter);


        save=(Button)view.findViewById(R.id.save);

        profile = new Student_profile();
        profile = realm.where(Student_profile.class).findFirst();
        name.setText(profile.getName());
        surname.setText(profile.getSurname());


        imageuri=Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + getResources().getResourcePackageName(R.drawable.profile_def)
                + '/' + getResources().getResourceTypeName(R.drawable.profile_def) + '/' + getResources().getResourceEntryName(R.drawable.profile_def) );








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
                            profile.setUid(0);
                            profile.setName(name.getText().toString());
                            profile.setSurname(surname.getText().toString());
                            profile.setYear(spYear.getSelectedItem().toString());
                            profile.setDiv(spDiv.getSelectedItem().toString());
                            profile.setBranch(spBranch.getSelectedItem().toString());
                            profile.setImagePath(String.valueOf(imageuri));
                            profile.setSemester(spSem.getSelectedItem().toString());
                            realm.commitTransaction();

                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.copyToRealmOrUpdate(profile);
                                }
                            });
                        }
                        else
                        {
                            college.root.vi12.Toast toast = new college.root.vi12.Toast();
                            toast.showProgressDialog(getActivity() , "Saving details...");
                            profile = realm.where(Student_profile.class).findFirst();
                            realm.beginTransaction();
                            mygrno = profile.getGrno(); // get GR number as its the primary key
                            mybranch = spBranch.getSelectedItem().toString();
                            mydiv =spDiv.getSelectedItem().toString();
                            myname = name.getText().toString();
                            mysurname = surname.getText().toString();
                            myyear = spYear.getSelectedItem().toString();
                            sem = spSem.getSelectedItem().toString();

                            profile.setName(name.getText().toString());
                            profile.setSurname(surname.getText().toString());
                            profile.setYear(myyear);
                            profile.setDiv(mydiv);
                            profile.setBranch(mybranch);
                            profile.setSemester(sem);
                            realm.commitTransaction();
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.copyToRealmOrUpdate(profile);

                                }
                            });
                            // send data to d server
                            Log.d(TAG, "onClick: name is "+myname);
                            Log.d(TAG, "onClick: gr number  is "+mygrno);
                            Log.d(TAG, "onClick: branch is "+mybranch);
                            Log.d(TAG, "onClick: year is "+myyear);
                            Log.d(TAG, "onClick: div is "+mydiv);
                            Log.d(TAG, "onClick: surname  is "+mysurname);


                            try {
                                socket = networkUtils.initializeSocketAsync();
                                JSONObject basicUserDetails = new JSONObject();
                                basicUserDetails.put("my_name" , myname);
                                basicUserDetails.put("surname" , mysurname);
                                basicUserDetails.put("branch" , mybranch);
                                basicUserDetails.put("year" , myyear);
                                basicUserDetails.put("GRNumber" , mygrno);
                                basicUserDetails.put("div" , mydiv);
                                basicUserDetails.put("sem" , sem);

                                String[] contents = {"my_name" , "surname" , "branch", "year"
                                        , "div","sem" };
                                StringBuilder sb = new StringBuilder();
                                for (int j=0 ; j<contents.length; j++){
                                    Log.d(TAG, "onClick: "+contents[j]);
                                    sb.append(contents[j]+",");
                                }
                                JSONObject finalObj = new JSONObject();
                                finalObj.put("obj" , basicUserDetails.toString());
                                finalObj.put("contents" , sb.toString());
                                finalObj.put("Length" , sb.length());
                                finalObj.put("collectionName" , "basicUserDetails");
                                finalObj.put("grNumber" , profile.getGrno());

                                networkUtils.emitSocket("Allinfo",finalObj);
                                networkUtils.disconnectSocketAsync();
                                networkUtils.listener("Allinfo" , getActivity() , getContext(), toast); //success  listener







                            }  catch (JSONException e) {
                                Log.d(TAG, "onClick: json error "+e.getMessage());
                            } catch (URISyntaxException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });
                builder.show();


            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((EditProfileActivity)getActivity()).setActionBarTitle("Personal Details");
        View view=inflater.inflate(R.layout.fragment_profile1, container, false);






        return view;




    }




}
