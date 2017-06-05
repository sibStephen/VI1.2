package college.root.vi12;

import android.content.Context;
import android.util.Log;
import android.widget.*;
import android.widget.Toast;

/**
 * Created by root on 24/5/17.
 */

public class L {

    String TAG = "Test";
    Context context;

    public void M(String message ){

        Log.d(TAG, " "+message);

    }

    public void S(Context context , String message){
        this.context = context;

        android.widget.Toast.makeText(context , message , Toast.LENGTH_SHORT).show();
    }
}
