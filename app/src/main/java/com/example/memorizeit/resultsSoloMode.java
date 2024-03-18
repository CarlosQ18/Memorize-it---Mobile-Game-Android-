package com.example.memorizeit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class resultsSoloMode extends AppCompatActivity {
    private TextView tvScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results_solo_mode);

        tvScore = findViewById(R.id.tvScore);

        Bundle bundle = getIntent().getExtras();
        int nivel = bundle.getInt("nivel");

        tvScore.setText("Puntaje: " + nivel);

    }

    public void playAgain(View view) {
        Intent intent = new Intent(this, soloMode.class);
        startActivity(intent);
        finish();
    }

    public void home(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
