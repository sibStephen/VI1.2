package college.root.vi12.Student.StudentProfile;

import android.content.ContentResolver;
import android.content.DialogInterface;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import college.root.vi12.Miscleneous.Toast;
import college.root.vi12.NetworkTasks.CheckNetwork;
import college.root.vi12.NetworkTasks.NetworkUtils;
import college.root.vi12.R;
import college.root.vi12.Student.Realm.Student_profile;
import io.realm.Realm;
import io.socket.client.Socket;


public class FragmentProfile1 extends Fragment {

    Realm realm;
    EditText name,surname,year,div,branch,grno , etSem;
    Button save,back;
    Student_profile profile;
    Uri imageuri;
    String TAG = "Test";
    String myname , mysurname , myyear, mydiv, mybranch, mygrno , sem;
    Socket socket;
    Toast toast;
    NetworkUtils networkUtils;
    Spinner spGender , spBranch, spSem, spYear , spDiv;
   ArrayAdapter<String> yearAdapter;
    ArrayAdapter<String> SemAdapter;
    ArrayAdapter<String> dataAdapter,divAdapter,branchAdapter;
    int pos=0;


    public FragmentProfile1() {
        // Required empty public constructor
    }



    public  void initializeViews(View view){
        networkUtils = new NetworkUtils() ;

        CheckNetwork checkNetwork = new CheckNetwork();
        boolean isNetWorkAvailable = CheckNetwork.isNetWorkAvailable(getActivity());
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
        spYear = (Spinner)view.findViewById(R.id.epYear);
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




        List<String> listOfDiv = new ArrayList<String>();
        listOfDiv.add("A");
        listOfDiv.add("B");
        listOfDiv.add("C");

        divAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item,listOfDiv);
        divAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDiv.setAdapter(divAdapter);

        List<String> listOfYear = new ArrayList<>();
        listOfYear.add("FE");
        listOfYear.add("SE");
        listOfYear.add("TE");
        listOfYear.add("BE");
        listOfYear.add("ME");

        yearAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item,listOfYear);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spYear.setAdapter(yearAdapter);



        save=(Button)view.findViewById(R.id.save);

        profile = new Student_profile();

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



      initializeViews(view);

        profile = realm.where(Student_profile.class).findFirst();

        if(profile != null){
            name.setText(profile.getName());
            surname.setText(profile.getSurname());
            pos =  branchAdapter.getPosition(profile.getBranch());
            spBranch.setSelection(pos);

            pos = yearAdapter.getPosition(profile.getYear());
            spYear.setSelection(pos);

            pos = divAdapter.getPosition(profile.getDiv());
            spDiv.setSelection(pos);

            pos = SemAdapter.getPosition(profile.getSemester());
            spSem.setSelection(pos);

        }


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
                            Toast toast = new Toast();
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
                                basicUserDetails.put("Timestamp",networkUtils.getLocalIpAddress()+" "+ new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(Calendar.getInstance().getTime() ));





                                String[] contents = {"my_name" , "surname" , "branch", "year"
                                        , "div","sem","Timestamp" };
                                StringBuilder sb = new StringBuilder();
                                for (int j=0 ; j<contents.length; j++){
                                    Log.d(TAG, "onClick: "+contents[j]);
                                    sb.append(contents[j]+",");
                                }
                                JSONObject finalObj = new JSONObject();
                                finalObj.put("obj" , basicUserDetails.toString());
                                finalObj.put("contents" , sb.toString());
                                finalObj.put("Length" , contents.length);
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
