package com.example.smarthomeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.example.smarthomeapp.databinding.ActivityMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference led = database.getReference("led");
    DatabaseReference fan = database.getReference("fan");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnOnLed.setBackgroundColor(Color.GREEN);
        binding.btnOffLed.setBackgroundColor(Color.RED);
        binding.led.setImageResource(R.drawable.off1);

        binding.btnOnFan.setBackgroundColor(Color.GREEN);
        binding.btnOffFan.setBackgroundColor(Color.RED);

        clickButton();
        queryFirebase();
    }
    private void clickButton()
    {
        binding.btnOnLed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(queryInternet.isNetworkAvailable(getApplicationContext())) {
                    led.setValue(1);
                    //startAnimation();
                    binding.btnOnLed.setBackgroundColor(Color.BLACK);
                    binding.led.setImageResource(R.drawable.on1);
                }
                else{
                  Toast.makeText(getApplicationContext(),"Không có kết nối mạng",Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.btnOffLed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(queryInternet.isNetworkAvailable(getApplicationContext())) {
                    led.setValue(0);
                    //startAnimation();
                    binding.btnOnLed.setBackgroundColor(Color.GREEN);
                    binding.led.setImageResource(R.drawable.off1);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Không có kết nối mạng",Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.btnOnFan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(queryInternet.isNetworkAvailable(getApplicationContext())){
                    fan.setValue(1);
                    startAnimation();
                    binding.btnOnFan.setBackgroundColor(Color.BLACK);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Không có kết nối mạng",Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.btnOffFan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(queryInternet.isNetworkAvailable(getApplicationContext())){
                    fan.setValue(0);
                    stopAnimation();
                    binding.btnOnFan.setBackgroundColor(Color.GREEN);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Không có kết nối mạng",Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.buttonAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(queryInternet.isNetworkAvailable(getApplicationContext())){
                    Intent intent = new Intent(MainActivity.this, set_alarm.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Không có kết nối mạng",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void queryFirebase()
    {
        /* Checking status Pump */
        led.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String statusL = Objects.requireNonNull(snapshot.getValue()).toString();
                int statusLed = Integer.parseInt(statusL);
                if (statusLed == 1) {
                    binding.led.setImageResource(R.drawable.on1);
                }
                else {
                    binding.led.setImageResource(R.drawable.off1);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        /* Checking status Pump */
        fan.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String statusF = Objects.requireNonNull(snapshot.getValue()).toString();
                int statusFan = Integer.parseInt(statusF);
                if (statusFan == 1) {
                    startAnimation();
                }
                else {
                    stopAnimation();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void startAnimation()
    {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                binding.imagefan.animate().rotationBy(360).withEndAction(this).setDuration(5000)
                        .setInterpolator(new LinearInterpolator()).start();
            }
        };
        binding.imagefan.animate().rotationBy(360).withEndAction(runnable).setDuration(5000)
                .setInterpolator(new LinearInterpolator()).start();
    }
    private void stopAnimation()
    {
        binding.imagefan.animate().cancel();
    }
}