package com.example.memorizeit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class UserInputActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private RadioButton radioButtonServer;
    private TextInputEditText userNameEditText;
    private String nickname;
    private int REQUEST_ENABLE_BLUETOOTH=1;
    private Context context;
    private final int LOCATION_PERMISSION_REQUEST = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_input);

        findViewByIdes();

        context = this;

        int error = this.getIntent().getIntExtra("error", 0);

        if (error == 1){
            nickname = this.getIntent().getStringExtra("nick");
            userNameEditText.setText(nickname);
            Toast.makeText(context, "Conexion Fallida", Toast.LENGTH_SHORT).show();
        }else{
            nickname = this.getIntent().getStringExtra("nick");
            userNameEditText.setText(nickname);
        }

        userNameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    textView.setCursorVisible(false);
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        userNameEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userNameEditText.setCursorVisible(true);
            }

        });


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if( bluetoothAdapter == null ) {
            Toast toast = Toast.makeText( this, "Bluetooth no disponible!",Toast.LENGTH_LONG );
            toast.show();
        } else {
            Button buttonStart = (Button) this.findViewById( R.id.buttonStart );
            buttonStart.setEnabled( true );
            if(!bluetoothAdapter.isEnabled())
            {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent,REQUEST_ENABLE_BLUETOOTH);
            }
        }
    }

    private void findViewByIdes() {
        userNameEditText = findViewById(R.id.UserName);
        radioButtonServer = findViewById(R.id.radioButtonServer);
    }


    public void searchRival(View view) {
        Intent intent;
        nickname = userNameEditText.getText().toString();
        if (TextUtils.isEmpty(nickname)) {
            Toast toast = Toast.makeText(this, "Ingresa tu Nick", Toast.LENGTH_LONG);
            toast.show();
        } else {
            if (radioButtonServer.isChecked()) {
                intent = new Intent(this, BluetoothMode.class);
                intent.putExtra("rol",0);
                intent.putExtra("nick",nickname);
                startActivity(intent);
            } else {
                checkPermissions();
            }

        }
    }
    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UserInputActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
        } else {
            Intent intent = new Intent(context, DeviceListActivity.class);
            intent.putExtra("nick",nickname);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}




