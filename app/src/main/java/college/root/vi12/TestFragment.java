package college.root.vi12;


import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.JSONStringer;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import io.socket.client.Ack;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static android.R.attr.data;
import static android.R.attr.logo;


public class TestFragment extends Fragment  {

    Socket socket;
    NetworkUtils networkUtils;
    CheckNetwork checkNetwork;
    Button btnSubmit;
    EditText et_uname;
    Toast toast;
    String TAG = "Test";
    ListView listOfUsernames;
    ArrayList <String> list;
    String name;
    Set<String> s;
   JSONArray array;
    org.json.simple.JSONArray rooms;
  JSONObject object;


    ImageView imgSend;
    int i=0;
     ArrayAdapter<String > adapter;
    int GALLERY_REQUEST =1;

    public TestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        checkNetwork = new CheckNetwork();
        networkUtils = new NetworkUtils();
        toast = new Toast();


        Log.d(TAG, "onCreateView: Views initialized....");
        return inflater.inflate(R.layout.fragment_test, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


     //   btnSubmit = (Button)getActivity().findViewById(R.id.btnSubmit);
        et_uname = (EditText)getActivity().findViewById(R.id.etmessage);
        listOfUsernames = (ListView)getActivity().findViewById(R.id.lvUser);
        imgSend = (ImageView)getActivity().findViewById(R.id.imgSend);

        list = new ArrayList<>();
        adapter = new ArrayAdapter<String>(getContext() , android.R.layout.simple_list_item_1 , list);
        listOfUsernames.setAdapter(adapter);
        try {
            socket = networkUtils.get();
            socket.on("roomNames" , handlerRequset);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


        imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = et_uname.getText().toString();
                if (!TextUtils.isEmpty(username)){

                    JSONObject object = new JSONObject();

                        Random r = new Random();
                        int roomId = r.nextInt(80 - 65) + 65;
                        object.put("rooms" , username);
                        object.put("RoomId" ,roomId );
                        socket.emit("NewRoom", object.toString());



                    et_uname.setText("");
                }
            }
        });


        listOfUsernames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String name = (String) listOfUsernames.getItemAtPosition(i);


                String id = getRoomId(name);

                toast.showToast(getActivity() , name +" "+id);


                Intent intent = new Intent(getContext() , ChatActivity.class);
                intent.putExtra("RoomName", name);
                intent.putExtra("RoomId" , id);
                startActivity(intent);
                getActivity().finish();





            }
        });
    }


    private String getRoomId(String roomName){



        for (i =0 ; i<rooms.size(); i++){

          object   =(JSONObject) rooms.get(i);

            if (object.get("roomName").equals(roomName)){
                String roomID = String.valueOf(object.get("roomId"));
                return  roomID;
            }
        }

        return null;

    }


    private  Emitter.Listener handlerRequset = new Emitter.Listener() {
        @Override
        public void call(Object... args) {


            array = (JSONArray) args[0];

            s = new HashSet<>();


            rooms = new org.json.simple.JSONArray();

            final int size = array.length();
            for ( i=0 ; i<size ; i++){

                try {


                    String reader = (String) array.get(i);
                    JSONParser jsonParser = new JSONParser();
                    JSONObject object = (JSONObject) jsonParser.parse(reader);

                    String name = (String)  object.get("rooms");
                    Log.d(TAG, "call: name is "+object);
                    s.add(name);
                    JSONObject roomObj = new JSONObject();
                    roomObj.put("roomName" , name);
                    roomObj.put("roomId" ,  object.get("RoomId"));
                    rooms.add(roomObj);

                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
            Log.d(TAG, "call: rooms jsonArray consists of "+rooms);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    list.clear();
                    list.addAll(s);
                    adapter.notifyDataSetChanged();

                }
            });
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        socket.disconnect();
    }

    @Override
    public void onPause() {
        super.onPause();
        socket.disconnect();
    }

    private void openGallery(){
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, GALLERY_REQUEST);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
         inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menufragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.attach:
                openGallery();
                break;

            }
            return super.onOptionsItemSelected(item);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }
}
