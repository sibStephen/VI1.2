package college.root.vi12.Student.StudentProfile;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import college.root.vi12.Miscleneous.Toast;
import college.root.vi12.NetworkTasks.NetworkUtils;
import college.root.vi12.R;
import college.root.vi12.Student.Realm.Student_profile;
import io.realm.Realm;
import io.socket.client.Socket;

import static android.app.Activity.RESULT_OK;

public class FragmentCertificates extends Fragment {

    ImageButton imgBtn10th, imgBtn12;
    int GALLERY_REQUEST = 1;
    Uri mimageUri;
    String TAG = "Test";
    Bitmap bitmap;
    NetworkUtils networkUtils;
    Toast toast;
    Socket socket;
    Student_profile profile;
    Realm realm;
    int[] image;
    public FragmentCertificates() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((EditProfileActivity)getActivity()).setActionBarTitle("Certificate Details");

        return inflater.inflate(R.layout.fragment_fragment_certificates, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imgBtn10th = (ImageButton)view.findViewById(R.id.img10th);
        imgBtn12 = (ImageButton)view.findViewById(R.id.imgbtn12th);



        realm = Realm.getDefaultInstance();
        image = new int[5];
        for (int i=0; i<5 ; i++)
            image[i] =0;

        profile = realm.where(Student_profile.class).findFirst();


        if (profile.getCertificate12th()!=null)
        imgBtn12.setImageURI(Uri.parse(profile.getCertificate12th()));
        if (profile.getCertificate10th()!=null)
        imgBtn10th.setImageURI(Uri.parse(profile.getCertificate10th()));

        imgBtn10th.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                image[0] = 1;
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, GALLERY_REQUEST);

            }
        });


        imgBtn12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                image[1] = 1;
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, GALLERY_REQUEST);


            }
        });

    }







    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {

            mimageUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), mimageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }


            UploadImage upload = null;
            if(image[0]== 1){
                imgBtn10th.setImageURI(mimageUri);
                image[0] =1;
                 upload = new UploadImage(bitmap, "10thCertificate");
                profile = realm.where(Student_profile.class).findFirst();
                if (profile!=null){

                    realm.beginTransaction();

                    profile.setCertificate10th(String.valueOf(mimageUri));

                    realm.commitTransaction();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {

                            realm.copyToRealmOrUpdate(profile);

                        }
                    });

                }else {
                    Log.d(TAG, "onActivityResult: profile is null");
                }



            }else if (image[1]== 1){


                Log.d(TAG, "onActivityResult: into 12th image btn");
                imgBtn12.setImageURI(mimageUri);
                image[1] =0;

                profile = realm.where(Student_profile.class).findFirst();
                if (profile!=null){

                    realm.beginTransaction();

                    profile.setCertificate12th(String.valueOf(mimageUri));

                    realm.commitTransaction();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {

                            realm.copyToRealmOrUpdate(profile);

                        }
                    });

                }else {
                    Log.d(TAG, "onActivityResult: profile is null");
                }
                 upload = new UploadImage(bitmap, "12thCertificate");

            }


            upload.execute();






        }
        }


        public class UploadImage extends AsyncTask<Void , Void , Void>{

            Bitmap image ;
            String name ;

            public UploadImage(Bitmap image , String name){

                this.image = image;
                this.name = name;
            }

            @Override
            protected Void doInBackground(Void... params) {

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG , 40 , byteArrayOutputStream);
                final String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray() , Base64.DEFAULT);
               // Log.d(TAG, "doInBackground: Encoded image is "+encodedImage );


                networkUtils = new NetworkUtils();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        profile = new Student_profile();
                        profile = realm.where(Student_profile.class).findFirst();
                        if (profile==null)
                            Log.d(TAG, "doInBackground: profile is null");



                        try {

                            socket = networkUtils.initializeSocketAsync();
                            JSONObject basicUserDetails = new JSONObject();
                            basicUserDetails.put(name , encodedImage);
                            basicUserDetails.put("Timestamp",networkUtils.getLocalIpAddress()+" "+ new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime() ));



                            String[] contents = {name , "Timestamp"};
                            StringBuilder sb = new StringBuilder();
                            for (int j=0 ; j<contents.length; j++){
                                Log.d(TAG, "onClick: "+contents[j]);
                                sb.append(contents[j]+",");
                            }
                            JSONObject finalObj = new JSONObject();
                            finalObj.put("obj" , basicUserDetails.toString());
                            finalObj.put("contents" , sb.toString());
                            finalObj.put("Length" , contents.length);
                            finalObj.put("collectionName" , "Certificates");
                            finalObj.put("grNumber" , profile.getGrno());


                            networkUtils.emitSocket("Allinfo",finalObj);
                            networkUtils.listener("Allinfo" , getActivity() , getContext(), toast); //success  listener







                        }  catch (JSONException e) {
                            Log.d(TAG, "onClick: json error "+e.getMessage());
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }


                    }
                });








                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }




}
