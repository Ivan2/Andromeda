package com.games.andromeda.dialogs;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.games.andromeda.GameActivity;
import com.games.andromeda.R;
import com.games.andromeda.multiplayer.Client;
import com.games.andromeda.ui.UI;

/**
 * Created by 1038844 on 20.12.2016.
 */

public class QuitDialog extends DialogFragment implements View.OnClickListener {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().setTitle("");
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
