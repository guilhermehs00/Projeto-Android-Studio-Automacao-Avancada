package com.guilherme.projetoautavancada;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Atividade_BD extends AppCompatActivity {

    private WebView webView;
    private Button button_mapa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atividade_2);

        webView = findViewById(R.id.webview);
        button_mapa = findViewById(R.id.button_mapa);

        // Habilitar JavaScript se necessário
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Manter navegação dentro do WebView
        webView.setWebViewClient(new WebViewClient());

        // Carregar uma URL
        webView.loadUrl("https://console.firebase.google.com/project/automacao-avancada-417713/firestore/databases/-default-/data/~2F?hl=pt-br");

        button_mapa.setOnClickListener(v -> {
            AnimarBotton.aplicarPulsacao(v);
            onBackPressed();
        });
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack(); // Volta para a página anterior na WebView, se possível
        } else {
            super.onBackPressed(); // Caso contrário, finaliza a atividade e volta para a atividade principal
        }
    }
}
