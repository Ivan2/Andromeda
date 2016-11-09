package com.games.andromeda.ui.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

public abstract class InputIPDialog {

    public abstract void onOk(String ip);

    public void show(Context context) {
        final EditText ipEditText = new EditText(context);
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle("Enter Server-IP ...")
                .setCancelable(false)
                .setView(ipEditText)
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface pDialog, final int pWhich) {
                        onOk(ipEditText.getText().toString());
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        dialog.show();
    }

}
