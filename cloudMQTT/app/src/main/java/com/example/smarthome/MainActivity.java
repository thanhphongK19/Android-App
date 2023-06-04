package com.example.smarthome;

import static com.hivemq.client.mqtt.MqttGlobalPublishFilter.ALL;
import static java.nio.charset.StandardCharsets.UTF_8;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import com.example.smarthome.databinding.ActivityMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hivemq.client.mqtt.datatypes.MqttQos;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements MQTTprotocol {

    public ActivityMainBinding binding;

    static int onlyOne = 0;
    static int onlyOne1 = 0;
    static int ActivityLifeCycle;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.buttonON.setBackgroundColor(Color.GREEN);
        binding.buttonOFF.setBackgroundColor(Color.RED);

        if(onlyOne == 0){
            /* Setting connect MQTT cloud */
            binding.image.setImageResource(R.drawable.off1);
            MQTTprotocol.setupConnectMQTT();
            onlyOne = 1;
            subscribeTopicMQTT();
            receiveDataMQTT();
        }
        if(onlyOne1 == 0){
            //binding.image.setImageResource(R.drawable.off1);
            receiveDataMQTT();
        }

        clickButton();

        ActivityLifeCycle = 1;
    }
    @Override
    protected void onPause() {
        super.onPause();
        ActivityLifeCycle = 0;
    }
    @Override
    protected void onStop() {
        super.onStop();
        ActivityLifeCycle = 0;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ActivityLifeCycle = 1;
        onlyOne1 = 0;
    }

    private void clickButton() {
        binding.buttonON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(queryInternet.isNetworkAvailable(getApplicationContext())){
                    binding.buttonON.setBackgroundColor(Color.BLACK);
                    MQTTprotocol.publishData("led","1");
                    MQTTprotocol.publishData("alarmLed","0");
                    MQTTprotocol.publishData("startHour","0");
                    MQTTprotocol.publishData("startMinute","0");
                    MQTTprotocol.publishData("endHour","0");
                    MQTTprotocol.publishData("endMinute","0");
                }
                else{
                    Toast.makeText(getApplicationContext(), "Không có kết nối mạng", Toast.LENGTH_SHORT).show();

                }

            }
        });

        binding.buttonOFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(queryInternet.isNetworkAvailable(getApplicationContext())){
                    binding.buttonON.setBackgroundColor(Color.GREEN);
                    MQTTprotocol.publishData("led","0");
                    MQTTprotocol.publishData("alarmLed","0");
                    MQTTprotocol.publishData("startHour","0");
                    MQTTprotocol.publishData("startMinute","0");
                    MQTTprotocol.publishData("endHour","0");
                    MQTTprotocol.publishData("endMinute","0");

                }
                else{
                    Toast.makeText(getApplicationContext(), "Không có kết nối mạng", Toast.LENGTH_SHORT).show();
                }

            }
        });

        binding.buttonAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(queryInternet.isNetworkAvailable(getApplicationContext())){
                    Intent intent = new Intent(MainActivity.this, setAlarm.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Không có kết nối mạng",Toast.LENGTH_SHORT).show();

                }

            }
        });

        binding.buttonInspect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (MainActivity.this,InspectActivity.class );
                startActivity(intent);
            }
        });
    }

    void subscribeTopicMQTT()
    {
        /* topic for Node Control */
        client.subscribeWith()
                .topicFilter("statusLed")
                .qos(MqttQos.EXACTLY_ONCE)
                .send();

        client.subscribeWith()
                .topicFilter("statusWifi")
                .qos(MqttQos.AT_LEAST_ONCE)
                .send();

        client.subscribeWith()
                .topicFilter("led")
                .qos(MqttQos.EXACTLY_ONCE)
                .send();
        client.subscribeWith()
                .topicFilter("alarmLed")
                .qos(MqttQos.EXACTLY_ONCE)
                .send();
        client.subscribeWith()
                .topicFilter("startHour")
                .qos(MqttQos.EXACTLY_ONCE)
                .send();
        client.subscribeWith()
                .topicFilter("startMinute")
                .qos(MqttQos.EXACTLY_ONCE)
                .send();
        client.subscribeWith()
                .topicFilter("endHour")
                .qos(MqttQos.EXACTLY_ONCE)
                .send();
        client.subscribeWith()
                .topicFilter("endMinute")
                .qos(MqttQos.EXACTLY_ONCE)
                .send();

        ////////////////////////////////////////////

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    void receiveDataMQTT()
    {
        final String[] val = new String[1];
        final CharBuffer[] value = new CharBuffer[1];

        String s1 = "statusLed";
        String s2 = "statusWifi";

        MQTTprotocol.client.toAsync().publishes(ALL, publish ->{
            val[0] = publish.getTopic().toString();
            value[0] = UTF_8.decode(ByteBuffer.wrap(publish.getPayloadAsBytes()));

            if(s1.equals(val[0])){

                if(Integer.parseInt(String.valueOf(value[0])) == 1){

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.image.setImageResource(R.drawable.on1);
                            System.out.println("on");
                        }
                    });
                }
                else if(Integer.parseInt(String.valueOf(value[0])) == 0){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.image.setImageResource(R.drawable.off1);
                            System.out.println("off");
                        }
                    });

                }
            }

            if(s2.equals(val[0])){

                if(ActivityLifeCycle == 1) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final Toast toast = Toast.makeText(getApplicationContext(), "HỆ THỐNG ĐÃ KẾT NỐI MẠNG", Toast.LENGTH_SHORT);
                            new CountDownTimer(2000, 1000) {
                                public void onTick(long millisUntilFinished) {
                                    toast.show();
                                }

                                public void onFinish() {
                                    toast.cancel();
                                }
                            }.start();
                        }
                    });
                }

            }

        });
    }


}