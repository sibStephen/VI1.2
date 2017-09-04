package college.root.vi12.StudentProfile;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import college.root.vi12.R;
import college.root.vi12.StudentProfile.Student_TT.TTRealmObject;
import io.realm.Realm;

/**
 * Created by root on 1/9/17.
 */

public class TTReceiver extends BroadcastReceiver {

    private static final String TAG = "Test";
    Realm realm;
    TTRealmObject ttobject;

    @Override
    public void onReceive(Context context, Intent intent) {



        TTRealmObject ttobject;
        Log.d(TAG, "TTNotification: in TTNotificaton");
        realm = Realm.getDefaultInstance();
        ttobject = realm.where(TTRealmObject.class).findFirst();
        if (ttobject == null){
            Log.d(TAG, "onReceive: ttobject was null");

        }else{
            // check the current task here....

            SimpleDateFormat sdf = new SimpleDateFormat("HH");
            String currentDateandTime = sdf.format(new Date());
            Log.d(TAG, "onReceive: current hour is "+currentDateandTime);
               if (Integer.parseInt(currentDateandTime)>8 && Integer.parseInt(currentDateandTime)< 19)// only during college hours
            {

                // valid ... check lectures

                SimpleDateFormat format = new SimpleDateFormat("EEEE");
                Date d = new Date();
                String dayOfTheWeek = format.format(d);
                Log.d(TAG, "onReceive: today is "+dayOfTheWeek);
                String  str = ttobject.getJsonTTObject();
                try {
                    JSONObject object = new JSONObject(str);
                    Log.d(TAG, "onReceive: tt object is"+object);
                    JSONArray array = object.getJSONArray(dayOfTheWeek);
                    Log.d(TAG, "onReceive: tt for today is "+array);

                    int length = array.length();
                    for (int i=0 ; i<length ; i++){
                        JSONObject currentObject = array.getJSONObject(i);


                        int time = Integer.parseInt(currentObject.getString("Time"));
                        if (time == 14){
                            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                            Intent intent1 = new Intent(context, UserProfile.class);
                            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            PendingIntent pendingIntent= PendingIntent.getActivity(context , 100, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

                            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                                    .setContentIntent(pendingIntent)
                                    .setContentTitle("Next Lecture Updates")
                                    .setContentText("Subject - "+currentObject.getString("Subject") +
                                            "\n Staff - "+currentObject.getString("Staff") + "\n Location - "+currentObject.getString("Location")
                                            +"\n Time - "+currentObject.getString("Time"))
                                    .setSmallIcon(R.drawable.profile)
                                    .setAutoCancel(true);

                            notificationManager.notify(100, builder.build());
                            Log.d(TAG, "TTNotification: current lecture is"+currentObject);
                        }else{
                            Log.d(TAG, "TTNotification: No lecture in this slot");

                        }


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }



    }
}

