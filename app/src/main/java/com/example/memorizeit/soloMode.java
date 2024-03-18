package com.example.memorizeit;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class soloMode extends AppCompatActivity implements View.OnClickListener {

    private TextView titulo;
    private Button[] botones;
    private List<Integer> secuencia = new ArrayList<>();
    private int nivel = 1;
    private int contador = 0;
    private boolean mostrandoSecuencia = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.solo_mode_game);

        titulo = findViewById(R.id.titulo);

        botones = new Button[9];
        botones[0] = findViewById(R.id.boton00);
        botones[1] = findViewById(R.id.boton01);
        botones[2] = findViewById(R.id.boton02);
        botones[3] = findViewById(R.id.boton03);
        botones[4] = findViewById(R.id.boton04);
        botones[5] = findViewById(R.id.boton05);
        botones[6] = findViewById(R.id.boton06);
        botones[7] = findViewById(R.id.boton07);
        botones[8] = findViewById(R.id.boton08);

        for (Button boton : botones) {
            boton.setOnClickListener(this);
        }

        GenerarSecuencia();
    }

    private void GenerarSecuencia() {
        titulo.setText("Nivel: " + nivel);
        secuencia.add(generarBotonAleatorio());
        System.out.println(secuencia);
        PintarBoton();
    }

    private void PintarBoton() {
        Handler handler = new Handler();
        for (int i = 0; i < secuencia.size(); i++) {
            int botonIndex = secuencia.get(i);
            int delay = (i + 1) * 1000;
            handler.postDelayed(() -> encenderBoton(botones[botonIndex]), delay);
        }
        mostrandoSecuencia = true;
        handler.postDelayed(() -> mostrandoSecuencia = false, secuencia.size() * 1000);
    }

    private void encenderBoton(Button boton) {
        boton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFEB3B")));
        Handler handler = new Handler();
        handler.postDelayed(() -> apagarBoton(boton), 500);
    }

    private void apagarBoton(Button boton) {
        boton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF0B658E")));;
    }

    private int generarBotonAleatorio() {
        Random random = new Random();
        return random.nextInt(9);
    }

    @Override
    public void onClick(View v) {
        if (mostrandoSecuencia) {
            return;
        }

        Button boton = (Button) v;
        int botonIndex = obtenerBotonIndex(boton);

        if (botonIndex == secuencia.get(contador)) {
            encenderBoton(boton);
            contador++;

            if (contador == secuencia.size()) {
                nivel++;
                contador = 0;
                GenerarSecuencia();
            }
        } else {
            Intent intent = new Intent(this, resultsSoloMode.class);
            intent.putExtra("nivel",nivel);
            startActivity(intent);
            finish();
        }
    }

    private int obtenerBotonIndex(Button boton) {
        for (int i = 0; i < botones.length; i++) {
            if (boton == botones[i]) {
                return i;
            }
        }
        return -1;
    }


}
