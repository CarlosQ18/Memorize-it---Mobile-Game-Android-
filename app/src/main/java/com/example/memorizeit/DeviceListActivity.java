package com.example.memorizeit;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Set;

public class DeviceListActivity extends AppCompatActivity {

    private String nickname;
    private ListView listaEmparejados, listaDispDisponibles;
    private ProgressBar progresoDispEscaneados;
    private ArrayAdapter<String> adapDispEmparejados, adapDispDisponibles;
    private Context context;
    private BluetoothAdapter bluetoothAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        context = this;
        nickname = this.getIntent().getStringExtra( "nick" );
        mostrarServers();
    }

    private void mostrarServers() {
        Intent intent = new Intent(this, BluetoothMode.class);

        listaEmparejados = findViewById(R.id.lista_emparejados);
        listaDispDisponibles = findViewById(R.id.lista_disponibles);
        progresoDispEscaneados = findViewById(R.id.progreso_escaneo_disps);

        adapDispEmparejados = new ArrayAdapter<String>(context, R.layout.device_list_item);
        adapDispDisponibles = new ArrayAdapter<String>(context, R.layout.device_list_item);

        listaEmparejados.setAdapter(adapDispEmparejados);
        listaDispDisponibles.setAdapter(adapDispDisponibles);

        listaDispDisponibles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String info = ((TextView) view).getText().toString();
                String address = info.substring(info.length() - 17);

                intent.putExtra("Address", address);
                intent.putExtra("nick", nickname);
                intent.putExtra("rol",1);
                startActivity(intent);
                finish();
            }
        });

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> DispEmpareja = bluetoothAdapter.getBondedDevices();

        if (DispEmpareja != null && DispEmpareja.size() > 0) {
            for (BluetoothDevice device : DispEmpareja) {
                adapDispEmparejados.add(device.getName() + "\n" + device.getAddress());
            }
        }

        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, intentFilter);
        IntentFilter intentFilter1 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver, intentFilter1);

        listaEmparejados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                bluetoothAdapter.cancelDiscovery();

                String info = ((TextView) view).getText().toString();
                String address = info.substring(info.length() - 17);

                intent.putExtra("Address", address);
                intent.putExtra("nick", nickname);
                intent.putExtra("rol",1);
                startActivity(intent);
                finish();
            }
        });
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    adapDispDisponibles.add(device.getName() + "\n" + device.getAddress());
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                progresoDispEscaneados.setVisibility(View.GONE);
                if (adapDispDisponibles.getCount() == 0) {
                    Toast.makeText(context, "No se encuentran dispositivos nuevos", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Click en el dispositivo para iniciar la conexión", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    public void Discover(View view) {
        progresoDispEscaneados.setVisibility(View.VISIBLE);
        adapDispDisponibles.clear();
        Toast.makeText(context, "Buscando...", Toast.LENGTH_SHORT).show();

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }

        bluetoothAdapter.startDiscovery();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }
}