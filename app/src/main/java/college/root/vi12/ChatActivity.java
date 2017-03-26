package college.root.vi12;

import android.content.Intent;
import android.graphics.EmbossMaskFilter;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.URISyntaxException;
import java.util.ArrayList;

import college.root.vi12.StudentProfile.Student_profile;
import io.realm.Realm;
import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatActivity extends AppCompatActivity {


    String roomName;
    String roomId;
    TextView tvRoomName;
    NetworkUtils networkUtils;
    Socket socket;
    String TAG = "Test";
    Realm realm;
    Student_profile profile;
    Toast toast;
    String username;
    RecyclerView recyclerView;
    ArrayList<Message> arrayList;
    ChatAdapter adapter;
    EditText et_Message;
    ImageView imgSend;
    Message messageList;
    String ipAddress = "http://192.168.1.103:8083/messages";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        tvRoomName = (TextView) findViewById(R.id.tvRoomname);

        messageList = new Message();

        imgSend = (ImageView) findViewById(R.id.imgSend);
        et_Message = (EditText) findViewById(R.id.etmessage);
        recyclerView = (RecyclerView) findViewById(R.id.rvChat);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(ChatActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();
        Log.d(TAG, "onCreate: Recycler view and adapter set ");

        adapter = new ChatAdapter(arrayList, ChatActivity.this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        Intent intent = getIntent();
        roomName = intent.getStringExtra("RoomName");
        roomId = intent.getStringExtra("RoomId");
        tvRoomName.setText(roomName);
        toast = new Toast();
        realm = Realm.getDefaultInstance();
        profile = new Student_profile();
        networkUtils = new NetworkUtils();
        profile = realm.where(Student_profile.class).findFirst();
        if (profile == null) {
            toast.showToast(ChatActivity.this, "Cant find username.... :(");

        } else {

            username = profile.getName();
            Log.d(TAG, "onCreate: username is " + username);
        }


        tvRoomName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Log.d(TAG, "onClick: in onClick of tv");
                    socket = IO.socket(ipAddress);
                    socket.connect();


                    JSONObject object = new JSONObject();
                    object.put("RoomId" , roomId);
                    object.put("Username" , username);
                    socket.emit("updateList" , object.toString());



                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });



        try {

            socket = IO.socket(ipAddress);

            socket.connect();

            if (socket.connected()) {
                android.widget.Toast.makeText(ChatActivity.this, "Connected..", android.widget.Toast.LENGTH_SHORT).show();

            } else {
                android.widget.Toast.makeText(ChatActivity.this, "Not Connected..", android.widget.Toast.LENGTH_SHORT).show();
                socket.connect();
            }


            JSONObject o = new JSONObject();
            o.put("roomId", roomId);
            o.put("username", username);

            socket.emit("joinRoom", o.toString(), new Ack() {
                @Override
                public void call(Object... args) {

                    boolean isGood = (boolean) args[0];

                    Log.d(TAG, "call: ack is " + isGood);
                    if (isGood) {
                        toast.showToast(ChatActivity.this, "Ack recieved");

                    } else {
                        toast.showToast(ChatActivity.this, "Ack recieved but is false ");

                    }

                }
            });
        } catch (URISyntaxException e) {
            Log.d(TAG, "onCreate: Error " + e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
        }





        imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message = et_Message.getText().toString();
                if (!TextUtils.isEmpty(message)) {
                    try {
                        socket = IO.socket(ipAddress);
                        socket.connect();

                        if (socket.connected()) {
                            android.widget.Toast.makeText(ChatActivity.this, "Connected..", android.widget.Toast.LENGTH_SHORT).show();

                        } else {
                            android.widget.Toast.makeText(ChatActivity.this, "Not Connected..", android.widget.Toast.LENGTH_SHORT).show();
                            socket.connect();
                        }
                        final JSONObject messageObj = new JSONObject();
                        try {
                            messageObj.put("RoomId", roomId);
                            messageObj.put("Username", username);
                            messageObj.put("Message", message);
                            Log.d(TAG, "onClick: posting message");

                            socket.emit("MessageInRoom", messageObj.toString(), new Ack() {
                                @Override
                                public void call(Object... args) {

                                    Log.d(TAG, "call: waiting for ack");

                                    boolean ack = (boolean) args[0];
                                    if (ack) {
                                        toast.showToast(ChatActivity.this, "Ack received");
                                        Log.d(TAG, "call: ack received for message in room");
                                    }

                                }
                            });

                            updateRecyclerView(message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "onClick: error" + e.getMessage());
                        }




                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        startListeners();
    }// onCreate

    public Emitter.Listener messageHandler = new Emitter.Listener() {
        @Override
        public void call(Object... args) {


            Log.d(TAG, "call: message handler called");
            JSONObject message = (JSONObject) args[0];
            try {
                String room = message.getString("RoomId");
                String messRecv = message.getString("Message");
                updateRecyclerView(messRecv);


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };

    public Emitter.Listener updateListHandler = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            Log.d(TAG, "call: update user list handler called");
            JSONArray array = (JSONArray) args[0];
            Log.d(TAG, "call: array contains"+array);
            Log.d(TAG, "call: args contain "+args[0]);

        }
    };


    private void updateRecyclerView(final String message) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                messageList.setMessage(message);
                arrayList.add(messageList);
                adapter = new ChatAdapter(arrayList, ChatActivity.this);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }
        });


      }

    private  void startListeners(){
        socket.on("messageFeed", messageHandler);
        socket.on("updateuserList" , updateListHandler);
    }


}
