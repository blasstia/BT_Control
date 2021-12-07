package com.example.bt_control;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class CarActivity extends AppCompatActivity {
    private String btData;
    private TextView textViewBT;
    private Context context;
    private BTChatService myBTChatService;
    private BluetoothAdapter btAdapter;
    private String macAddress;
    private BluetoothDevice btDevice;
    private Switch switchSensor;
    private ImageButton buttonF;
    private ImageButton buttonB;
    private ImageButton buttonR;
    private ImageButton buttonL;
    private ImageButton buttonS;
    private Button buttonlink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car);
        setTitle("車輛模式");
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);    //新增back鍵
        Intent intent = getIntent();
        btData = intent.getStringExtra("item");
        Log.d("control","btData="+btData);
        textViewBT = (TextView)findViewById(R.id.textViewCar);
        textViewBT.setText(btData);
        context = this;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //固定畫面
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        myBTChatService = new BTChatService(context,myHandler);
        if(btData != null){
            macAddress = btData.substring(btData.length()-17);
            Log.d("car","macAddress="+macAddress);
            btDevice = btAdapter.getRemoteDevice(macAddress);
            myBTChatService.connect(btDevice);
        }
        buttonlink = (Button)findViewById(R.id.buttonCar);
        buttonlink.setOnClickListener(new View.OnClickListener() {
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
        buttonF = (ImageButton)findViewById(R.id.ButtonUp);
        buttonB = (ImageButton)findViewById(R.id.ButtonDown);
        buttonR = (ImageButton)findViewById(R.id.ButtonRight);
        buttonL = (ImageButton)findViewById(R.id.ButtonLeft);
        buttonS = (ImageButton)findViewById(R.id.ButtonCancel);
        buttonF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCommand("f");
            }
        });
        buttonB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCommand("b");
            }
        });
        buttonR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCommand("r");
            }
        });
        buttonL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCommand("l");
            }
        });
        buttonS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCommand("s");
            }
        });
        switchSensor = (Switch)findViewById(R.id.switchSensor);
        switchSensor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sendCommand("v");
                } else {
                    sendCommand("z");
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_songoff:
                sendCommand("0");
                break;
            case R.id.menu_song1:
                sendCommand("1");
                break;
            case R.id.menu_song2:
                sendCommand("2");
                break;
            case R.id.menu_song3:
                sendCommand("3");
                break;
            case R.id.menu_song4:
                sendCommand("4");
                break;
            case R.id.menu_song5:
                sendCommand("5");
                break;
            case R.id.menu_song6:
                sendCommand("6");
                break;
            case R.id.menu_song7:
                sendCommand("7");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.carmenu,menu);
        return true;
    }

    private Handler myHandler=new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case Constants.MESSAGE_READ:
                    Log.d("Car","Message read");
                    byte[] dataBuff = (byte[])msg.obj;
                    String data = new String(dataBuff, 0, msg.arg1);
                    Log.d("Car","data="+data);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    Log.d("Car","Message name");
                    String name = msg.getData().getString(Constants.DEVICE_NAME);
                    Log.d("Car","device name="+name);
                    Toast.makeText(context,"Link To:"+name,Toast.LENGTH_SHORT).show();
                    break;
                case Constants.MESSAGE_TOAST:
                    Log.d("Car","Message toast");
                    String error = msg.getData().getString(Constants.TOAST);
                    Toast.makeText(context,"ERROR:"+error,Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void sendCommand(String cmd){
        int btState = myBTChatService.getState();
        Log.d("Car","bt state="+btState);
        if(btState==BTChatService.STATE_CONNECTED){
            if(cmd.length()>0){
                byte[] data=cmd.getBytes();
                myBTChatService.BTWrite(data);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(myBTChatService!=null){
            myBTChatService.stop();
            myBTChatService=null;
        }
    }
}