package college.root.vi12.Miscleneous;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import college.root.vi12.Faculty.FacultyLogin.FacultyLogin;
import college.root.vi12.Faculty.FacultyProfile.FacultyProfileRealm;
import college.root.vi12.MainActivity;
import college.root.vi12.NetworkTasks.NetworkUtils;
import io.realm.Realm;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by root on 16/9/17.
 */

public class Utils {




   public static HashMap<String , String> mapOfFaculty = new HashMap<>();

    public static HashMap<String , String> mapFacultyID = new HashMap<>();
    public static String[] days = {"Monday" , "Tuesday" , "Wednesday" , "Thursday" , "Friday" , "Saturday"};
    static boolean  isConnected = false;
    private static String TAG = "connection";


    public Utils() throws URISyntaxException {
    }


    public static void loadHashMap(){
        mapOfFaculty.put("Shailesh Thaware" , "E103");
        mapOfFaculty.put("shubham purandare" , "E104");
        mapFacultyID.put("E103" , "shubham purandare");
        mapFacultyID.put("E104" , "Shailesh Thaware");

    }


    public static FacultyProfileRealm createFacultyProfile(){

        Log.d(TAG, "createFacultyProfile: in function ");
        Realm realm;
        realm = Realm.getDefaultInstance();
        FacultyProfileRealm profileRealm ;
        profileRealm = realm.where(FacultyProfileRealm.class).equalTo("eid" , "E103").findFirst();
        if (profileRealm == null){
            Log.d(TAG, "createFacultyProfile: profile is null ");
            profileRealm = new FacultyProfileRealm();
            profileRealm.setEid("E103");
            final FacultyProfileRealm finalProfileRealm = profileRealm;
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    realm.copyToRealmOrUpdate(finalProfileRealm);
                    Log.d(TAG, "execute: faculty realmm object created");
                }
            });
        }else {
            Log.d(TAG, "createFacultyProfile: faculty realm obj is already created");
        }

        return  profileRealm;
    }


    public  static boolean isNetWorkAvailable(Activity activity){
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null;
    }

    public static String[] removeElements(String[] input, String deleteMe) {
        List result = new LinkedList();

        for(String item : input)
            if(!deleteMe.equals(item))
                result.add(item);

        return (String[]) result.toArray(input);
    }


    public static void toast(Context context , String msg ){
        android.widget.Toast.makeText(context , msg , Toast.LENGTH_SHORT).show();

    }

    public  static JSONObject getMatchingJsonObj(JSONArray array ,String key , String value) throws JSONException {


           for (int i=0; i<array.length() ; i++){
               JSONObject object = array.getJSONObject(i);
               if (object.get(key).equals(value)){
                   return object;
               }
           }

        return  null;

    }


    public static boolean arrayContainstoken(JSONArray array, String token) throws JSONException {

        for (int i=0 ; i<array.length(); i++){
            if (array.getString(i).equals(token)){
                return true;
            }
        }


        return false;
    }


    public static int getNumberFromWeek(String[] days, String dayOfTheWeek) {


        for (int i=0; i<days.length ; i++){
            if (days[i].equals(dayOfTheWeek)){
                return  i;
            }
        }
        return 0;

    }

    public static void isConnectionEstablished() {
        Socket socket;
        Log.d(TAG, "isConnectionEstablished: ");
               try {
            socket = IO.socket(NetworkUtils.ipaddress);
            Log.d(TAG, "isConnectionEstablished: trying connection");
                   socket.connect();
            socket.once(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {

                    Log.d(TAG, "call: socket is connected...");
                    isConnected = true;


                }
            });




                   if (isConnected){
                       Log.d(TAG, "isConnectionEstablished: connected.............");
                   }else
                   {
                       Log.d(TAG, "isConnectionEstablished: not conneted");
                   }

        } catch (URISyntaxException e) {
            e.printStackTrace();
            Log.d(TAG, "isConnectionEstablished: cannot connect...");


        }
    }

    public static void somethingIsNull(Activity activity ,  String user){
        toast(activity , "Please login again...");

        if (user.equals("Faculty")){
            activity.startActivity(new Intent(activity  , FacultyLogin.class));
        }else
        if (user.equals("Student")){
            activity.startActivity(new Intent(activity  , MainActivity.class));
        }

        activity.finish();
    }


}
