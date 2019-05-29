package co.nearbee.sdksample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import co.nearbee.NearBeaconListener;
import co.nearbee.NearBee;
import co.nearbee.NearBeeException;
import co.nearbee.models.NearBeacon;

public class MainActivity extends AppCompatActivity implements NearBeaconListener {

    private static final int REQUEST_LOCATION_PERMISSION = 2453;
    NearBee nearBee;
    ListAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<NearBeacon> beacons;
    TextView errorText;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        errorText = findViewById(R.id.error_text);
        recyclerView = findViewById(R.id.beacon_list);
        progressBar = findViewById(R.id.loading);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                this, DividerItemDecoration.VERTICAL
        );
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startScan();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (nearBee != null)
            nearBee.stopScanning();
    }

    private void startScan() {
        errorText.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        if (nearBee == null)
            nearBee = new NearBee.Builder(this)
                    .setBackgroundNotificationsEnabled(true)
                    .build();
        if (!nearBee.isScanning())
            nearBee.startScanning(this);
        nearBee.enableBackgroundNotifications(true);
    }

    @Override
    public void onUpdate(ArrayList<NearBeacon> beaconsInRange) {
        progressBar.setVisibility(View.INVISIBLE);
        if (adapter == null) {
            this.beacons = new ArrayList<>(beaconsInRange);
            adapter = new ListAdapter(this.beacons);
            LinearLayoutManager manager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(manager);
            recyclerView.setAdapter(adapter);
        }
        if (beaconsInRange.isEmpty()) {
            errorText.setVisibility(View.VISIBLE);
            errorText.setText("No beacons in range");
        } else {
            errorText.setVisibility(View.INVISIBLE);
        }
        Log.d("NearBee SDK", beaconsInRange.size() + " beacons in range");
    }

    @Override
    public void onBeaconLost(ArrayList<NearBeacon> lost) {
        if (adapter != null) {
            for (NearBeacon beacon : lost) {
                beacons.remove(beacon);
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBeaconFound(ArrayList<NearBeacon> found) {
        if (adapter != null) {
            for (NearBeacon beacon : found) {
                if (!beacons.contains(beacon))
                    beacons.add(beacon);
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onError(NearBeeException exception) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    MainActivity.REQUEST_LOCATION_PERMISSION);
        }
        progressBar.setVisibility(View.INVISIBLE);
        errorText.setVisibility(View.VISIBLE);
        errorText.setText(exception.getMessage());
        Log.e("NearBee SDK", "Error: " + exception.getMessage());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScan();
            } else {
                finish();
            }
        }
    }

}
