package com.example.mentalhealthapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class ResultActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    private Button openMapsButton;

    private final ActivityResultLauncher<String[]> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean fine = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                Boolean coarse = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);

                if (fine != null && fine) {
                    // Precise location access granted
                    getCurrentLocationAndShowMap();
                } else if (coarse != null && coarse) {
                    // Approximate location access granted
                    getCurrentLocationAndShowMap();
                } else {
                    Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show();
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        openMapsButton = findViewById(R.id.openMapsButton);

        openMapsButton.setOnClickListener(v -> showLocationChoiceDialog());
    }

    private void showLocationChoiceDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Choose Option")
                .setMessage("Would you like to use your current location or enter an address manually?")
                .setPositiveButton("Use Current Location", (dialog, which) -> requestLocationPermission())
                .setNegativeButton("Enter Address", (dialog, which) -> promptAddressInput())
                .show();
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocationAndShowMap();
        } else {
            locationPermissionRequest.launch(new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }


    private void getCurrentLocationAndShowMap() {
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                Uri mapUri = Uri.parse("https://www.google.com/maps/search/psychiatrist/@" +
                        latitude + "," + longitude + ",15z");
                openMap(mapUri);
            } else {
                Toast.makeText(this, "Unable to fetch current location. Try again.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error getting location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void promptAddressInput() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Address");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);
        builder.setView(input);

        builder.setPositiveButton("Search", (dialog, which) -> {
            String address = input.getText().toString().trim();
            if (!address.isEmpty()) {
                Uri mapUri = Uri.parse("https://www.google.com/maps/search/psychiatrist+near+" + Uri.encode(address));
                openMap(mapUri);
            } else {
                Toast.makeText(this, "Address cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showPermissionDeniedDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permission Required")
                .setMessage("You've denied location permission permanently. Please enable it in settings to use this feature.")
                .setPositiveButton("Open Settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void openMap(Uri mapUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, mapUri);
        startActivity(intent);
    }
}
