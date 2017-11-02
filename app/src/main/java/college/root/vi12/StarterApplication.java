package college.root.vi12;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import com.parse.Parse;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import college.root.vi12.Miscleneous.Utils;
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
        Utils.loadHashMap();
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Log.d(TAG, "onCreate: Application called");


        // Add your initialization code here


        RealmConfiguration configuration = new RealmConfiguration.Builder().build();

                /*new RealmConfiguration.Builder(StarterApplication.this).deleteRealmIfMigrationNeeded().schemaVersion(4).build();*/
        Realm.setDefaultConfiguration(configuration);
        Log.d(TAG , "Realm set");


        Utils.loadHashMap();
    }







    private void getKeyHash() {
        try {
            Log.d(TAG, "getKeyHash: in getKeyHash Method");
            PackageInfo info = getPackageManager().getPackageInfo("college.root.vi12", PackageManager.GET_SIGNATURES);
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
