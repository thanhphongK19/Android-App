package com.example.smarthome;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AlarmReceiver extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Hi", "I am here");

        int endHour = 0;
        int endMinute = 0;

        int startHour = intent.getIntExtra("startHour", 0);
        int startMinute = intent.getIntExtra("startMinute", 0);
        int time = intent.getIntExtra("time", 0);
        int timePlay = Integer.parseInt(String.valueOf(time));

        if ((startMinute + timePlay) < 60) {
            endMinute = startMinute + timePlay;
            endHour = startHour;
        } else {
            endMinute = (startMinute + timePlay) - 60;
            endHour = startHour + 1;
        }
        System.out.println(startHour);
        System.out.println(startMinute);
        System.out.println(endHour);
        System.out.println(endMinute);


        MQTTprotocol.publishData("startHour",String.valueOf(startHour));
        MQTTprotocol.publishData("startMinute",String.valueOf(startMinute));
        MQTTprotocol.publishData("endHour",String.valueOf(endHour));
        MQTTprotocol.publishData("endMinute",String.valueOf(endMinute));
        MQTTprotocol.publishData("led","0");
        MQTTprotocol.publishData("alarmLed","1");

    }



}
