package college.root.vi12.StudentProfile;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Iterator;

import college.root.vi12.CheckNetwork;
import college.root.vi12.NetworkUtils;
import college.root.vi12.R;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static android.content.Context.CONNECTIVITY_SERVICE;


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


    public FragmentProfile1() {
        // Required empty public constructor
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view=inflater.inflate(R.layout.fragment_profile1, container, false);
        final RealmConfiguration config = new RealmConfiguration.Builder(getContext()).schemaVersion(4).deleteRealmIfMigrationNeeded().build();
        realm.setDefaultConfiguration(config);

        networkUtils = new NetworkUtils() ;

        try {
            socket = networkUtils.get();

            Log.d(TAG, "onCreateView: listener called");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


        CheckNetwork checkNetwork = new CheckNetwork();
        boolean isNetWorkAvailable = checkNetwork.isNetWorkAvailable(getActivity());
        if (!isNetWorkAvailable){
            toast.showToast(getActivity() , "No internet connection");
        }

        realm = Realm.getDefaultInstance();
        name=(EditText)view.findViewById(R.id.etname);
        surname=(EditText)view.findViewById(R.id.etsurname);
        year=(EditText)view.findViewById(R.id.etyear);
        div=(EditText)view.findViewById(R.id.etdiv);
        branch=(EditText)view.findViewById(R.id.etbranch);
        etSem = (EditText)view.findViewById(R.id.etSem);

        save=(Button)view.findViewById(R.id.save);
        back=(Button)view.findViewById(R.id.back);

        profile = new Student_profile();
        profile = realm.where(Student_profile.class).findFirst();
        name.setText(profile.getName());
        surname.setText(profile.getSurname());
        year.setText(profile.getYear());
        div.setText(profile.getDiv());
        branch.setText(profile.getBranch());
        etSem.setText(profile.getSemester());


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
                            profile.setYear(year.getText().toString());
                            profile.setDiv(div.getText().toString());
                            profile.setBranch(branch.getText().toString());
                            profile.setImagePath(String.valueOf(imageuri));
                            profile.setSemester(etSem.getText().toString());
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
                            mybranch = branch.getText().toString();
                            mydiv = div.getText().toString();
                            myname = name.getText().toString();
                            mysurname = surname.getText().toString();
                            myyear = year.getText().toString();
                            sem = etSem.getText().toString();

                            profile.setName(name.getText().toString());
                            profile.setSurname(surname.getText().toString());
                            profile.setYear(year.getText().toString());
                            profile.setDiv(div.getText().toString());
                            profile.setBranch(branch.getText().toString());
                            profile.setSemester(etSem.getText().toString());
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



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),UserProfile.class);
                startActivity(intent);
            }
        });
        return view;




    }




}
