package com.example.smarthome;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smarthome.databinding.SetAlarmBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Objects;

public class setAlarm extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference requestCodeDB = database.getReference("requestCode");

    SetAlarmBinding binding;
    Calendar calendar;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;


    static int requestCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SetAlarmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        calendar = Calendar.getInstance();
        alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

        queryFunctionButton();
        readRequestCode();
    }



    private void queryFunctionButton()
    {
        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(setAlarm.this,MainActivity.class);
                startActivity(intent);
            }
        });

        binding.buttonSave.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if(queryInternet.isNetworkAvailable(getApplicationContext())){
                    if(queryTimePump()==1){
                        sheduleNotification();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"Không có kết nối mạng",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void readRequestCode(){
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


    private int queryTimePump(){
        int flag = 0;

        String timeP = binding.editText.getText().toString();
        int timePlay = Integer.parseInt(timeP);

        if(timePlay > 60 || timePlay <= 0){
            Toast.makeText(getApplicationContext(),"Giá trị hợp lệ từ 1 đến 60",Toast.LENGTH_LONG).show();
            flag = 0;
        }
        else{
            Toast.makeText(getApplicationContext(),"Đặt chuông báo thành công",Toast.LENGTH_LONG).show();
            flag = 1;
        }
        return flag;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void sheduleNotification()
    {

        int i =  calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH,i);

        calendar.set(Calendar.HOUR_OF_DAY,binding.timePicker.getHour());
        calendar.set(Calendar.MINUTE,binding.timePicker.getMinute());
        calendar.set(Calendar.SECOND,0);

        String timeP = binding.editText.getText().toString();
        int timePlay = Integer.parseInt(timeP);

        Intent intent = new Intent(this,AlarmReceiver.class);
        intent.putExtra("startHour",binding.timePicker.getHour());
        intent.putExtra("startMinute",binding.timePicker.getMinute());
        intent.putExtra("time",timePlay);


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

        /* Save request Code Firebase */
        requestCodeDB.setValue(requestCode);

        Intent intent1 = new Intent(setAlarm.this,MainActivity.class);
        startActivity(intent1);
    }

}
