package vn.edu.usth.irc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class ServerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sever);
    }

    public void connect (View view){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
//        Function to check connection to the server
//        *
//        *
//        *
//        *
//        *
//        *
//        *
//        put here
        finish();
        SharedPreferences preferences = getSharedPreferences("first_time", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("RanBefore", true);
        editor.commit();
    }

    @Override
    public void onBackPressed() {
        SharedPreferences preferences = getSharedPreferences("first_time", Context.MODE_PRIVATE);
        if (preferences.getBoolean("RanBefore", false) == true)
        {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        }
        super.onBackPressed();
    }
}

