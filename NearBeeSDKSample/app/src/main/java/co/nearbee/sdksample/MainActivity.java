package co.nearbee.sdksample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;

import co.nearbee.NearBee;
import co.nearbee.NearBeeBeacon;
import co.nearbee.NearBeeException;
import co.nearbee.NearBeeListener;

public class MainActivity extends AppCompatActivity implements NearBeeListener {

    NearBee nearBee;
    ListAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<NearBeeBeacon> beacons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.beacon_list);
        nearBee = new NearBee.Builder(this)
                .setBackgroundNotificationsEnabled(true)
                .build();
        nearBee.startScanning(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nearBee != null && !nearBee.isScanning())
            nearBee.startScanning(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (nearBee != null)
            nearBee.stopScanning();
    }

    @Override
    public void onUpdate(ArrayList<NearBeeBeacon> beaconsInRange) {
        Log.e("sdk", "Found " + beaconsInRange.size() + " beacons");
        if (adapter == null) {
            this.beacons = new ArrayList<>(beaconsInRange);
            adapter = new ListAdapter(this.beacons);
            LinearLayoutManager manager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(manager);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onBeaconLost(ArrayList<NearBeeBeacon> lost) {
        Log.e("sdk", "Lost " + lost.size() + " beacons");
        if (adapter != null) {
            for (NearBeeBeacon beacon : lost) {
                beacons.remove(beacon);
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBeaconFound(ArrayList<NearBeeBeacon> found) {
        for (NearBeeBeacon beacon : found) {
            Log.e("sdk", "Found " + beacon.getNotification().getTitle());
        }
        if (adapter != null) {
            for (NearBeeBeacon beacon : found) {
                if (!beacons.contains(beacon))
                    beacons.add(beacon);
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onError(NearBeeException exception) {
        Log.e("sdk", "Error: " + exception.getMessage());
    }
}
