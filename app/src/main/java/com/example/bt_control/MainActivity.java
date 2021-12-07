package com.example.bt_control;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private ListView ListViewBT;
    private BluetoothAdapter btAdapter;
    private Intent intent;
    private int RequestBTCode = 100;
    private Set<BluetoothDevice> allBtDevice;
    private ArrayList<String> btList;
    private ArrayAdapter<String> avatar;
    private int mode;
    private final int ControlMode = 50;
    private final int CarMode = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        mode = ControlMode;
        ListViewBT = (ListView)findViewById(R.id.ListViewBT);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(btAdapter==null){
            Toast.makeText(context,"無藍芽設備!",Toast.LENGTH_SHORT).show();
            finish();
        } else {
            if(btAdapter.isEnabled()){
                intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent,RequestBTCode);

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RequestBTCode){
            if(resultCode == RESULT_CANCELED){
                Toast.makeText(context, "藍芽關閉!",Toast.LENGTH_SHORT).show();
                finish();
            } else if(resultCode == RESULT_OK){
                Toast.makeText(context,"藍芽開啟",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        allBtDevice = btAdapter.getBondedDevices();
        btList = new ArrayList<String>();
        if(allBtDevice.size()>0){
            for(BluetoothDevice device:allBtDevice){
                btList.add(device.getName()+"\n"+device.getAddress());
            }
            avatar = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1,btList);
            ListViewBT.setAdapter(avatar);
            ListViewBT.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String item = parent.getItemAtPosition(position).toString();
                    Log.d("main","item="+item);
                    if(mode == ControlMode){
                        intent = new Intent(context, ControlActivity.class);
                        intent.putExtra("item",item);
                        startActivity(intent);
                    } else if(mode ==CarMode){
                        intent = new Intent(context, CarActivity.class);
                        intent.putExtra("item",item);
                        startActivity(intent);
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.setup,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.control_mode:
                mode = ControlMode;
                break;
            case R.id.car_mode:
                mode = CarMode;
                break;
        }
        Log.d("main","mode="+mode);
        return super.onOptionsItemSelected(item);
    }
}