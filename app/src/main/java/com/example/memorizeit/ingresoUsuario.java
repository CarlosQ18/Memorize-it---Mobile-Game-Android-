package com.example.memorizeit;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class ingresoUsuario extends AppCompatActivity {

    private TextInputEditText userName1;
    private TextInputEditText userName2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ingreso_user);

        userName1 = findViewById(R.id.UserName1);
        userName2 = findViewById(R.id.UserName2);

    }
    public void play(View v) {
        String name1 = userName1.getText().toString().trim();
        String name2 = userName2.getText().toString().trim();

        if (TextUtils.isEmpty(name1) || TextUtils.isEmpty(name2)) {
            Toast.makeText(this, "Por favor, ingresa los nicknames de ambos jugadores", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, multipMode.class);
            intent.putExtra("nombreJugador1", name1);
            intent.putExtra("nombreJugador2", name2);
            startActivity(intent);
        }
    }
}
