package com.example.sudoku;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.os.Bundle;

public class PrefsActivity extends AppCompatActivity {
    public static final String OPT_MUSIC = "music";

    public static final boolean OPT_MUSIC_DEF = true;
    public static final String OPT_HINTS = "hints";

    public static final boolean OPT_HINTS_DEF = true;
    public static final String OPT_EDIT = "edit";

    public static final boolean OPT_EDIT_DEF = true;

    public static boolean getHints(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(OPT_HINTS, OPT_HINTS_DEF);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prefs);

        getSupportActionBar().setTitle("Settings");

        if (findViewById(R.id.settings_container) != null) {
            if (savedInstanceState != null)
            {
                return;
            }
            PrefsFragment frag = new PrefsFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.settings_container, frag).commit();

        }
    }
    public static boolean getMusic(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(OPT_MUSIC, OPT_MUSIC_DEF);
    }
    public static boolean getEdit(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(OPT_EDIT,OPT_EDIT_DEF);
//        return false;
    }
}