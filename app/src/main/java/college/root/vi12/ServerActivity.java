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

import io.realm.Realm;
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
    Thread threadConnect , thread1 , threadSend;
    AutoCompleteTextView completetv;
    SocketAddress sockaddr;
    Button button , btnDissconnect , btnSend ,btnProfile, btnLogin;
    StringBuilder result ;
    TextView tv_messages;
    EditText et_message;
    JSONObject jobj;
    public String ipaddress = "";
    EditText et_ip;
    Realm realm;
    IPAddess ipAddess;

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

        realm = Realm.getDefaultInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(ServerActivity.this , MainActivity.class));

            }
        });




        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{

                    try {
                        ipaddress = completetv.getText().toString();

                       /* ipAddess = new IPAddess();
                        ipAddess = realm.where(IPAddess.class).findFirst();
                        if (ipAddess == null){
                            ipAddess = new IPAddess();
                            realm.beginTransaction();
                            ipAddess.setIpaddress(ipaddress);
                            realm.commitTransaction();
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    Log.d(TAG, "execute: ip address saved in realm ");
                                    realm.copyToRealmOrUpdate(ipAddess);
                                    threadConnect.start();


                                }
                            });

                        }else {
                            realm.beginTransaction();
                            ipAddess.setIpaddress(ipaddress);
                            realm.commitTransaction();
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {

                                    realm.copyToRealmOrUpdate(ipAddess);
                                    threadConnect.start();


                                }
                            });

                        }*/

                        NetworkUtils networkUtils = new NetworkUtils();
                        socket = networkUtils.getSocketAsync();


                        //threadConnect.start();
                        Log.d(TAG, "onClick: ip address is : "+ipaddress);




                    } catch (Exception e){

                    }

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









      threadConnect = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    Log.d(TAG, "run: in thread connect ....");


/*                    ipAddess = realm.where(IPAddess.class).findFirst();
                    ipaddress = ipAddess.getIpaddress();*/
                    if (ipAddess==null)
                        Log.d(TAG, "run: ipAdress is null");
                    Log.d(TAG, "run: ip address is "+ipaddress);
                    Log.d(TAG, "run: ip address is "+ipaddress);
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







}
