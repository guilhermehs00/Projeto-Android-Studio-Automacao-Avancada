package com.guilherme.projetoautavancada;

import static java.lang.String.format;

import android.os.Handler;
import android.view.View;
import android.widget.TextView;

public class Medir_Trajetoria extends Thread {
    private AtividadePrincipal atividadePrincipal;
    private Handler uiHandler;
    private TextView notification;
    private TextView notification2;
    private TextView labelsizeFila;
    private TextView addRegionButton;
    private TextView GravarBdButtom;
    private TextView num_medicao;

    private Region F1;
    private Region F2;
    private Region F3;
    private Region F4;
    private Region F5;
    private Region F6;

    private double latitudeAtual;
    private double longitudeAtual;

    private long tempo_inicio1;
    private long tempo_inicio2;
    private long tempo_inicio3;
    private long tempo_inicio4;
    private long tempo_inicio5;
    private long tempo_inicio6;

    private boolean rodando = true;
    private boolean cronometroRodando = false;
    private Runnable cronometroRunnable;
    private Runnable numero_med;
    private long Inicio_Cronometro;
    public int Medicao = 0;
    public double[][] d;

    public Medir_Trajetoria(AtividadePrincipal atividadePrincipal, TextView num_medicao, TextView notification, TextView notification2, TextView labelsizeFila, TextView addRegionButton, TextView GravarBdButtom,
                   Handler uiHandler, Region F1, Region F2, Region F3, Region F4, Region F5, Region F6, double d[][]) {
        this.atividadePrincipal = atividadePrincipal;
        this.num_medicao = num_medicao;
        this.notification = notification;
        this.notification2 = notification2;
        this.labelsizeFila = labelsizeFila;
        this.addRegionButton = addRegionButton;
        this.GravarBdButtom = GravarBdButtom;
        this.uiHandler = uiHandler;
        this.F1 = F1;this.F2 = F2;this.F3 = F3;this.F4 = F4;this.F5 = F5; this.F6 = F6;
        this.d = d;
    }
    @Override
    public void run() {
        addRegionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimarBotton.aplicarPulsacao(v);
                if (Medicao == 0){
                    uiHandler.post(() -> {
                        notification.setText(String.format("Tempo de Deslocamento da medição anterior\nD1: %.4f\nD2: %.4f\nD3: %.4f\nD4: %.4f\nD5: %.4f",d[Medicao][0],d[Medicao][1],d[Medicao][1],d[Medicao][1],d[Medicao][4]));
                        notification.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(() -> notification.setVisibility(View.GONE), 1500);
                    });
                }else{
                    uiHandler.post(() -> {
                        notification.setText(String.format("Tempo de Deslocamento\nD1: %.4f\nD2: %.4f\nD3: %.4f\nD4: %.4f\nD5: %.4f",d[Medicao-1][0], d[Medicao-1][1], d[Medicao-1][2], d[Medicao-1][3], d[Medicao-1][4]));
                        notification.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(() -> notification.setVisibility(View.GONE), 1500);
                    });
                }

            }
        });
        GravarBdButtom.setOnClickListener(v -> {
            AnimarBotton.aplicarPulsacao(v);
            uiHandler.post(() -> {
                notification.setText(format("Todos deslocamentos\n" +
                                "%.4f %.4f %.4f %.4f %.4f\n" +
                                "%.4f %.4f %.4f %.4f %.4f\n" +
                                "%.4f %.4f %.4f %.4f %.4f\n" +
                                "%.4f %.4f %.4f %.4f %.4f\n" +
                                "%.4f %.4f %.4f %.4f %.4f\n" +
                                "%.4f %.4f %.4f %.4f %.4f\n" +
                                "%.4f %.4f %.4f %.4f %.4f\n" +
                                "%.4f %.4f %.4f %.4f %.4f\n" +
                                "%.4f %.4f %.4f %.4f %.4f\n" +
                                "%.4f %.4f %.4f %.4f %.4f\n",
                                d[0][0], d[0][1], d[0][2], d[0][3], d[0][4],
                                d[1][0], d[1][1], d[1][2], d[1][3], d[1][4],
                                d[2][0], d[2][1], d[2][2], d[2][3], d[2][4],
                                d[3][0], d[3][1], d[3][2], d[3][3], d[3][4],
                                d[4][0], d[4][1], d[4][2], d[4][3], d[4][4],
                                d[5][0], d[5][1], d[5][2], d[5][3],d[5][4],
                                d[6][0], d[6][1], d[6][2], d[6][3],d[6][4],
                                d[7][0], d[7][1], d[7][2], d[7][3],d[7][4],
                                d[8][0], d[8][1], d[8][2], d[8][3],d[8][4],
                                d[9][0], d[9][1], d[9][2], d[9][3],d[9][4]
                ));
                notification.setVisibility(View.VISIBLE);
                new Handler().postDelayed(() -> notification.setVisibility(View.GONE), 2000);
            });
        });
        cronometroRunnable = new Runnable() {
            @Override
            public void run() {
                long tempoAtual = System.currentTimeMillis();
                long tempoDecorrido = tempoAtual - Inicio_Cronometro;
                long segundos = (tempoDecorrido / 1000) % 60;
                long minutos = (tempoDecorrido / (1000 * 60)) % 60;
                long horas = (tempoDecorrido / (1000 * 60 * 60)) % 24;

                labelsizeFila.setText(String.format("%02d:%02d:%02d", horas, minutos, segundos));
                uiHandler.postDelayed(this, 1000); // Atualiza a cada segundo
            }
        };
        numero_med = new Runnable(){
            @Override
            public void run() {
                num_medicao.setText(format("%d Medições", Medicao));
            }
        };

        while (rodando) {
            latitudeAtual = atividadePrincipal.getLatitudeAtual();
            longitudeAtual = atividadePrincipal.getLongitudeAtual();

            if (Medicao < 10) {
                uiHandler.post(numero_med);
                if (F1.distance(latitudeAtual, longitudeAtual, F1.getLatitude(), F1.getLongitude())){
                    if (!cronometroRodando) {
                        tempo_inicio1 = System.nanoTime();
                        Inicio_Cronometro = System.currentTimeMillis();
                        uiHandler.post(cronometroRunnable);
                        cronometroRodando = true;
                        uiHandler.post(() -> {
                            notification2.setText(String.format("Iniciou a medição %d da Rota!", Medicao+1));
                            notification2.setVisibility(View.VISIBLE);
                            new Handler().postDelayed(() -> notification.setVisibility(View.GONE), 1500);
                        });
                    }
                }

                if (F2 != null && F2.distance(latitudeAtual, longitudeAtual, F2.getLatitude(), F2.getLongitude())) {
                    if(d[Medicao][0] == 0.00){
                        if (Medicao == 0){
                            tempo_inicio2 = System.nanoTime();
                            d[Medicao][0] = (tempo_inicio2-tempo_inicio1) / 1_000_000_000.0;
                            Mostrar_medida(d[Medicao][0], 0);
                        }else{
                            if(cronometroRodando){
                                tempo_inicio2 = System.nanoTime();
                                d[Medicao][0] = (tempo_inicio2-tempo_inicio1) / 1_000_000_000.0;
                                Mostrar_medida(d[Medicao][0], 0);
                            }
                        }

                    }

                }
                if (F3 != null && F3.distance(latitudeAtual, longitudeAtual, F3.getLatitude(), F3.getLongitude())) {
                    if((d[Medicao][1] == 0.00)&&(d[Medicao][0] != 0.00)){
                        tempo_inicio3 = System.nanoTime();
                        d[Medicao][1] = (tempo_inicio3 - tempo_inicio2) / 1_000_000_000.0;
                        Mostrar_medida(d[Medicao][1], 1);

                    }
                }
                if (F4 != null && F4.distance(latitudeAtual, longitudeAtual, F4.getLatitude(), F4.getLongitude())) {
                    if((d[Medicao][2] == 0.00)&&(d[Medicao][1] != 0.00)){
                        tempo_inicio4 = System.nanoTime();
                        d[Medicao][2] = (tempo_inicio4 - tempo_inicio3) / 1_000_000_000.0;
                        Mostrar_medida(d[Medicao][2], 2);
                    }
                }
                if (F5 != null && F5.distance(latitudeAtual, longitudeAtual, F5.getLatitude(), F5.getLongitude())) {
                    if((d[Medicao][3] == 0.00)&&(d[Medicao][2] != 0.00)){
                        tempo_inicio5 = System.nanoTime();
                        d[Medicao][3] = (tempo_inicio5 - tempo_inicio4) / 1_000_000_000.0;
                        Mostrar_medida(d[Medicao][3], 3);
                    }

                }
                if (F6 != null && F6.distance(latitudeAtual, longitudeAtual, F6.getLatitude(), F6.getLongitude())) {
                    if((d[Medicao][4] == 0.00)&&(d[Medicao][3] != 0.00)){
                        tempo_inicio6 = System.nanoTime();
                        d[Medicao][4] = (tempo_inicio6 - tempo_inicio5) / 1_000_000_000.0;
                        Mostrar_medida(d[Medicao][4], 4);
                        Mostrar_medida(d, Medicao);
                        Medicao++;
                        uiHandler.removeCallbacks(cronometroRunnable);
                        cronometroRodando = false;
                    }
                }
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private void Mostrar_medida(double medida, int indice) {
            uiHandler.post(() -> {
                notification2.setText(String.format("F%d: %.4f segundos", indice + 1, medida));
                notification2.setVisibility(View.VISIBLE);
                new Handler().postDelayed(() -> notification2.setVisibility(View.GONE), 1500);
            });
    }
    private void Mostrar_medida(double[][] medida, int Medicao) {
        uiHandler.post(() -> {
            notification.setText(String.format("Medição %d\nF1: %.4f\nF2: %.4f\nF3: %.4f\nF4: %.4f\nF5: %.4f\n", Medicao + 1, medida[Medicao][0], medida[Medicao][1], medida[Medicao][2], medida[Medicao][3], medida[Medicao][4]));
            notification.setVisibility(View.VISIBLE);
            new Handler().postDelayed(() -> notification.setVisibility(View.GONE), 1500);
        });
    }
}