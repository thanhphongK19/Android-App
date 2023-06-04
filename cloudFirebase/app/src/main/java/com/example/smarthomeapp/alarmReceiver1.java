package com.example.smarthomeapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class alarmReceiver1 extends BroadcastReceiver {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference fan = database.getReference("fan");

    int on = 1;
    int off = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Hi", "I am here");

        int FAN = intent.getIntExtra("FAN",0);

        if(FAN == 1){
            fan.setValue(on);
        }
        else{
            fan.setValue(off);
        }
    }
}
