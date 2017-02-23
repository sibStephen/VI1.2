package college.root.vi12;

import android.app.Activity;
import android.app.ProgressDialog;

/**
 * Created by root on 14/2/17.
 */

public class Toast {

    public ProgressDialog dialog;

    public Toast(){

    }

    public void showToast(final Activity activity , final String message){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                android.widget.Toast.makeText(activity , message , android.widget.Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void showProgressDialog(final Activity activity , final String title){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog = new ProgressDialog(activity);
                dialog.setMessage(title);
               // dialog.show();
            }
        });
    }

    public void dismissProgressDialog(final Activity activity){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog = new ProgressDialog(activity);
                dialog.dismiss();

            }
        });
       }
}
