package college.root.vi12.Faculty.Background_Services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import college.root.vi12.Faculty.FacultyProfile.FacultyProfileRealm;
import college.root.vi12.Faculty.FacultySubjects.RechedulingActivity;
import college.root.vi12.NetworkTasks.NetworkUtils;
import college.root.vi12.R;
import io.realm.Realm;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class FacultyFetchUpdateService extends IntentService {

    Realm realm;
    FacultyProfileRealm profile;
    NetworkUtils networkUtils;
    Socket socketsend;
    String TAG = "Test";
    Context context = this;

    private static final String ACTION_FOO = "college.root.vi12.Faculty.action.FOO";
    private static final String ACTION_BAZ = "college.root.vi12.Faculty.action.BAZ";

    private static final String EXTRA_PARAM1 = "college.root.vi12.Faculty.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "college.root.vi12.Faculty.extra.PARAM2";

    public FacultyFetchUpdateService() {
        super("MyIntentService");
    }

    public static void startActionFoo(Context context, String param1, String param2) {

        Intent intent = new Intent(context, FacultyFetchUpdateService.class);

        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
        }

        Log.d(TAG, "onHandleIntent: Intent is received here");
        startBackgroundService();



    }


    private void handleActionFoo(String param1, String param2) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void handleActionBaz(String param1, String param2) {
        throw new UnsupportedOperationException("Not yet implemented");
    }



    private void startBackgroundService() {

        try {
            realm = Realm.getDefaultInstance();
            profile = realm.where(FacultyProfileRealm.class).findFirst();

            if (profile != null){
                networkUtils = new NetworkUtils();
                JSONObject object = new JSONObject();
                socketsend = networkUtils.get();
                Log.d(TAG, "onBind: EID is "+profile.getEid());
                object.put("Code" , profile.getEid());
                Log.d(TAG, "startBackgroundService: the object is "+object);
                socketsend.emit("LookForUpdates" , object.toString());
                socketsend.on("updates" , lookForUpdatesListener );
            }else {
                Log.d(TAG, "startBackgroundService: profile is null");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }


    Emitter.Listener lookForUpdatesListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject object = null;

            Log.d(TAG, "call: in lookForUpdatesListener");
            if (args[0].equals("0")){


//                Utils.toast(getApplicationContext(), "No Updates for this session...");
            }else {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationCompat.Builder mBuilder = null;
                Log.d(TAG, "call: the object is "+args[0]);
                JSONArray array = (JSONArray) args[0];
                try {
                    object = array.getJSONObject(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final JSONObject finalObject = object;

                try {
                    if (finalObject.getString("RequestType").equals("LecRescheduleRequest")){
                        // its a request for this faculty to accept the rescheduling request

                        Intent intent1 = new Intent(context , RechedulingActivity.class);
                        intent1.putExtra("jsondata" , finalObject.toString());
                        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        PendingIntent pendingIntent= PendingIntent.getActivity(context , 100, intent1, PendingIntent.FLAG_UPDATE_CURRENT);



                        try {
                            String mSubject =" Prof."+ finalObject.getString("Staff") + " wants to reschedule lecture with you";
                            String subject = "Subject : "+finalObject.getString("Subject");
                            String time = "Time : "+finalObject.getString("Time");
                            String loc = "Location : "+finalObject.getString("Location");

                           mBuilder = new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.drawable.note)
                                    .setContentTitle("Lecture Reschedule")
                                    .setContentText("Click here for details....")
                                    .setContentIntent(pendingIntent);
                            NotificationCompat.InboxStyle inboxStyle =
                                    new NotificationCompat.InboxStyle();

                            String[] events = {mSubject,subject,time,loc , "Click here for details"};
                            inboxStyle.setBigContentTitle("Rescheduling details");
                            for (int i=0; i < events.length; i++) {
                                inboxStyle.addLine(events[i]);
                            }
                            mBuilder.setStyle(inboxStyle);

//                    Utils.toast(context, "Loading Updates for this session...");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }






                    }else if (finalObject.getString("RequestType").equals("LecRescheduleResponse")){
                        // its a response of the requested faculty on lec rescheduling

                        Intent intent1 = new Intent(context , RechedulingActivity.class);
                        intent1.putExtra("jsondata" , finalObject.toString());
                        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        PendingIntent pendingIntent= PendingIntent.getActivity(context , 100, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
                        String isApproved ;

                        if (finalObject.getString("isAccepted").equals("0")){

                            isApproved = "not accepted";
                        }else {

                            isApproved = "accepted";
                        }

                        mBuilder = new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.note)
                                .setContentTitle("Lecture Response")
                                .setContentText("Drag to see....")
                                .setContentIntent(pendingIntent);
                        NotificationCompat.InboxStyle inboxStyle =
                                new NotificationCompat.InboxStyle();

                        String[] events = {"Prof "+ finalObject.getString("RescFaculty")  , "has "+isApproved + " your request"};
                        inboxStyle.setBigContentTitle("Rescheduling details");
                        for (int i=0; i < events.length; i++) {
                            inboxStyle.addLine(events[i]);
                        }
                        mBuilder.setStyle(inboxStyle);







                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                notificationManager.notify(200 , mBuilder.build());


            }

        }
    };
}
