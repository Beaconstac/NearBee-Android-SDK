package co.nearbee.sdksample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import co.nearbee.NearBee;
import co.nearbee.common.ErrorListener;
import co.nearbee.common.SuccessListener;

public class GeoFenceActivity extends AppCompatActivity {

    private Button enable;
    private TextView state;
    private NearBee nearBee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_fence);

        state = findViewById(R.id.geo_state);
        enable = findViewById(R.id.geo_button);

        nearBee = new NearBee.Builder(this).build();
        setupState();

        enable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nearBee.isGeoFenceMonitoringEnabled()) {
                    nearBee.stopGeoFenceMonitoring().setSuccessListener(successListener).setErrorListener(errorListener);
                } else {
                    nearBee.startGeoFenceMonitoring().setSuccessListener(successListener).setErrorListener(errorListener);
                }
            }
        });
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, GeoFenceActivity.class);
        context.startActivity(intent);
    }

    private SuccessListener<Void> successListener = new SuccessListener<Void>() {
        @Override
        public void onSuccess(Void aVoid) {
            setupState();
        }
    };

    private ErrorListener<Exception> errorListener = new ErrorListener<Exception>() {
        @Override
        public void onError(Exception e) {
            setupState();
            Toast.makeText(GeoFenceActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    private void setupState() {
        boolean enabled = false;
        if (nearBee != null)
            enabled = nearBee.isGeoFenceMonitoringEnabled();
        if (enabled) {
            state.setText("GeoFence monitoring is enabled");
            enable.setText("Disable");
        } else {
            state.setText("GeoFence monitoring is disabled");
            enable.setText("Enable");
        }
    }

}
