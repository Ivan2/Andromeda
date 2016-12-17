package com.games.andromeda.ui.dialogs;

import android.content.Context;
import android.content.DialogInterface;

public abstract class WarningDialog {

    public abstract void onOk();

    public void show(Context context, String msg) {
        android.app.AlertDialog aD = new android.app.AlertDialog.Builder(context)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Your Server-IP ...")
            .setCancelable(false)
            .setMessage("Error retrieving IP of your Server: " + msg)
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface pDialog, final int pWhich) {
                    onOk();
                }
            })
            .create();
        aD.show();
    }

}
