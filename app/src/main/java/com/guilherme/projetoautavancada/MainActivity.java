package com.guilherme.projetoautavancada;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.guilherme.mylibrary.*;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final Semaphore semaphore = new Semaphore(1);
    private GoogleMap mMap;
    private Marker currentMarker;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private TextView latitudeTextView;
    private TextView longitudeTextView;
    private TextView userNameTextView;
    private TextView userIdTextView;
    private TextView labelsizeFila;
    private Button addRegionButton;
    private Button GravarBdButtom;
    private Button botaoCamada;

    private double latitudeAtual = 0.0;
    private double longitudeAtual = 0.0;
    private Queue<String> filaDeRegions = new LinkedList<>();
    private int contRegion = 0;
    private int contSubRegion = 0;
    private int contRestRegion = 0;
    private boolean ultimaAddSub = false;
    private SharedPreferences sharedPreferences;
    private int  userId;
    private String userName;
    private Handler uiHandler = new Handler(Looper.getMainLooper());
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public Region auxRegion = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userNameTextView = findViewById(R.id.userNameTextView);
        userIdTextView = findViewById(R.id.userIdTextView);
        latitudeTextView = findViewById(R.id.latitudeTextView);
        longitudeTextView = findViewById(R.id.longitudeTextView);
        labelsizeFila = findViewById(R.id.labelsizeFila);
        addRegionButton = findViewById(R.id.addRegionButton);
        GravarBdButtom = findViewById(R.id.GravarBdButtom);
        botaoCamada = findViewById(R.id.botaoCamada);

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
            userName = sharedPreferences.getString("NomeUsuario", "Nome de Usuário não disponível");
            userId = sharedPreferences.getInt("IdUsuario", -1); // -1 indica que o ID não está disponível
        }

        userNameTextView.setText(userName);
        if (userId != -1) {
            userIdTextView.setText(String.valueOf(userId));
        } else {
            userIdTextView.setText("ID não disponível");
        }

        addRegionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AtomicInteger contadorDeRespostas = new AtomicInteger(0);
                AtomicBoolean RegProx = new AtomicBoolean(false);
                AtomicBoolean SubRegProx = new AtomicBoolean(false);
                AtomicBoolean RestRegProx = new AtomicBoolean(false);

                CallbackConsulta callback = (existeRegProx, existeSubRegProx, existeRestRegProx, regionMain) -> {
                    if (existeRegProx) {
                        RegProx.set(true);
                        auxRegion = regionMain;
                    }
                    if (existeSubRegProx) SubRegProx.set(true);
                    if (existeRestRegProx) RestRegProx.set(true);

                    if (contadorDeRespostas.incrementAndGet() == 2) {
                        partiuAddRegion(RegProx, SubRegProx, RestRegProx);
                    }
                };

                Thread consultaNaFila = new ConsultaFila(filaDeRegions, latitudeAtual, longitudeAtual, semaphore, callback, auxRegion);
                Thread consultarBd = new ConsultaBD(db, latitudeAtual, longitudeAtual, callback, auxRegion);

                consultaNaFila.start();
                consultarBd.start();
            }

            private void partiuAddRegion(AtomicBoolean RegProx, AtomicBoolean SubRegProx, AtomicBoolean RestRegProx) {
                if (!RegProx.get() && !SubRegProx.get() && !RestRegProx.get()) {
                    addNewRegion();
                } else if (RegProx.get() && !SubRegProx.get() && RestRegProx.get()) {
                    addNewSubRegion();
                } else if (RegProx.get() && SubRegProx.get() && !RestRegProx.get()) {
                    addNewRestrictedRegion();
                } else if (RegProx.get() && !SubRegProx.get() && !RestRegProx.get()) {
                    addSUBorREST();
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "Região/SubRegião/Região Restrita já cadastrada!", Toast.LENGTH_LONG).show();
                    });
                }
            }

            private void addNewRegion() {
                contRegion++;
                Region atualRegion = new Region("Região " + contRegion, latitudeAtual, longitudeAtual, userId, "Region");

                CriptografarDados criptografarRegion = new CriptografarDados(atualRegion);
                criptografarRegion.start();
                try {
                    criptografarRegion.join();  // Aguarda a conclusão da thread de criptografia
                    String regionJsonCriptografada = criptografarRegion.getEncryptedJson();  // Obtém o resultado após a conclusão
                    addRegionFila(regionJsonCriptografada);  // Adiciona a string criptografada à fila
                } catch (InterruptedException e) {
                    System.err.println("Thread interrompida: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            private void addNewSubRegion() {
                try {
                    contSubRegion++;
                    SubRegion atualSubRegion = new SubRegion("Sub Região " + contSubRegion + " da " + auxRegion.getName(), latitudeAtual, longitudeAtual, userId, auxRegion, "SubRegion");

                    CriptografarDados criptografarSubRegion = new CriptografarDados(atualSubRegion);
                    criptografarSubRegion.start();
                    try {
                        criptografarSubRegion.join();  // Aguarda a conclusão da thread de criptografia
                        String SubregionJsonCriptografada = criptografarSubRegion.getEncryptedJson();  // Obtém o resultado após a conclusão
                        addRegionFila(SubregionJsonCriptografada);  // Adiciona a string criptografada à fila
                    } catch (InterruptedException e) {
                        System.err.println("Thread interrompida: " + e.getMessage());
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    // Trata a exceção, tal como exibindo um erro ou registrando em logs
                    System.err.println("\n\nErro ao criptografar os dados: " + e.getMessage() + "\n\n");
                    e.printStackTrace();
                }
            }

            private void addNewRestrictedRegion() {
                contRestRegion++;
                RestrictedRegion atualRestRegion = new RestrictedRegion("Região restrita " + contRestRegion + " da " + auxRegion.getName(), latitudeAtual, longitudeAtual, userId, auxRegion, true, "RestrictedRegion");

                CriptografarDados criptografarRestRegion = new CriptografarDados(atualRestRegion);
                criptografarRestRegion.start();
                try {
                    criptografarRestRegion.join();  // Aguarda a conclusão da thread de criptografia
                    String RestregionJsonCriptografada = criptografarRestRegion.getEncryptedJson();  // Obtém o resultado após a conclusão
                    addRegionFila(RestregionJsonCriptografada);  // Adiciona a string criptografada à fila
                } catch (InterruptedException e) {
                    System.err.println("Thread interrompida: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            private void addSUBorREST() {
                if (ultimaAddSub) {
                    ultimaAddSub = false;
                    addNewRestrictedRegion();
                } else {
                    ultimaAddSub = true;
                    addNewSubRegion();
                }
            }

            private void addRegionFila(String region) {
                Thread adicionarNaFila = new AdicionarFila(filaDeRegions, region, semaphore);
                adicionarNaFila.start();
                try {
                    adicionarNaFila.join();
                    Region regionDescriptografada = null;
                    try {
                        DescriptografarDados descriptografarDados = new DescriptografarDados(region);
                        descriptografarDados.start();
                        descriptografarDados.join();  // Aguarda a conclusão da thread de descriptografia
                        regionDescriptografada = descriptografarDados.getDecryptedJson();  // Obtém o resultado após a conclusão

                        String tipoRegion = "Tipo não identificado";
                        if ("SubRegion".equals(regionDescriptografada.getType())) {
                            tipoRegion = "Sub Região";
                        } else if ("RestrictedRegion".equals(regionDescriptografada.getType())) {
                            tipoRegion = "Região Restrita";
                        }else if("Region".equals(regionDescriptografada.getType())){
                            tipoRegion = "Região";
                        }
                        String finalTipoRegion = tipoRegion;
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, finalTipoRegion + " cadastrada!", Toast.LENGTH_LONG).show());
                    } catch (InterruptedException e) {
                        System.err.println("Thread interrompida: " + e.getMessage());
                        e.printStackTrace();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });

        GravarBdButtom.setOnClickListener(v -> {
            RemFaddBD threadRemoverDaFila = new RemFaddBD(db, filaDeRegions, semaphore, uiHandler, MainActivity.this, labelsizeFila);
            threadRemoverDaFila.start();
        });

        botaoCamada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Solicitar permissões de localização em tempo de execução
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Começa a Thread quando a permissão já foi concedida
            new LerDadosGps(this).start();
        }
    }

    public void updateLocationUI(double latitude, double longitude) {
        latitudeAtual = latitude;
        longitudeAtual = longitude;
        latitudeTextView.setText(String.format("Latitude: %.6f", latitude));
        longitudeTextView.setText(String.format("Longitude: %.6f", longitude));
        labelsizeFila.setText("Regiões na fila: " + filaDeRegions.size());

        // Remove o marcador anterior, se houver
        if (currentMarker != null) {
            currentMarker.remove();
        }

        // Cria um novo objeto LatLng para a localização atualizada
        LatLng latLng = new LatLng(latitude, longitude);

        // Adicionar um novo marcador na localização atual
        currentMarker = mMap.addMarker(new MarkerOptions().position(latLng)
                .title("Localização Atual"));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new LerDadosGps(this).start();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Permissão de Localização Necessária")
                        .setMessage("Esta aplicação precisa da permissão de localização para adicionar regiões." +
                                " Por favor, conceda a permissão nas configurações do aplicativo.")
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                        .create().show();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
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
}