package college.root.vi12.NetworkTasks;

import android.app.Activity;
import android.content.Context;
import android.text.format.Formatter;
import android.util.Log;

import org.json.JSONObject;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.util.Enumeration;

import college.root.vi12.Miscleneous.Toast;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by root on 2/2/17.
 */

public class NetworkUtils {
    // this class will create a socket get connected to the ip address and return d socket


    public Socket socket;

    public Thread threadConnect , threadListen, threadDisconnect;
    public  String TAG = "Test";
    public  String collectionName;
    public  JSONObject object;
    public String ipaddress = "http://192.168.43.98:8083/";
    Toast toast;


    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String ip = Formatter.formatIpAddress(inetAddress.hashCode());
                        Log.i(TAG, "***** IP="+ ip);
                        return ip;
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e(TAG, ex.toString());
        }
        return null;
    }


    public Socket createSocketNameSpace(String nameSpace) throws URISyntaxException {

        socket = IO.socket(ipaddress+nameSpace);
        socket.connect();
        if (socket.connected()){
            Log.d(TAG, "createSocketNameSpace: socket connected");
        }else {
            socket.connect();
        }
        return  socket;

    }


    public void disconnectSocket() throws URISyntaxException {

        socket = get();
        socket.disconnect();
        if (socket.connected()){
            Log.d(TAG, "run: socket still connected");
        }else {
            Log.d(TAG, "disconnectSocket: socket disconnected");
        }

    }

    public void disconnectSocketAsync(){
        threadDisconnect = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket.disconnect();
                    if (socket.connected()){

                        Log.d(TAG, "run: socket still connected");
                    }else {
                        Log.d(TAG, "disconnectSocket: socket disconnected");
                    }
                }catch (Exception e){

                }


            }
        });
    }


    public  Socket getSocketAsync() throws URISyntaxException {
        threadConnect = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = IO.socket(ipaddress);
                    Log.d(TAG, "run: socket created successfully");
                    socket.connect();
                    Log.d(TAG, "run: socket connected successfully");

                    if (socket.connected()){
                        Log.d(TAG, "run: socket is connected ");
                    }else {
                        Log.d(TAG, "run: socket is not connected..");
                    }

                } catch (URISyntaxException e) {
                    Log.d(TAG, "run: Error "+e.getMessage());
                }


            }
        });
        threadConnect.start();
        return  socket;
    }

    public Socket get() throws URISyntaxException {
        socket = IO.socket(ipaddress);
        Log.d(TAG, "run: socket created successfully");
       if (!socket.connected()){
           socket.connect();

       }
        return socket;

    }


    public  Socket initializeSocketAsync() throws URISyntaxException {
        threadConnect = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = IO.socket(ipaddress);
                    Log.d(TAG, "run: socket created successfully");
                    Log.d(TAG, "run: socket connected successfully");

                    if (socket.connected()){
                        Log.d(TAG, "run: socket is connected ");
                    }else {
                        Log.d(TAG, "run: socket is not connected..");
                    }

                } catch (URISyntaxException e) {
                    Log.d(TAG, "run: Error "+e.getMessage());
                }


            }
        });
        threadConnect.start();
        return  socket;
    }

public void emitSocket(final String collectionName , final JSONObject object){

    this.collectionName = collectionName;
    this.object = object;
    
   Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                socket = IO.socket(ipaddress);

               if (socket.connected()){

               }else {
                   socket.connect();

               }
                socket.emit(collectionName , object.toString());
                Log.d(TAG, "run: data sent");
               // disconnectSocket();
            } catch (URISyntaxException e) {
                Log.d(TAG, "run: error "+e.getMessage());
                e.printStackTrace();
            }

        }
    });

    thread.start();
}


public  void listener(final String name  , final Activity activity , final Context context , final Toast toast1) {

    Log.d(TAG, "listener: Listener was called ");


    threadListen = new Thread(new Runnable() {
        @Override
        public void run() {

            try {
                socket=  get();

                socket.on(name, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {


                        int result = (int)args[0];
                        Log.d(TAG, "call: in listener thread");
                        Log.d(TAG, "call: value returned is "+result);
                        if (result == 1){

                            toast = new Toast();
//                            toast1.dismissProgressDialog(activity);
                            toast.showToast(activity , "Details saved successfully ");


                            Log.d(TAG, "call: results saved successfully on to server");
                        }else {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    android.widget.Toast.makeText(context, "Error in saving data .. ", android.widget.Toast.LENGTH_SHORT).show();


                                }
                            });
                            Log.d(TAG, "call: Error saving details on server");
                        }
                    }
                });
            } catch (URISyntaxException e) {
                e.printStackTrace();
                Log.d(TAG, "run: error "+e.getMessage());
            }



        }
    });

    threadListen.start();

}





}
