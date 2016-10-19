package com.games.andromeda;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.andengine.extension.multiplayer.protocol.util.WifiUtils;
import org.andengine.util.debug.Debug;

import java.io.IOException;
import java.net.UnknownHostException;

public class Main2Activity extends AppCompatActivity {

    private static final int DIALOG_ENTER_SERVER_IP_ID = 1;

    private Button start;
    private Button join;
    private Button options;
    private Button exit;
    private static Boolean isServer;
    private static String serverIp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        LinearLayout main = (LinearLayout) findViewById(R.id.main_menu);
        main.setBackground(getResources().getDrawable(R.drawable.background));
        start = (Button)findViewById(R.id.button_start);
        join = (Button)findViewById(R.id.button_join);
        options = (Button)findViewById(R.id.button_options);
        exit = (Button)findViewById(R.id.button_exit);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isServer = true;
                try {

                    AlertDialog aD = new AlertDialog.Builder(Main2Activity.this)
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setTitle("Your Server-IP ...")
                            .setCancelable(false)
                            .setMessage("The IP of your Server is:\n" + WifiUtils.getWifiIPv4Address(Main2Activity.this))
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface pDialog, final int pWhich) {
                                    Intent start = new Intent(Main2Activity.this, MainActivity.class);
                                    startActivity(start);
                                }
                            })
                            .create();
                    aD.show();

                } catch (final UnknownHostException e) {
                    AlertDialog aD =  new AlertDialog.Builder(Main2Activity.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Your Server-IP ...")
                            .setCancelable(false)
                            .setMessage("Error retrieving IP of your Server: " + e)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface pDialog, final int pWhich) {

                                }
                            })
                            .create();
                    aD.show();
                }
            }
        });
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText ipEditText = new EditText(Main2Activity.this);
                AlertDialog aD = new AlertDialog.Builder(Main2Activity.this)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setTitle("Enter Server-IP ...")
                        .setCancelable(false)
                        .setView(ipEditText)
                        .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface pDialog, final int pWhich) {
                                Main2Activity.this.serverIp = ipEditText.getText().toString();
                                isServer = false;
                                Intent start = new Intent(Main2Activity.this, MainActivity.class);
                                startActivity(start);
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .create();
                aD.show();

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
                Main2Activity.this.finish();
            }
        });

    }


    public static Boolean getIsServer()
    {
        return isServer;
    }
    public static String getServerIP() { return serverIp;}
}
