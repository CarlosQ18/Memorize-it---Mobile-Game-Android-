package com.example.memorizeit;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class multipMode extends AppCompatActivity implements View.OnClickListener {

    private TextView nivelT;
    private TextView turno;
    private Button[] botones;
    private List<Integer> secuencia = new ArrayList<>();

    private ArrayList<Integer> secuenciaJugador1 = new ArrayList <Integer>();
    private ArrayList<Integer> secuenciaJugador2 = new ArrayList <Integer>();

    private boolean turnoJugador1 = true;
    private int nivel = 1;
    private int contador = 0;
    private boolean mostrandoSecuencia = false;
    private String nombreJugador1;
    private String nombreJugador2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multip_mode);

        turno = findViewById(R.id.turno);
        nivelT = findViewById(R.id.nivelT);

        nombreJugador1 = this.getIntent().getStringExtra("nombreJugador1");
        nombreJugador2 = this.getIntent().getStringExtra("nombreJugador2");


        turno.setText(nombreJugador1);
        nivelT = findViewById(R.id.nivelT);


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

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Turno de " + nombreJugador1 ).setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                GenerarSecuencia();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

    }

    private void GenerarSecuencia() {
        nivelT.setText("Nivel: " + nivel);
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


        if (turnoJugador1 == true) {
            encenderBoton(boton);
            secuenciaJugador1.add(botonIndex);
            contador++;

            if (contador == secuencia.size()) {
                turno.setText(nombreJugador2);
                turnoJugador1 = false;
                contador = 0;
                turno.setText(nombreJugador2);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Turno de " + nombreJugador2 ).setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        PintarBoton();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();

            }
        } else {

            encenderBoton(boton);
            secuenciaJugador2.add(botonIndex);
            contador++;
            if (contador == secuencia.size()) {
                turno.setText("Verificando Secuencia");
                contador = 0;

                if ( !(secuenciaJugador1.equals(secuencia)) && !(secuenciaJugador2.equals(secuencia))){
                    System.out.println(secuenciaJugador1);
                    System.out.println(secuenciaJugador2);
                    secuenciaJugador1.clear();
                    secuenciaJugador2.clear();
                    nivel++;
                    nivelT.setText("Nivel: " + nivel);
                    turnoJugador1 = true;
                    contador = 0;
                    turno.setText(nombreJugador1);
                    Toast.makeText(this, "Empate - Muerte Súbita", Toast.LENGTH_SHORT).show();

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Turno de " + nombreJugador1 ).setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            GenerarSecuencia();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();
                }
                else if ( !(secuenciaJugador1.equals(secuencia)) && (secuenciaJugador2.equals(secuencia))){
                    System.out.println(secuenciaJugador1);
                    System.out.println(secuenciaJugador2);
                    Intent intent = new Intent(this, resultsMultipMode.class);
                    intent.putExtra("nombreJugador1", nombreJugador1);
                    intent.putExtra("nombreJugador2", nombreJugador2);
                    intent.putExtra("ganador",nombreJugador2);
                    intent.putExtra("nivel",nivel);
                    startActivity(intent);
                    finish();
                    //Toast.makeText(this, "Ganó "+ nombreJugador2, Toast.LENGTH_SHORT).show();
                }
                else if ( (secuenciaJugador1.equals(secuencia)) && !(secuenciaJugador2.equals(secuencia))){
                    System.out.println(secuenciaJugador1);
                    System.out.println(secuenciaJugador2);
                    Intent intent = new Intent(this, resultsMultipMode.class);
                    intent.putExtra("nombreJugador1", nombreJugador1);
                    intent.putExtra("nombreJugador2", nombreJugador2);
                    intent.putExtra("ganador",nombreJugador1);
                    intent.putExtra("nivel",nivel);
                    startActivity(intent);
                    finish();
                    //Toast.makeText(this, "Ganó "+ nombreJugador1, Toast.LENGTH_SHORT).show();
                }
                else{
                    System.out.println(secuenciaJugador1);
                    System.out.println(secuenciaJugador2);
                    secuenciaJugador1.clear();
                    secuenciaJugador2.clear();
                    nivel++;
                    nivelT.setText("Nivel: " + nivel);
                    turnoJugador1 = true;
                    contador = 0;
                    turno.setText(nombreJugador1);
                    Toast.makeText(this, "Los 2 jugadores adivinaron la secuencia", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Turno de " + nombreJugador1 ).setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            GenerarSecuencia();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();
                }
            }
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