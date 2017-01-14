package college.root.vi12;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.interceptors.ParseStethoInterceptor;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by root on 5/1/17.
 */

public class StarterApplication extends Application {

    String TAG = "Test";

    @Override
    public void onCreate(){
        super.onCreate();

        getKeyHash();
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Log.d(TAG, "onCreate: Application called");


        // Add your initialization code here

        RealmConfiguration configuration = new RealmConfiguration.Builder(StarterApplication.this).deleteRealmIfMigrationNeeded().schemaVersion(4).build();
        Realm.setDefaultConfiguration(configuration);
        Log.d(TAG , "Realm set");

        try{

            Parse.initialize(new Parse.Configuration.Builder(this)
            .applicationId("@strings/parse_app_id")
            .clientKey("@strings/parse_client_key")
            .server("https://parseapi.back4app.com/").build()
            );
            Log.d(TAG, "onCreate: initialization done");




        }catch (Exception e){

        }






      //  ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        // Optionally enable public read access.
        // defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

    }
    private void getKeyHash() {
        try {
            Log.d(TAG, "getKeyHash: in getKeyHash Method");
            PackageInfo info = getPackageManager().getPackageInfo("scm.dominwong4.back4appandroidtutorial", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            //something
            Log.d(TAG, "getKeyHash: nothing found");
        } catch (NoSuchAlgorithmException e) {
            //something
            Log.d(TAG, "getKeyHash: no such algo");
        }
    }
}
