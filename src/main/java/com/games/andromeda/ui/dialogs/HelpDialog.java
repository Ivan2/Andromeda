package com.games.andromeda.ui.dialogs;

import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.games.andromeda.Phases;
import com.games.andromeda.R;
import com.games.andromeda.logic.phases.CommonHandlingStrategy;
import com.games.andromeda.logic.phases.FleetMovingStrategy;
import com.games.andromeda.logic.phases.FleetPreparationStrategy;
import com.games.andromeda.logic.phases.LevelPreparationStrategy;
import com.games.andromeda.logic.phases.MoneySpendingStrategy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by 1038844 on 14.12.2016.
 */

public class HelpDialog extends DialogFragment implements View.OnClickListener {

    private TextView helpText;
    private Context context;
    private final String OPTIONS_FILE_NAME = "options";

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Phases phases = Phases.getInstance();
        getDialog().setTitle(phases.getPhase().getTextDescription());
        View v = inflater.inflate(R.layout.help_dialog, null);
        v.findViewById(R.id.checkShow).setOnClickListener(this);
        v.findViewById(R.id.buttonOk).setOnClickListener(this);
        helpText = new TextView(inflater.getContext());
        helpText = (TextView)v.findViewById(R.id.helpText);
        if (phases.getPhase() instanceof FleetMovingStrategy)
            helpText.setText(R.string.fleet_moving_strategy);
        if (phases.getPhase() instanceof FleetPreparationStrategy)
            helpText.setText(R.string.fleet_preparattion_strategy);
        if (phases.getPhase() instanceof MoneySpendingStrategy)
            helpText.setText(R.string.money_spending_strategy);
        if (phases.getPhase() instanceof LevelPreparationStrategy)
            helpText.setText(R.string.level_preparation_strategy);
        context = inflater.getContext();
        //v.setBackgroundColor(Color.);
        return v;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.checkShow:
                String str = readFile();
                if (str.charAt(1) == '1')
                    writeFile("01");
                else
                    writeFile("11");
                break;
        }
        dismiss();
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }
    private void writeFile(String content) {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    context.openFileOutput(OPTIONS_FILE_NAME,MODE_PRIVATE)));
            bw.write(content);
            bw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String readFile() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    context.openFileInput("options")));
            String str = "";
            str = br.readLine();
            return str;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "11";
    }

}
