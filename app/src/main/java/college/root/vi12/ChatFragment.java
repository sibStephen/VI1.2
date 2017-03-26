package college.root.vi12;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import college.root.vi12.StudentProfile.Student_profile;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class ChatFragment extends Fragment {

    ImageView mSend;
    EditText et_message;
    NetworkUtils networkUtils;
    Socket socket;
    Student_profile profile ;
    static Realm realm;
    Toast toast;
    String TAG = "Test";
    Thread threadChat;
    boolean threadStarted = false;
    CheckNetwork checkNetwork ;

    public ChatFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        realm = Realm.getDefaultInstance();
        profile = new Student_profile();
        toast = new Toast();
        networkUtils = new NetworkUtils();
        checkNetwork = new CheckNetwork();
        Log.d(TAG, "onCreateView: views initialized");
        return inflater.inflate(R.layout.fragment_chat, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // main code here...


        mSend = (ImageView)getActivity().findViewById(R.id.imgSend);
        et_message = (EditText)getActivity().findViewById(R.id.etmessage);


        try {
            socket = networkUtils.get();
            socket.on("messages" , handlemessage);
            socket.on("ChatResult" , handleChat);
            Log.d(TAG, "onViewCreated: losteners called");

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


        threadChat = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Log.d(TAG, "run: listener activated now ...");
                    socket = networkUtils.get();

                   /* socket.on("messages", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {

                            Log.d(TAG, "call: in messages listener");
                            JSONObject messageObj = (JSONObject) args[0];


                            try {
                                 String name =(String) messageObj.get("Name");
                                String message = (String )messageObj.get("Message");
                                Log.d(TAG, "call: message received is :"+name + " : "+message);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });*/


                    socket.on("ChatResult", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {

                            Log.d(TAG, "call: in chaResult listener");
                            JSONArray array = (JSONArray) args[0];
                            Log.d(TAG, "call: length is elements is "+array.length());
                            for (int i =0 ; i < array.length(); i++){
                                JSONObject obj ;
                                try {
                                    obj = array.getJSONObject(i);
                                    String name =(String) obj.get("Name");
                                    String message = (String )obj.get("Message");
                                    Log.d(TAG, "call: message recieved is :"+name + " : "+message);

                                 } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        }
                    });
                } catch (URISyntaxException e) {
                    Log.d(TAG, "run: Error in socket");
                }

            }
        });


        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!checkNetwork.isNetWorkAvailable(getActivity())){
                    toast.showToast(getActivity() , "No internet connection");
                }else {

                    String message = et_message.getText().toString();
                    if ( message != " " && message !=""){

                        // send data on server
                        profile = realm.where(Student_profile.class).findFirst();
                        if (profile == null){
                            toast.showToast(getActivity() , "Error ...");
                            Log.d(TAG, "onClick: profile is null");
                        }
                        String name = profile.getName();
                        Log.d(TAG, "onClick: "+profile.getName() + "     "+ profile.getFull_name());
                        JSONObject data = new JSONObject();
                        try {
                            data.put("Message" , message);
                            data.put("Name" , name);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        networkUtils.emitSocket("chatSection" , data);





                        et_message.setText("");

                    }else {

                    }
                }




            }
        });

    }


    private Emitter.Listener handleChat = new Emitter.Listener() {
        @Override
        public void call(Object... args) {


            Log.d(TAG, "call: in chaResult listener");
            JSONArray array = (JSONArray) args[0];
            Log.d(TAG, "call: length is elements is "+array.length());
            for (int i =0 ; i < array.length(); i++){
                JSONObject obj ;
                try {
                    obj = array.getJSONObject(i);
                    String name =(String) obj.get("Name");
                    String message = (String )obj.get("Message");
                    Log.d(TAG, "call: message recieved is :"+name + " : "+message);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }
    };

    private  Emitter.Listener handlemessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

          
                    Log.d(TAG, "call: in messages listener");
                    JSONObject messageObj = (JSONObject) args[0];


                    try {
                        String name =(String) messageObj.get("Name");
                        String message = (String )messageObj.get("Message");
                        Log.d(TAG, "call: message received is :"+name + " : "+message);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                
            

        }
    };



}
