package college.root.vi12.Student.BackgroundServices;

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

import college.root.vi12.NetworkTasks.NetworkUtils;
import college.root.vi12.R;
import college.root.vi12.Student.Realm.Student_profile;
import college.root.vi12.Student.StudentProfile.UserProfile;
import io.realm.Realm;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class StudentIntentService extends IntentService {

    public static final String ACTION_FOO = "college.root.vi12.Student.BackgroundServices.action.FOO";
    public static final String ACTION_BAZ = "college.root.vi12.Student.BackgroundServices.action.BAZ";
    // TODO: Rename parameters
    public static final String EXTRA_PARAM1 = "college.root.vi12.Student.BackgroundServices.extra.PARAM1";
    public static final String EXTRA_PARAM2 = "college.root.vi12.Student.BackgroundServices.extra.PARAM2";
    Realm realm;
    Student_profile profile;
    NetworkUtils networkUtils;
    Socket socketsend;
    String TAG = "Test";
    Context context = this;
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

                if (array.length()!=0) {


                    try {
                        object = array.getJSONObject(0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    final JSONObject finalObject = object;

                    try {


                        if (finalObject.get("RequestType").equals("LecRescheduleResponse")) {
                            // its a response of the requested faculty on lec rescheduling

                            Intent intent1 = new Intent(context, UserProfile.class);
                            intent1.putExtra("jsondata", finalObject.toString());
                            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, intent1, PendingIntent.FLAG_UPDATE_CURRENT);


                            mBuilder = new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.drawable.note)
                                    .setContentTitle("Lecture Response")
                                    .setContentText("Drag to see....")
                                    .setAutoCancel(true)
                                    .setContentIntent(pendingIntent);
                            NotificationCompat.InboxStyle inboxStyle =
                                    new NotificationCompat.InboxStyle();

                            String[] events = {"Lecture of Subject " + finalObject.get("Subject"), "has " + " been rescheduled to " + " Subject " + finalObject.get("RescSubject")};
                            inboxStyle.setBigContentTitle("Rescheduling details");
                            for (String event : events) {
                                inboxStyle.addLine(event);
                            }
                            mBuilder.setStyle(inboxStyle);


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    notificationManager.notify(200, mBuilder.build());

                }
            }

        }
    };

    public StudentIntentService() {
        super("StudentIntentService");
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
        startBackgroundService();

    }

   private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void startBackgroundService() {

        try {
            realm = Realm.getDefaultInstance();
            profile = realm.where(Student_profile.class).findFirst();

            if (profile != null){
                networkUtils = new NetworkUtils();
                JSONObject object = new JSONObject();
                socketsend = networkUtils.get();
                Log.d(TAG, "onBind: EID is "+profile.getGrno());
                object.put("Code" , profile.getGrno());
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

}
