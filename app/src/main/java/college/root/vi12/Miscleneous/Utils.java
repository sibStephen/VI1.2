package college.root.vi12.Miscleneous;

import android.app.Activity;
import android.net.ConnectivityManager;

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
}
