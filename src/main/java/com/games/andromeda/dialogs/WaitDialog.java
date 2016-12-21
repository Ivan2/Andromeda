package com.games.andromeda.dialogs;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.games.andromeda.MainActivity;
import com.games.andromeda.R;
import com.games.andromeda.multiplayer.Client;
import com.games.andromeda.ui.UI;

/**
 * Created by 1038844 on 21.12.2016.
 */

public class WaitDialog extends DialogFragment {

    private View view;
    private Context context;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().setTitle("");
        view = inflater.inflate(R.layout.wait_dialog, null);
        context = inflater.getContext();
        ((TextView)view.findViewById(R.id.waitText)).setText(MainActivity.type);
        return view;
    }

}
