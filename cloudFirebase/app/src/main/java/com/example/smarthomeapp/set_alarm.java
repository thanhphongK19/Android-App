package com.example.smarthomeapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smarthomeapp.databinding.SetAlarmBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Objects;

public class set_alarm extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference requestCodeDB = database.getReference("requestCode");

    SetAlarmBinding binding;
    Calendar calendar;
    TimePicker timePicker;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;

    /* Global varible */
    int LED = 0;
    int FAN = 0;

    static int requestCode;
    int timeAlarmLed;
    int timeAlarmFan;
    ///////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SetAlarmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        calendar = Calendar.getInstance();
        alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

        binding.textLed.setVisibility(View.INVISIBLE);
        binding.textFan.setVisibility(View.INVISIBLE);

        readRequestCode();
        queryFunctionButton();
        queryDevice();

    }

    private void readRequestCode()
    {
        requestCodeDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    String request = Objects.requireNonNull(snapshot.getValue()).toString();
                    requestCode = Integer.parseInt(request);
                    //requestCode++;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    private void queryFunctionButton()
    {

        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(set_alarm.this,MainActivity.class);
                startActivity(intent);
            }
        });

        binding.buttonSave.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if(queryInternet.isNetworkAvailable(getApplicationContext())){
                    if(queryTime()==1){
                        if(LED == 1 || FAN == 1){

                            sheduleNotification();

                            repeatAlarm();

                            Intent intent1 = new Intent(set_alarm.this,MainActivity.class);
                            startActivity(intent1);
                        }

                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"Không có kết nối mạng",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private int queryTime()
    {
        int flagLed = 0;
        int flagFan = 0;
        int flag = 0;

        if(LED==1){
            String timeP = binding.editLed.getText().toString();
            timeAlarmLed = Integer.parseInt(timeP);

            if(timeAlarmLed > 60 || timeAlarmLed <= 0){
                flagLed = 0;
                flag = flagLed;
            }
            else{
                flagLed = 1;
                flag = flagLed;
            }
        }

        if(FAN==1){
            String timeP1 = binding.editFan.getText().toString();
            timeAlarmFan = Integer.parseInt(timeP1);

            if(timeAlarmFan > 60 || timeAlarmFan <= 0){
                flagFan = 0;
                flag = flagFan;
            }
            else{
                flagFan = 1;
                flag = flagFan;
            }
        }

        if(FAN == 1 && LED == 1){
            if((flagFan+flagLed) > 1){
                Toast.makeText(getApplicationContext(),"Đặt chuông báo thành công",Toast.LENGTH_SHORT).show();
                return 1;
            }
            else{
                Toast.makeText(getApplicationContext(),"Giá trị hợp lệ từ 1 đến 60",Toast.LENGTH_SHORT).show();
                return 0;
            }
        }
        else{
            if(flag == 0){
                Toast.makeText(getApplicationContext(),"Giá trị hợp lệ từ 1 đến 60",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getApplicationContext(),"Đặt chuông báo thành công",Toast.LENGTH_SHORT).show();
            }
            return flag;
        }
    }

    private void queryDevice()
    {
        binding.cbLed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (binding.cbLed.isChecked()){
                    LED = 1;
                    binding.textLed.setVisibility(View.VISIBLE);
                }
                else{
                    LED = 0;
                    binding.textLed.setVisibility(View.INVISIBLE);
                }
            }
        });
        binding.cbFan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (binding.cbFan.isChecked()){
                    FAN = 1;
                    binding.textFan.setVisibility(View.VISIBLE);
                }
                else{
                    FAN = 0;
                    binding.textFan.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void sheduleNotification()
    {
        int i =  calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH,i);
        calendar.set(Calendar.HOUR_OF_DAY,binding.timePicker.getHour());
        calendar.set(Calendar.MINUTE,binding.timePicker.getMinute());
        calendar.set(Calendar.SECOND,0);

        if(LED == 1){
            Intent intent = new Intent(this,alarmReceiver.class);
            intent.putExtra("LED",LED);

            pendingIntent = PendingIntent.getBroadcast(
                    getApplicationContext(),
                    requestCode,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
            );
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );
            requestCode++;
        }
        if(FAN == 1){
            Intent intent1 = new Intent(this,alarmReceiver1.class);
            intent1.putExtra("FAN",FAN);

            pendingIntent = PendingIntent.getBroadcast(
                    getApplicationContext(),
                    requestCode,
                    intent1,
                    PendingIntent.FLAG_IMMUTABLE
            );
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );
            requestCode++;
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void repeatAlarm()
    {
        long timeAlarm = 0;
        if(LED==1){
            LED = 0;
            Intent intent1 = new Intent(this,alarmReceiver.class);
            intent1.putExtra("LED",LED);
            timeAlarm = getTimeALarm(timeAlarmLed);

            pendingIntent = PendingIntent.getBroadcast(
                    getApplicationContext(),
                    requestCode,
                    intent1,
                    PendingIntent.FLAG_IMMUTABLE
            );
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    timeAlarm,
                    pendingIntent
            );
            requestCode++;
        }
        if(FAN==1){
            FAN = 0;
            Intent intent2 = new Intent(this,alarmReceiver1.class);
            intent2.putExtra("FAN",FAN);
            timeAlarm = getTimeALarm(timeAlarmFan);

            pendingIntent = PendingIntent.getBroadcast(
                    getApplicationContext(),
                    requestCode,
                    intent2,
                    PendingIntent.FLAG_IMMUTABLE
            );
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    timeAlarm,
                    pendingIntent
            );
            requestCode++;
        }

        requestCodeDB.setValue(requestCode);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private long getTimeALarm(int timeAlarm)
    {
        int hour = 0;
        int minute = 0;


        if((binding.timePicker.getMinute() + timeAlarm) <60){
            minute = binding.timePicker.getMinute() + timeAlarm;
            hour = binding.timePicker.getHour();
        }
        else{
            minute = (binding.timePicker.getMinute() +timeAlarm) - 60;
            hour = binding.timePicker.getHour()+1;
        }

        int i =  calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH,i);
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE,minute);
        calendar.set(Calendar.SECOND,0);

        return calendar.getTimeInMillis();
    }


}
