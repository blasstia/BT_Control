package com.example.bt_control;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class ControlActivity extends AppCompatActivity {

    private String btData;
    private TextView textViewBT;
    private Context context;
    private BluetoothAdapter btAdapter;
    private BTChatService myBTChatService;
    private String macAddress;
    private BluetoothDevice btDevice;
    private Spinner spinnerSong;
    private ArrayAdapter<CharSequence> spinnerAvatar;
    private int songCommand=0;
    private Button buttonPlay;
    private final String Lamp1_on="x";
    private final String Lamp1_off="y";
    private final String Lamp2_on="c";
    private final String Lamp2_off="d";
    private final String Lamp3_on="h";
    private final String Lamp3_off="i";
    private final String Fan_on="j";
    private final String Fan_off="k";
    private final String Melody_on="m";
    private final String Melody_off="n";
    private Button buttonLink;
    private Switch switchLamp;
    private boolean flagLamp1;
    private String lampCommand;
    private Switch switchLamp2;
    private boolean flagLamp2;
    private Switch switchLamp3;
    private boolean flagLamp3;
    private Switch switchFan;
    private boolean flagFan;
    private Switch switchMelody;
    private boolean flagMelody;
    private Button buttonData;
    private TextView textViewData;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        setTitle("控制模式");
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        btData = intent.getStringExtra("item");
        textViewBT = (TextView)findViewById(R.id.textViewControl);
        textViewBT.setText(btData);

        context = this;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //固定畫面
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        myBTChatService = new BTChatService(context,myHandler);
        if(btData != null){
            macAddress = btData.substring(btData.length()-17);
            Log.d("control","macAddress="+macAddress);
            btDevice = btAdapter.getRemoteDevice(macAddress);
            myBTChatService.connect(btDevice);
        }
        spinnerSong = (Spinner)findViewById(R.id.spinnerSong);
        spinnerAvatar = ArrayAdapter.createFromResource(context,R.array.song_name, android.R.layout.simple_spinner_item);
        spinnerAvatar.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSong.setAdapter(spinnerAvatar);
        spinnerSong.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Log.d("home","song data");
                songCommand = position;
                Log.d("home","song command="+songCommand);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        buttonPlay = (Button)findViewById(R.id.buttonPlay);
        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCommand(String.valueOf(songCommand));
            }
        });
        buttonLink = (Button)findViewById(R.id.buttonControl);
        buttonLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Link with BT again",Toast.LENGTH_SHORT).show();
                if(btData!=null){
                    macAddress = btData.substring(btData.length()-17);
                    btDevice = btAdapter.getRemoteDevice(macAddress);
                    myBTChatService.connect(btDevice);
                }
            }
        });
        switchLamp = (Switch)findViewById(R.id.switch1);
        flagLamp1=false;
        switchLamp.setChecked(flagLamp1);
        switchLamp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    lampCommand = Lamp1_on;
                } else {
                    lampCommand = Lamp1_off;
                }
                Log.d("Home","Lamp command="+lampCommand);
                sendCommand(lampCommand);
            }
        });
        switchLamp2 = (Switch)findViewById(R.id.switch2);
        flagLamp2=false;
        switchLamp2.setChecked(flagLamp2);
        switchLamp2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    lampCommand = Lamp2_on;
                } else {
                    lampCommand = Lamp2_off;
                }
                Log.d("Home","Lamp command="+lampCommand);
                sendCommand(lampCommand);
            }
        });
        switchLamp3 = (Switch)findViewById(R.id.switch3);
        flagLamp3=false;
        switchLamp3.setChecked(flagLamp3);
        switchLamp3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    lampCommand = Lamp3_on;
                } else {
                    lampCommand = Lamp3_off;
                }
                Log.d("Home","Lamp command="+lampCommand);
                sendCommand(lampCommand);
            }
        });
        switchFan = (Switch)findViewById(R.id.switchFan);
        flagFan=false;
        switchFan.setChecked(flagFan);
        switchFan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    lampCommand = Fan_on;
                } else {
                    lampCommand = Fan_off;
                }
                Log.d("Home","Lamp command="+lampCommand);
                sendCommand(lampCommand);
            }
        });
        switchMelody = (Switch)findViewById(R.id.switchMelody);
        flagMelody=false;
        switchMelody.setChecked(flagMelody);
        switchMelody.setChecked(flagMelody);
        switchMelody.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    lampCommand = Melody_on;
                } else {
                    lampCommand = Melody_off;
                }
                Log.d("Home","Lamp command="+lampCommand);
                sendCommand(lampCommand);
            }
        });
        buttonData = (Button)findViewById(R.id.buttonData);
        textViewData = (TextView)findViewById(R.id.textViewData);
        textViewData.setText("");
        buttonData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewData.setText("");
                sendCommand("t");
            }
        });
    }

    private void sendCommand(String cmd){
        int btState = myBTChatService.getState();
        Log.d("home","bt state="+btState);
        if(btState==BTChatService.STATE_CONNECTED){
            if(cmd.length()>0){
                byte[] data=cmd.getBytes();
                myBTChatService.BTWrite(data);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private Handler myHandler=new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case Constants.MESSAGE_READ:
                    Log.d("control","Message read");
                    byte[] dataBuff = (byte[])msg.obj;
                    String data = new String(dataBuff, 0, msg.arg1);
                    Log.d("home","data="+data);
                    textViewData.append(data);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    Log.d("control","Message name");
                    String name = msg.getData().getString(Constants.DEVICE_NAME);
                    Log.d("control","device name="+name);
                    Toast.makeText(context,"Link To:"+name,Toast.LENGTH_SHORT).show();
                    break;
                case Constants.MESSAGE_TOAST:
                    Log.d("control","Message toast");
                    String error = msg.getData().getString(Constants.TOAST);
                    Toast.makeText(context,"ERROR:"+error,Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(myBTChatService!=null){
            myBTChatService.stop();
            myBTChatService=null;
        }
    }
}