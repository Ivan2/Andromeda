package com.games.andromeda.ui.dialogs;

import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.games.andromeda.R;
import com.games.andromeda.multiplayer.Client;
import com.games.andromeda.ui.UI;

public class QuitDialog extends DialogFragment implements View.OnClickListener {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle("");
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View v = inflater.inflate(R.layout.quit_dialog, null);
        v.findViewById(R.id.buttonContinue).setOnClickListener(this);
        v.findViewById(R.id.buttonSurrender).setOnClickListener(this);
        return v;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSurrender:
                UI.getInstance().finishGame();
                Client.getInstance().sendLooseGameMessage();
                break;
        }
        dismiss();
    }
}
