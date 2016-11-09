package com.games.andromeda.ui.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public abstract class MessageDialog {

    public abstract void onOk();

    public void show(Context context, String ip) {
        AlertDialog aD = new AlertDialog.Builder(context)
            .setIcon(android.R.drawable.ic_dialog_info)
            .setTitle("Your Server-IP ...")
            .setCancelable(false)
            .setMessage("The IP of your Server is:\n" + ip)
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
