package com.example.lab11;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap maCarte;
    private LocationManager gestionnaireLocalisation;
    private Marker marqueurActuel;
    private static final int CODE_PERMISSION_LOC = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialisation du fragment de la carte
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_view);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        gestionnaireLocalisation = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        maCarte = googleMap;

        // Position par défaut (ex: Paris)
        LatLng positionInitiale = new LatLng(48.8566, 2.3522);
        maCarte.moveCamera(CameraUpdateFactory.newLatLngZoom(positionInitiale, 10));

        verifierEtActiverLocalisation();
    }

    private void verifierEtActiverLocalisation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Demander la permission si elle n'est pas accordée
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, CODE_PERMISSION_LOC);
        } else {
            // Permission déjà accordée, on commence le suivi
            demarrerSuiviPosition();
        }
    }

    private void demarrerSuiviPosition() {
        try {
            // On écoute via le réseau et le GPS pour plus de fiabilité
            gestionnaireLocalisation.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    2000, // Mise à jour toutes les 2 secondes
                    5,    // Ou tous les 5 mètres
                    ecouteurPosition
            );

            // Vérification si le GPS est activé
            if (!gestionnaireLocalisation.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                afficherAlerteGpsDesactive();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private final LocationListener ecouteurPosition = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location localisation) {
            LatLng coordonnees = new LatLng(localisation.getLatitude(), localisation.getLongitude());

            // Mise à jour du marqueur unique pour éviter de surcharger la carte
            if (marqueurActuel == null) {
                marqueurActuel = maCarte.addMarker(new MarkerOptions()
                        .position(coordonnees)
                        .title("Ma position actuelle"));
            } else {
                marqueurActuel.setPosition(coordonnees);
            }

            // Animation fluide vers la nouvelle position
            maCarte.animateCamera(CameraUpdateFactory.newLatLngZoom(coordonnees, 15f));

            Toast.makeText(MainActivity.this, "Position : " + localisation.getLatitude() + ", " + localisation.getLongitude(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
            if (LocationManager.GPS_PROVIDER.equals(provider)) {
                afficherAlerteGpsDesactive();
            }
        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {
            Toast.makeText(MainActivity.this, "Localisation activée : " + provider, Toast.LENGTH_SHORT).show();
        }
    };

    private void afficherAlerteGpsDesactive() {
        new AlertDialog.Builder(this)
                .setTitle("GPS désactivé")
                .setMessage("Votre GPS semble être désactivé. Voulez-vous l'activer dans les paramètres ?")
                .setCancelable(false)
                .setPositiveButton("Paramètres", (dialog, id) -> {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                })
                .setNegativeButton("Annuler", (dialog, id) -> dialog.cancel())
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODE_PERMISSION_LOC) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission accordée", Toast.LENGTH_SHORT).show();
                demarrerSuiviPosition();
            } else {
                Toast.makeText(this, "Permission refusée. La carte ne pourra pas afficher votre position.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
