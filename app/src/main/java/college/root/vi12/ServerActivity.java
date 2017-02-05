package college.root.vi12;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.SocketAddress;
import java.net.URISyntaxException;

import io.socket.client.IO;
//import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;

//import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ServerActivity extends AppCompatActivity {

    io.socket.client.Socket socket = null;
    String TAG = "Test";
    String localhost = "192.168.1.38";
    int portNumber  = 2000;
    Thread thread , thread1 , threadSend;
    AutoCompleteTextView completetv;
    SocketAddress sockaddr;
    Button button , btnDissconnect , btnSend ,btnProfile, btnLogin;
    StringBuilder result ;
    TextView tv_messages;
    EditText et_message;
    JSONObject jobj;
    String ipaddress = "http://192.168.1.38:8083/";
    EditText et_ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        completetv = (AutoCompleteTextView) findViewById(R.id.autotv);
        String[] countries = getResources().getStringArray(R.array.countries);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ServerActivity.this , android.R.layout.simple_list_item_1 , countries);
        completetv.setAdapter(adapter);
        tv_messages = (TextView) findViewById(R.id.tv_messages);
        et_message = (EditText)findViewById(R.id.et_message);
        btnSend = (Button)findViewById(R.id.btn_send);

        button = (Button)findViewById(R.id.connectBtn);
        btnDissconnect = (Button)findViewById(R.id.btn_disconnect);
        btnProfile = (Button) findViewById(R.id.btnProfile);
        btnLogin = (Button)findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(ServerActivity.this , MainActivity.class));

            }
        });


        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(ServerActivity.this , ProfileActivity.class));

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{

                    try {
                        thread.start();


                    } catch (Exception e){

                    }
                  //  new Connect().execute("http://192.168.1.38:1000/");

                }catch (Exception e){
                    Log.d(TAG, "onClick: error in on click "+e.getMessage() );                    
                }
                

            }
        });


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            threadSend.start();

            }


        });

        btnDissconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thread1.start();


            }
        });


        threadSend = new Thread(new Runnable() {
            @Override
            public void run() {

                String message = et_message.getText().toString().trim();
                jobj = new JSONObject();
                try {
                    jobj.put("name", "Shubham");
                    jobj.put("message" , message);

                    socket.emit("input" ,jobj);

                }catch (JSONException e){
                    Log.d(TAG, "run: error in json string");
                }


            }
        });


        thread1 = new Thread(new Runnable() {
            @Override
            public void run() {

                sockaddr = new InetSocketAddress(localhost, portNumber);

                try {
                   // socket = new Socket(localhost , portNumber);
                    socket = IO.socket(ipaddress);
                    socket.close();
                   // socket.emit("connection" , null);
                    if (socket.connected()){
                        Log.d(TAG, "run: Socket not closed....");
                    }else {
                        Log.d(TAG, "run: socket  closed...");
                    }




                }
                 catch (URISyntaxException e) {
                    e.printStackTrace();
                }


            }
        });





      thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {

                    sockaddr = new InetSocketAddress(localhost, portNumber);

                    Log.d(TAG, "run: ip address is "+ipaddress);
                   // socket = new Socket(localhost , portNumber);
                    socket = IO.socket("http://192.168.1.38:8083/");
                    socket.connect();

                    Log.d(TAG, "run:connected.........");
                  //  socket.on()
                   // socket.connect();
                  if (!socket.connected())
                     {
                       Log.d(TAG, "onCreate: Socket is connected");
                       //  socket.
                         // display all the messages .. from server
                         socket.on("output", new Emitter.Listener() {
                             @Override
                             public void call(Object... args) {

                                 JSONArray data = (JSONArray) args[0];
                                 String message;
                                 try {
                                     for (int i=0 ; i <data.length() ; i++){

                                         message = data.getString(i).toString();
                                         Log.d(TAG, "call: "+message);

                                     }

                                     // addMessage(message);

                                 } catch (JSONException e) {
                                     Log.e("result", "Error : JSONException");
                                     return;
                                 } catch (ClassCastException e) {
                                     Log.e("result", "Error : ClassCastException");
                                     e.printStackTrace();
                                     return;
                                 }
                             }
                         });



                   }else
                 {
                       Log.d(TAG, "onCreate: Socket is not connected");

                   }








                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });







    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.connect:
                AlertDialog.Builder b = new AlertDialog.Builder(ServerActivity.this);
                LayoutInflater inflater=ServerActivity.this.getLayoutInflater();
                //this is what I did to added the layout to the alert dialog
                View layout=inflater.inflate(R.layout.layout,null);
                b.setView(layout);
                  et_ip = (EditText) findViewById(R.id.et_ip);
                b.setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        ipaddress = et_ip.getText().toString().trim();
                        Log.d(TAG, "onClick: input ip address is "+ipaddress);
                        thread.start();
                    }
                });
                b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();
                    }
                });

                b.show();
                return true;

           }
        return false;

    }




        public class Connect extends AsyncTask<String , Void , String>{


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... strings) {

                try {
                    Log.d(TAG, "doInBackground: strings[0] contains "+strings[0]);
                    URL url = new URL(strings[0]);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setReadTimeout(10000); // miliseconds
                    urlConnection.setConnectTimeout(10000);
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setRequestProperty("Content-Type " , "application/json");
                    urlConnection.connect();
                    Log.d(TAG, "doInBackground: connected......");




                    /*InputStream input = urlConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    String line;
                    result = new StringBuilder();
                    while( (line = reader.readLine()) != null  ){
                        Log.d(TAG, "doInBackground: in loop");
                        result.append(line).append("\n");

                    }*/



                } catch (MalformedURLException e) {
                    Log.d(TAG, "doInBackground: error"+e.getMessage());
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.d(TAG, "doInBackground: error"+e.getMessage());
                    e.printStackTrace();
                }


                return result.toString() ;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                tv_messages.setText(result);


            }
        }

}
