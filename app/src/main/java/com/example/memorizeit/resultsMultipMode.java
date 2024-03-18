package com.example.memorizeit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class resultsMultipMode extends AppCompatActivity {

    private TextView tvWinner;
    private TextView tvLevel;
    private TextView tvestado;
    private Button btnPlayAgain;
    private Button btnBackToWelcome;
    private int rol;
    private String ganador;
    private int nivel;
    private String nombreJugador1;
    private String nombreJugador2;
    private String NicknamePersonal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results_multip_mode);

        tvWinner = findViewById(R.id.tvWinner);
        tvLevel = findViewById(R.id.tvLevel);
        btnPlayAgain = findViewById(R.id.btnPlayAgain);
        btnBackToWelcome = findViewById(R.id.btnBackToWelcome);
        tvestado = findViewById(R.id.tvestado);

        rol = this.getIntent().getIntExtra("rol", -1);
        ganador = this.getIntent().getStringExtra("ganador");
        nivel = this.getIntent().getIntExtra("nivel", 1);

        if (rol == 0) {
            btnPlayAgain.setText("Volver al Lobby");
            NicknamePersonal = this.getIntent().getStringExtra("NicknamePersonal");
            System.out.println(NicknamePersonal);
            if (NicknamePersonal.equals(ganador)){
                tvestado.setText("Ganaste");
            }
            else{
                tvestado.setText("Perdiste");
            }
            tvestado.setVisibility(View.VISIBLE);
        }
        else if( rol == 1 ) {
            btnPlayAgain.setText("Volver al Lobby");
            NicknamePersonal = this.getIntent().getStringExtra("NicknamePersonal");
            System.out.println(NicknamePersonal);
            if (NicknamePersonal.equals(ganador)){
                tvestado.setText("Ganaste");
            }
            else{
                tvestado.setText("Perdiste");
            }
            tvestado.setVisibility(View.VISIBLE);
        }
        else{
            nombreJugador1 = this.getIntent().getStringExtra("nombreJugador1");
            nombreJugador2 = this.getIntent().getStringExtra("nombreJugador2");
        }

        tvWinner.setText("¡Ganador!: " + ganador);
        tvLevel.setText("Nivel alcanzado: " + nivel);


    }

    public void playAgain(View view) {

        if (rol == 0) {
            finish();
        }
        else if( rol == 1 ) {
            finish();
        }
        else{
            Intent intent = new Intent(this, multipMode.class);
            intent.putExtra("nombreJugador1", nombreJugador1);
            intent.putExtra("nombreJugador2", nombreJugador2);
            startActivity(intent);
            finish();
        }

    }

    public void home(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
