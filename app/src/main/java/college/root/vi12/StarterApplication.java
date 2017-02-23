package college.root.vi12;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.interceptors.ParseStethoInterceptor;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

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


        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());

        RealmInspectorModulesProvider.builder(this)
                .withFolder(getCacheDir())
                .withMetaTables()
                .withDescendingOrder()
                .withLimit(1000)
                .databaseNamePattern(Pattern.compile(".+\\.realm"))
                .build();
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
