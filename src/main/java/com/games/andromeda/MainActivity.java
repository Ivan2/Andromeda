package com.games.andromeda;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.games.andromeda.message.ConnectionCloseServerMessage;
import com.games.andromeda.multiplayer.GameClient;
import com.games.andromeda.multiplayer.ServerCreator;
import com.games.andromeda.ui.dialogs.InputIPDialog;
import com.games.andromeda.ui.dialogs.MessageDialog;
import com.games.andromeda.ui.dialogs.WarningDialog;

import org.andengine.extension.multiplayer.protocol.adt.message.IMessage;
import org.andengine.extension.multiplayer.protocol.server.SocketServer;
import org.andengine.extension.multiplayer.protocol.server.connector.SocketConnectionClientConnector;
import org.andengine.extension.multiplayer.protocol.util.MessagePool;
import org.andengine.extension.multiplayer.protocol.util.WifiUtils;
import org.andengine.util.debug.Debug;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    private static final int SERVER_PORT = 8686;
    private static final String LOCALHOST_IP = "127.0.0.1";
    private static final short FLAG_MESSAGE_SERVER_SHOW = 1;

    private static final int DIALOG_CHOOSE_SERVER_OR_CLIENT_ID = 0;
    private static final int DIALOG_ENTER_SERVER_IP_ID = DIALOG_CHOOSE_SERVER_OR_CLIENT_ID + 1;

    private Button start;
    private Button join;
    private Button options;
    private Button exit;
    private static String serverIp = LOCALHOST_IP;

    private SocketServer<SocketConnectionClientConnector> mSocketServer;
    private GameClient client;
    private final MessagePool<IMessage> mMessagePool = new MessagePool<IMessage>();
    //private ClientConnector<SocketConnection> clientConnector;


    /*private void initMessagePool() {
        this.mMessagePool.registerMessage(FLAG_MESSAGE_SERVER_SHOW, MoveShipServerMessage.class);
    }*/

    private void initServer() {

            start.setEnabled(false);

            join.setEnabled(false);

            ServerCreator creator = new ServerCreator(this);
            mSocketServer = creator.getServer(SERVER_PORT);
            mSocketServer.start();

    }

    private void initClient() {
        if (mSocketServer == null) {
            try {
                DatagramSocket ds = new DatagramSocket(9999);
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                ds.receive(packet);
                client = GameClient.createInstance(this, packet.getAddress().getHostAddress(), SERVER_PORT);
                Thread thread = new Thread(client);
                thread.setDaemon(true);
                thread.start();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            client = GameClient.createInstance(this, serverIp, SERVER_PORT);
            Thread thread = new Thread(client);
            thread.setDaemon(true);
            thread.start();
        }

    }

    public void toast(final String pMessage) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, pMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (mSocketServer != null) {
            try {
                mSocketServer.sendBroadcastServerMessage(new ConnectionCloseServerMessage());
            } catch (final IOException e) {
                Debug.e(e);
            }
            mSocketServer.terminate();
        }

        if (client != null)
            if (client.getConnector() != null) {
                client.getConnector().terminate();
            }

        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_main);
        LinearLayout main = (LinearLayout) findViewById(R.id.main_menu);
        main.setBackground(getResources().getDrawable(R.drawable.background));
        start = (Button)findViewById(R.id.button_start);
        join = (Button)findViewById(R.id.button_join);
        options = (Button)findViewById(R.id.button_options);
        exit = (Button)findViewById(R.id.button_exit);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initServer();
                try {
                    Thread.sleep(500);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
                initClient();
            }
        });
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initClient();
            }
        });
        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.finish();
            }
        });
    }

}
