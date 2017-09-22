package college.root.vi12.Miscleneous;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.LinkedList;
import java.util.List;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by root on 16/9/17.
 */

public class Utils {



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

}
