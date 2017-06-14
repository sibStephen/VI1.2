package college.root.vi12.StudentProfile;


import android.app.ActionBar;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import college.root.vi12.NetworkTasks.CheckNetwork;
import college.root.vi12.NetworkTasks.NetworkUtils;
import college.root.vi12.R;
import college.root.vi12.Toast;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.socket.client.Socket;


public class FragmentProfile3 extends Fragment  implements AdapterView.OnItemSelectedListener{

    EditText address,district,state,city,landline,pincode,area;
    Button btnSave;
    String Country;
    Student_profile profile;
    String TAG = "Test";
    Spinner spinner_country;
    Realm realm;
    NetworkUtils networkUtils;
    Toast toast;
    Socket socket;

    public FragmentProfile3() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_profile3, container, false);

        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((EditProfileActivity)getActivity()).setActionBarTitle("Residential Details");



        realm = Realm.getDefaultInstance();

        address=(EditText)view.findViewById(R.id.et_address);
        district = (EditText)view.findViewById(R.id.etdistrict);
        city = (EditText)view.findViewById(R.id.city_in);
        landline = (EditText)view.findViewById(R.id.landline_in);
        state = (EditText)view.findViewById(R.id.etstate);
        pincode = (EditText)view.findViewById(R.id.pincode_in);
        area = (EditText)view.findViewById(R.id.area_in);
        btnSave = (Button)view.findViewById(R.id.btnNation);



        spinner_country = (Spinner) view.findViewById(R.id.spinner2);
        Locale[] locales = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList<String>();
        for (Locale locale : locales) {
            String country = locale.getDisplayCountry();
            if (country.trim().length() > 0 && !countries.contains(country)) {
                countries.add(country);
            }
        }
        Collections.sort(countries);
        for (String country : countries) {
            System.out.println(country);
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, countries);
        // set the view for the Drop down list
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // set the ArrayAdapter to the spinner
        spinner_country.setAdapter(dataAdapter);
        spinner_country.setSelection(37);

        System.out.println("# countries found: " + countries.size());


        realm = Realm.getDefaultInstance();
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
            toast = new Toast();
            toast.showToast(getActivity() , "No internet connection");
        }
        profile = new Student_profile();
        profile = realm.where(Student_profile.class).findFirst();

        if (profile!=null){
            Log.d(TAG, "onViewCreated: profile is not null ");
            landline.setText(profile.getLandlineNumber());
            address.setText(profile.getAddress());
            state.setText(profile.getState());
            pincode.setText(profile.getPincode());
            city.setText(profile.getCity());
            district.setText(profile.getDistrict());


        }else{
            Log.d(TAG, "onViewCreated: profile is Null");
        }


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                profile = new Student_profile();
                profile = realm.where(Student_profile.class).findFirst();
                if(profile==null) {
                    Log.d(TAG, "save: profile is null");

                    realm.beginTransaction();
                    profile = new Student_profile();

                    profile.setLandlineNumber(landline.getText().toString());
                    profile.setAddress(address.getText().toString());
                    profile.setState(state.getText().toString());
                    profile.setCity(city.getText().toString());
                    profile.setPincode(pincode.getText().toString());
                    profile.setNationality(Country);
                    profile.setDistrict(district.getText().toString());

                    realm.commitTransaction();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.copyToRealmOrUpdate(profile);
                        }
                    });


                }else{
                    realm.beginTransaction();
                    profile = new Student_profile();

                    profile.setLandlineNumber(landline.getText().toString());
                    profile.setAddress(address.getText().toString());
                    profile.setState(state.getText().toString());
                    profile.setCity(city.getText().toString());
                    profile.setPincode(pincode.getText().toString());
                    profile.setNationality(Country);
                    profile.setDistrict(district.getText().toString());

                    realm.commitTransaction();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.copyToRealmOrUpdate(profile);
                        }
                    });
                }// end of if else



                try {
                    networkUtils = new NetworkUtils();
                    socket = networkUtils.initializeSocketAsync();
                    JSONObject basicUserDetails = new JSONObject();
                    basicUserDetails.put("landline" , landline.getText().toString());
                    basicUserDetails.put("address" , address.getText().toString());
                    basicUserDetails.put("state" , state.getText().toString());
                    basicUserDetails.put("city" , city.getText().toString());
                    basicUserDetails.put("pincode" , pincode.getText().toString());
                    basicUserDetails.put("Country" , Country);
                    basicUserDetails.put("district" , district.getText().toString());





                    String[] contents = {"landline" , "address" , "state", "city"
                            , "pincode" , "Country" , "Country" , "district"};
                    StringBuilder sb = new StringBuilder();
                    for (int j=0 ; j<contents.length; j++){
                        Log.d(TAG, "onClick: "+contents[j]);
                        sb.append(contents[j]+",");
                    }
                    JSONObject finalObj = new JSONObject();
                    finalObj.put("obj" , basicUserDetails.toString());
                    finalObj.put("contents" , sb.toString());
                    finalObj.put("Length" , contents.length);
                    finalObj.put("collectionName" , "residentialInfo");
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
        });







    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


       Country =  parent.getItemAtPosition(position).toString();
        Log.d(TAG, "onItemSelected: Country selected is " + Country);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
