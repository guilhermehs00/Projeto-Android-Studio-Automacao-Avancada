package com.guilherme.projetoautavancada;

import android.view.MotionEvent;
import android.view.View;

public class AnimarBotton {

    // Método para aplicar a animação de pulsação
    public static void aplicarPulsacao(final View view) {
        view.animate()
                .scaleX(0.7f) // Reduz a escala horizontal para 90%
                .scaleY(0.7f) // Reduz a escala vertical para 90%
                .setDuration(100) // Define a duração da animação
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        // Retorna ao tamanho original
                        view.animate()
                                .scaleX(1f) // Escala horizontal para 100%
                                .scaleY(1f) // Escala vertical para 100%
                                .setDuration(100);
                    }
                });
    }

    // Método para manter a escala reduzida enquanto o botão está pressionado
    public static void aplicarPulsacaoLong(final View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.animate().scaleX(0.7f).scaleY(0.7f).setDuration(100).start();
                        return false;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                        return false;
                }
                return false;
            }
        });
    }
}