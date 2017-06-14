package college.root.vi12.Miscleneous;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.widget.*;
import android.widget.Toast;

/**
 * Created by root on 14/3/17.
 */

public class MyReciever extends BroadcastReceiver {

    // This Broadcast reciever checks for internet connection constantly in background.
    @Override
    public void onReceive(Context context, Intent intent) {

        boolean isConnected = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

        if(isConnected){
            Toast.makeText(context, "Internet Connection Lost", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(context, "Internet Connected", Toast.LENGTH_LONG).show();
        }


    }
}
