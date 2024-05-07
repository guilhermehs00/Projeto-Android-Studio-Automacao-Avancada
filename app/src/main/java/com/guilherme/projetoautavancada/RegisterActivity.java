package com.guilherme.projetoautavancada;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText nameEditText = findViewById(R.id.nameEditText);
        EditText idEditText = findViewById(R.id.IDuser);
        Button enterButton = findViewById(R.id.okButton);

        enterButton.setOnClickListener(v -> {
            String nome = nameEditText.getText().toString();
            int id = Integer.parseInt(idEditText.getText().toString());

            // Salvando os dados do usuário nas SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("NomeUsuario", nome);
            editor.putInt("IdUsuario", id);
            editor.apply();

            // Inicie a MainActivity ou outra atividade como destino após o registro
            Intent iniciaMain = new Intent(RegisterActivity.this, AtividadePrincipal.class);
            startActivity(iniciaMain);
            finish(); // Finaliza a RegisterActivity
        });
    }
}
