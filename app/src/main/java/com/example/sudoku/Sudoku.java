package com.example.sudoku;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class Sudoku extends AppCompatActivity {

    public static final String TAG = "CPTR320";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudoku);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }
    public void onClick(View view){
        int id = view.getId();
        if (id == R.id.About) {
            Intent intent = new Intent();
            startActivity(new Intent(this, About.class));
        } else if (id == R.id.NewGame) {
            openNewGameDialog();
        } else if (id == R.id.Exit) {
            finish();
        } else if (id == R.id.Continue) {
            startGame(Game.DIFFICULTY_CONTINUE);
        }
    }

    private void openNewGameDialog() {
        new AlertDialog.Builder(this).setTitle(R.string.new_game_title).setItems(R.array.difficulty, (dialogInterface, i) -> startGame(i)).show();
    }

    private void startGame(int i) {
        String[] array = getResources().getStringArray(R.array.difficulty);
        //Log.d(TAG, "Selected" + array[i]);

        Intent intent = new Intent(this, Game.class);
        intent.putExtra(Game.KEY_DIFFICULTY, i);
        startActivity(intent);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //getSupportActionBar().hide();
        if (id == R.id.settings) {
            Intent intent = new Intent(this, PrefsActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }

}