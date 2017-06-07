package college.root.vi12.StudentProfile;


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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;

import college.root.vi12.R;

import static android.app.Activity.RESULT_OK;

public class FragmentCertificates extends Fragment {

    ImageButton imgBtn10th, imgBtn12;
    int GALLERY_REQUEST = 1;
    Uri mimageUri;
    String TAG = "Test";
    Bitmap bitmap;

    public FragmentCertificates() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_certificates, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imgBtn10th = (ImageButton)view.findViewById(R.id.img10th);
        imgBtn12 = (ImageButton)view.findViewById(R.id.imgbtn12th);



        imgBtn10th.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            imgBtn10th.setImageURI(mimageUri);
            try {
                 bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), mimageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            UploadImage upload = new UploadImage(bitmap, "profilepic");
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
                image.compress(Bitmap.CompressFormat.JPEG , 100 , byteArrayOutputStream);
                String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray() , Base64.DEFAULT);
                Log.d(TAG, "doInBackground: Encoded image is "+encodedImage );

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }


}
