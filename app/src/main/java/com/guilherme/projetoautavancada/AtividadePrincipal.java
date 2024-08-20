package com.guilherme.projetoautavancada;

import static java.lang.String.format;
import static java.lang.String.valueOf;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.FirebaseFirestore;
import com.guilherme.mylibrary.Region;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class AtividadePrincipal extends AppCompatActivity implements OnMapReadyCallback {

    // Constantes
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    double[][] d1 = new double[10][5];
    double[][] d2 = new double[10][5];
    double[][] d3 = new double[10][5];
    int num_rota = 0;

    // Componentes da Interface do Usuário
    private TextView latitudeTextView;
    private TextView longitudeTextView;
    private TextView userNameTextView;
    private TextView userIdTextView;
    private TextView labelsizeFila;
    private TextView notification;
    private TextView notification2;
    private TextView num_medicao;
    private TextView Dados;

    private Button addRegionButton;
    private Button GravarBdButtom;
    private Button botaoCamada;
    private Button botaoLimparFila;
    private Button RD1;
    private Button RD3;
    private Button RD2;
    private ChipGroup Rotas;

    // Componentes do Mapa
    private GoogleMap mMap;
    private Marker currentMarker, M1, M2, M3, M4, M5, N2, N3, N4, N5, O2, O3, O4, O5, M6;
    BitmapDescriptor rota1;
    BitmapDescriptor rota2;
    BitmapDescriptor rota3;
    BitmapDescriptor Inicio;
    BitmapDescriptor Fim;
    BitmapDescriptor user;
    private Region F1 = new Region("Medidor 1", -21.280952, -44.765114, 0);
    private Region F2 = new Region("Medidor 2", -21.281311, -44.765197, 0);
    private Region F3 = new Region("Medidor 3", -21.281828, -44.764892, 0);
    private Region F4 = new Region("Medidor 4", -21.281947, -44.764307, 0);
    private Region F5 = new Region("Medidor 5", -21.281919, -44.763675, 0);
    private Region F6 = new Region("Medidor 6", -21.281873, -44.763057, 0);

    private Region G2 = new Region("Medidor 2", -21.280915, -44.764536, 0);
    private Region G3 = new Region("Medidor 3", -21.280906, -44.764039, 0);
    private Region G4 = new Region("Medidor 4", -21.281431, -44.763940, 0);
    private Region G5 = new Region("Medidor 5", -21.281929, -44.763780, 0);

    private Region H2 = new Region("Medidor 2", -21.280920, -44.764540, 0);
    private Region H3 = new Region("Medidor 3", -21.280892, -44.764001, 0);
    private Region H4 = new Region("Medidor 4", -21.280894, -44.763471, 0);
    private Region H5 = new Region("Medidor 5", -21.281244, -44.763053, 0);

    private Medir_Trajetoria_1 med1;
    private Medir_Trajetoria_2 med2;
    private Medir_Trajetoria_3 med3;

    // Dados do Usuário e Localização
    private double latitudeAtual = 0.0;
    private double longitudeAtual = 0.0;
    private int userId;
    private String userName;

    // Gerenciamento de Regiões
    private final Queue<Region> filaDeRegions = new LinkedList<>();
    private int contRegion = 0;
    private int contSubRegion = 0;
    private int contRestRegion = 0;
    private boolean ultimaAddSub = false;
    private Region auxRegion;


    // Concorrência e Armazenamento
    private final Semaphore semaphore = new Semaphore(1);
    private final Handler uiHandler = new Handler(Looper.getMainLooper());
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // SharedPreferences para armazenamento de configurações ou dados do usuário
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userNameTextView = findViewById(R.id.userNameTextView);
        userIdTextView = findViewById(R.id.userIdTextView);
        latitudeTextView = findViewById(R.id.latitudeTextView);
        longitudeTextView = findViewById(R.id.longitudeTextView);
        labelsizeFila = findViewById(R.id.labelsizeFila);
        notification = findViewById(R.id.notification);
        notification2 = findViewById(R.id.notification2);
        num_medicao = findViewById(R.id.num_medicao);
        Dados = findViewById(R.id.Dados);
        addRegionButton = findViewById(R.id.addRegionButton);
        GravarBdButtom = findViewById(R.id.GravarBdButtom);
        botaoCamada = findViewById(R.id.botaoCamada);
        RD1 = findViewById(R.id.RD1);
        RD2 = findViewById(R.id.RD2);
        RD3 = findViewById(R.id.RD3);
        botaoLimparFila = findViewById(R.id.botaoLimparFila);
        Rotas = findViewById(R.id.Rotas);
        rota1 = BitmapDescriptorFactory.fromResource(R.drawable.rota1);
        rota2 = BitmapDescriptorFactory.fromResource(R.drawable.rota2);
        rota3 = BitmapDescriptorFactory.fromResource(R.drawable.rota3);
        Inicio = BitmapDescriptorFactory.fromResource(R.drawable.inicio);
        Fim = BitmapDescriptorFactory.fromResource(R.drawable.fim);
        user = BitmapDescriptorFactory.fromResource(R.drawable.user);

        sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);

        // Verifique se é a primeira execução
        boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
        if (isFirstRun) {
            // Se for a primeira execução, atualize o valor de isFirstRun para false
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isFirstRun", false);
            editor.apply();

            // Inicie a RegisterActivity
            Intent iniciaRegister = new Intent(this, RegisterActivity.class);
            startActivity(iniciaRegister);
            finish();
            return;
        }


        // Tenta recuperar os dados do usuário da Intent ou das SharedPreferences
        Intent intent = getIntent();
        userName = intent.getStringExtra("NomeUsuario");
        userId = intent.getIntExtra("IdUsuario", -1); // -1 como valor padrão

        // Se os dados não forem encontrados na Intent, tenta recuperar das SharedPreferences
        if (userName == null || userName.isEmpty() || userId == -1) {
            userName = sharedPreferences.getString("NomeUsuario", "Nome de Usuário indisponível");
            userId = sharedPreferences.getInt("IdUsuario", -1); // -1 indica que o ID não está disponível
        }

        userNameTextView.setText(userName);
        if (userId != -1) {
            userIdTextView.setText(valueOf(userId));
        } else {
            userIdTextView.setText("ID não disponível");
        }

        addRegionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimarBotton.aplicarPulsacao(v);
            }
        });

        GravarBdButtom.setOnClickListener(v -> {
            AnimarBotton.aplicarPulsacao(v);
        });

        GravarBdButtom.setOnClickListener(v -> {
            AnimarBotton.aplicarPulsacao(v);
        });

        RD1.setOnClickListener(v -> {
            AnimarBotton.aplicarPulsacao(v);
            Reconciliar_dados(d2);
        });

        RD2.setOnClickListener(v -> {
            AnimarBotton.aplicarPulsacao(v);
            Reconciliar_dados(d3);
        });

        RD3.setOnClickListener(v -> {
            AnimarBotton.aplicarPulsacao(v);
            Reconciliar_dados(d1);
        });

        botaoLimparFila.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimarBotton.aplicarPulsacao(v);
                exportar_Dados_txt(d1);
            }
        });

        botaoLimparFila.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AnimarBotton.aplicarPulsacaoLong(v);

                return true;
            }
        });

        botaoCamada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimarBotton.aplicarPulsacao(v);
                if (mMap != null) {
                    // Alternar entre os tipos de mapa
                    if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
                        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    } else {
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    }
                }
            }
        });

        Rotas.setOnCheckedStateChangeListener((group, checkedIds) -> {
            RemoverTodosOsMarcadores();

            if (checkedIds.contains(R.id.chipRota1)) {
                AnimarBotton.aplicarPulsacao(findViewById(R.id.chipRota1));
                num_rota = 1;

                if (med2 != null) med2.interrupt();
                if (med3 != null) med3.interrupt();

                med1 = new Medir_Trajetoria_1(this, num_medicao, notification, notification2, labelsizeFila, addRegionButton, GravarBdButtom, uiHandler,
                        F1, F2, F3, F4, F5, F6, d1);
                med1.start();

            } else if (checkedIds.contains(R.id.chipRota2)) {
                AnimarBotton.aplicarPulsacao(findViewById(R.id.chipRota2));
                num_rota = 2;

                if (med1 != null) med1.interrupt();
                if (med3 != null) med3.interrupt();

                med2 = new Medir_Trajetoria_2(this, num_medicao, notification, notification2, labelsizeFila, addRegionButton, GravarBdButtom, uiHandler,
                        F1, G2, G3, G4, G5, F6, d3);
                med2.start();

            } else if (checkedIds.contains(R.id.chipRota3)) {
                AnimarBotton.aplicarPulsacao(findViewById(R.id.chipRota3));
                num_rota = 3;

                if (med1 != null) med1.interrupt();
                if (med2 != null) med2.interrupt();

                med3 = new Medir_Trajetoria_3(this, num_medicao, notification, notification2, labelsizeFila, addRegionButton, GravarBdButtom, uiHandler,
                        F1, H2, H3, H4, H5, F6, d2);
                med3.start();
            } else {
                num_rota = 0; // Nenhum chip selecionado
            }
            Adicionar_Medidores(rota1, rota2, rota3, Inicio, Fim, num_rota);
        });

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Solicitar permissões de localização em tempo de execução
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Começa a Thread quando a permissão já foi concedida
            new LerDadosGps(this).start();
        }
    }

    public void atualizarLocalization(double latitude, double longitude) {
        latitudeAtual = latitude;
        longitudeAtual = longitude;
        latitudeTextView.setText(format("Latitude: %.6f", latitudeAtual));
        longitudeTextView.setText(format("Longitude: %.6f", longitudeAtual));

        //labelsizeFila.setText(format("%d Regiões na fila!",filaDeRegions.size()));
        //Remove o marcador anterior, se houver
        if (currentMarker != null) {
            currentMarker.remove();
        }
        // Adicionar um novo marcador na localização atual
        currentMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(latitudeAtual, longitudeAtual))
                .title("Localização Atual").icon(user));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                inicializaRecursosLocalizacao(); // Inicializa recursos de GPS e mapa
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Permissão de Localização Necessária")
                        .setMessage("Esta aplicação precisa da permissão de localização. Por favor, conceda a permissão nas configurações do aplicativo.")
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                        .create().show();
            }
        }
    }

    private void inicializaRecursosLocalizacao() {
        new LerDadosGps(this).start(); // Inicia a thread de dados de GPS

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true); // Ativa a camada de localização no mapa
            FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(this);
            locationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
                }

            });
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        Adicionar_Medidores(rota1, rota2, rota3, Inicio, Fim, num_rota);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

            FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(this);
            locationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
                }
            });
        }
    }

    private void Adicionar_Medidores(BitmapDescriptor rota1, BitmapDescriptor rota2, BitmapDescriptor rota3, BitmapDescriptor Inicio, BitmapDescriptor Fim, int num_rota){
        if (num_rota == 0) {
            M1 = mMap.addMarker(new MarkerOptions().position(new LatLng(F1.getLatitude(), F1.getLongitude()))
                    .title(F1.getName()).icon(Inicio));
            M2 = mMap.addMarker(new MarkerOptions().position(new LatLng(F2.getLatitude(), F2.getLongitude()))
                    .title(F2.getName()).icon(rota1));
            M3 = mMap.addMarker(new MarkerOptions().position(new LatLng(F3.getLatitude(), F3.getLongitude()))
                    .title(F3.getName()).icon(rota1));
            M4 = mMap.addMarker(new MarkerOptions().position(new LatLng(F4.getLatitude(), F4.getLongitude()))
                    .title(F4.getName()).icon(rota1));
            M5 = mMap.addMarker(new MarkerOptions().position(new LatLng(F5.getLatitude(), F5.getLongitude()))
                    .title(F5.getName()).icon(rota1));
            N2 = mMap.addMarker(new MarkerOptions().position(new LatLng(G2.getLatitude(), G2.getLongitude()))
                    .title(G2.getName()).icon(rota2));
            N3 = mMap.addMarker(new MarkerOptions().position(new LatLng(G3.getLatitude(), G3.getLongitude()))
                    .title(G3.getName()).icon(rota2));
            N4 = mMap.addMarker(new MarkerOptions().position(new LatLng(G4.getLatitude(), G4.getLongitude()))
                    .title(G4.getName()).icon(rota2));
            N5 = mMap.addMarker(new MarkerOptions().position(new LatLng(G5.getLatitude(), G5.getLongitude()))
                    .title(G5.getName()).icon(rota2));
            O2 = mMap.addMarker(new MarkerOptions().position(new LatLng(H2.getLatitude(), H2.getLongitude()))
                    .title(H2.getName()).icon(rota3));
            O3 = mMap.addMarker(new MarkerOptions().position(new LatLng(H3.getLatitude(), H3.getLongitude()))
                    .title(H3.getName()).icon(rota3));
            O4 = mMap.addMarker(new MarkerOptions().position(new LatLng(H4.getLatitude(), H4.getLongitude()))
                    .title(H4.getName()).icon(rota3));
            O5 = mMap.addMarker(new MarkerOptions().position(new LatLng(H5.getLatitude(), H5.getLongitude()))
                    .title(H5.getName()).icon(rota3));
            M6 = mMap.addMarker(new MarkerOptions().position(new LatLng(F6.getLatitude(), F6.getLongitude()))
                    .title(F6.getName()).icon(Fim));
        } else if (num_rota == 1) {
            M2 = mMap.addMarker(new MarkerOptions().position(new LatLng(F2.getLatitude(), F2.getLongitude()))
                    .title(F2.getName()).icon(rota1));
            M3 = mMap.addMarker(new MarkerOptions().position(new LatLng(F3.getLatitude(), F3.getLongitude()))
                    .title(F3.getName()).icon(rota1));
            M4 = mMap.addMarker(new MarkerOptions().position(new LatLng(F4.getLatitude(), F4.getLongitude()))
                    .title(F4.getName()).icon(rota1));
            M5 = mMap.addMarker(new MarkerOptions().position(new LatLng(F5.getLatitude(), F5.getLongitude()))
                    .title(F5.getName()).icon(rota1));
        } else if (num_rota == 2) {
            N2 = mMap.addMarker(new MarkerOptions().position(new LatLng(G2.getLatitude(), G2.getLongitude()))
                    .title(G2.getName()).icon(rota2));
            N3 = mMap.addMarker(new MarkerOptions().position(new LatLng(G3.getLatitude(), G3.getLongitude()))
                    .title(G3.getName()).icon(rota2));
            N4 = mMap.addMarker(new MarkerOptions().position(new LatLng(G4.getLatitude(), G4.getLongitude()))
                    .title(G4.getName()).icon(rota2));
            N5 = mMap.addMarker(new MarkerOptions().position(new LatLng(G5.getLatitude(), G5.getLongitude()))
                    .title(G5.getName()).icon(rota2));
        } else if (num_rota == 3) {
            O2 = mMap.addMarker(new MarkerOptions().position(new LatLng(H2.getLatitude(), H2.getLongitude()))
                    .title(H2.getName()).icon(rota3));
            O3 = mMap.addMarker(new MarkerOptions().position(new LatLng(H3.getLatitude(), H3.getLongitude()))
                    .title(H3.getName()).icon(rota3));
            O4 = mMap.addMarker(new MarkerOptions().position(new LatLng(H4.getLatitude(), H4.getLongitude()))
                    .title(H4.getName()).icon(rota3));
            O5 = mMap.addMarker(new MarkerOptions().position(new LatLng(H5.getLatitude(), H5.getLongitude()))
                    .title(H5.getName()).icon(rota3));
        }
    }
    private void RemoverTodosOsMarcadores() {
        if (M2 != null) M2.remove();
        if (M3 != null) M3.remove();
        if (M4 != null) M4.remove();
        if (M5 != null) M5.remove();
        if (N2 != null) N2.remove();
        if (N3 != null) N3.remove();
        if (N4 != null) N4.remove();
        if (N5 != null) N5.remove();
        if (O2 != null) O2.remove();
        if (O3 != null) O3.remove();
        if (O4 != null) O4.remove();
        if (O5 != null) O5.remove();
    }

    private void Reconciliar_dados(double[][] d){
        boolean TemZero = false;
        for (int i = 0; i < d.length; i++) {
            for (int j = 0; j < d[i].length; j++) {
                if (d[i][j] == 0) {
                    TemZero = true;
                    break;
                }
            }
            if (TemZero) {
                break;
            }
        }
        if (!TemZero) {
            exportar_Dados_txt(d);
            double[] D = Calculo.Media(d);
            double[] Sigma = Calculo.DesvioPadrao(d, D);
            double[] V = Calculo.Variancia(Sigma);
            double[] Precisao = Calculo.Precisao(Sigma);
            double[] Bias = Calculo.Bias(D, Calculo.Media(D));
            double[] Incerteza = Calculo.Incerteza(Bias, Precisao);
            double[][] A = new double[][] { { 1, -1,  0,  0,  0 },
                                            { 0,  1, -1,  0,  0 },
                                            { 0,  0,  1, -1,  0 },
                                            { 0,  0,  0,  1, -1 }
            };
            Reconciliation rec = new Reconciliation(D, V, A);
            StringBuilder result = new StringBuilder();
            result.append("Médias: ");
            for (double media : D) {
                result.append(String.format("%.3f", media)).append(" ");
            }
            result.append("\n\nDesvios Padrão: ");
            for (double desvio : Sigma) {
                result.append(String.format("%.3f", desvio)).append(" ");
            }
            result.append("\n\nBias: ");
            for (double bias : Bias) {
                result.append(String.format("%.3f", bias)).append(" ");
            }
            result.append("\n\nPrecisão: ");
            for (double prec : Precisao) {
                result.append(String.format("%.3f", prec)).append(" ");
            }
            result.append("\n\nIncerteza: ");
            for (double inctz : Incerteza) {
                result.append(String.format("%.3f", inctz)).append(" ");
            }
            result.append("\n\nVariância: ");
            for (double variancia : V) {
                result.append(String.format("%.3f", variancia)).append(" ");
            }
            result.append("\n\nDados reconcilicados: \n").append(rec.printMatrix(rec.getReconciledFlow()));
            Dados.setText(result.toString());
            Dados.setVisibility(View.VISIBLE);
            new Handler().postDelayed(() -> Dados.setVisibility(View.GONE), 5000);
        } else {
            notification2.setText("Matriz com dados nulos!");
            notification2.setVisibility(View.VISIBLE);
            new Handler().postDelayed(() -> notification2.setVisibility(View.GONE), 1500);
        }
    }
    public void exportar_Dados_txt(double[][] d) {
        File directory = new File(Environment.getExternalStorageDirectory(), "MyAppData");
        if (!directory.exists()) {
            directory.mkdirs(); // Cria o diretório, se não existir
        }

        File file = new File(directory, "dados.txt");

        try (FileOutputStream fos = new FileOutputStream(file)) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < d.length; i++) {
                for (int j = 0; j < d[i].length; j++) {
                    sb.append(String.format("%.3f", d[i][j])).append("\t"); // Formata com 3 casas decimais e separa por tabulação
                }
                sb.append("\n"); // Adiciona uma nova linha após cada linha da matriz
            }
            fos.write(sb.toString().getBytes());
            fos.flush();
            notification2.setText("Dados exportados para dados.txt");
            notification2.setVisibility(View.VISIBLE);
            new Handler().postDelayed(() -> notification2.setVisibility(View.GONE), 1000);
        } catch (IOException e) {
            e.printStackTrace();
            notification2.setText("Erro ao exportar dados");
            notification2.setVisibility(View.VISIBLE);
            new Handler().postDelayed(() -> notification2.setVisibility(View.GONE), 1000);
        }
    }

    public double getLatitudeAtual() {
        return latitudeAtual;
    }

    public double getLongitudeAtual() {
        return longitudeAtual;
    }
}