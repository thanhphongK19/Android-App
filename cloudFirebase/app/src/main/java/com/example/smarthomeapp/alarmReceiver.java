package com.example.smarthomeapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class alarmReceiver extends BroadcastReceiver {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference led = database.getReference("led");

    int on = 1;
    int off = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Hi", "I am here");

        int LED = intent.getIntExtra("LED",0);

        if(LED == 1){
            led.setValue(on);
        }
        else{
            led.setValue(off);
        }

    }
}
