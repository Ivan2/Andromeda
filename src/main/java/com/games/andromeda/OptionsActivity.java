package com.games.andromeda;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Switch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class OptionsActivity extends AppCompatActivity {

    private Switch sound;
    private Switch hints;
    private String OPTIONS_FILE_NAME = "options";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        sound = new Switch(this);
        hints = new Switch(this);
        sound = (Switch) findViewById(R.id.soundSwitch);
        hints = (Switch) findViewById(R.id.hintsSwitch);
        String option = readFile();
        if (option == null) {
            option = "11";
            writeFile("11");
        }
        Log.wtf(option,option);
        if (option.equals("10") || option.equals("11"))
        {
            hints.setChecked(true);
        }
        else
            hints.setChecked(false);
        if (option.equals("01") || option.equals("11"))
        {
            sound.setChecked(true);
        }
        else
            hints.setChecked(false);
        sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionChanged();
            }
        });
        hints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionChanged();
            }
        });
    }

    private void optionChanged()
    {
        String str = "";
        if (hints.isChecked())
        {
            str+="1";
        }
        else
            str+="0";
        if (sound.isChecked())
        {
            str+="1";
        }
        else
            str+="0";
        writeFile(str);
        Log.wtf(str,str);
    }


    private String readFile() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    openFileInput(OPTIONS_FILE_NAME)));
            String str = "";
            str = br.readLine();
            return str;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void writeFile(String content) {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    openFileOutput(OPTIONS_FILE_NAME,MODE_PRIVATE)));
            bw.write(content);
            bw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
