# Handle Beacon and GeoFence notifications manually

To remove notifications from the SDK and handle them yourself follow these steps

#### 1. Add a BroadcastReceiver

Use `ProximityEvent.process` to handle the intent
 
```java
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import co.nearbee.ProximityEvent;
import co.nearbee.geofence.repository.models.GeoFence;
import co.nearbee.models.NearBeacon;

public class ProximityEventReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Handle the events here
        ProximityEvent.process(intent, new ProximityEvent.Listener() {
            @Override
            public void onBeaconDetected(NearBeacon beacon) {
                Log.d("Nearbee", "Beacon in range " + beacon.getEddystoneUID());
            }

            @Override
            public void onGeoFenceDetected(GeoFence geoFence) {
                Log.d("Nearbee", "Inside geofence " + geoFence.getId());
            }

            @Override
            public void onBeaconRegionExit() {
                Log.d("Nearbee", "Beacon region exit");
            }
        });
    }
}

```
#### 2. Setup intent-filter
```xml
<receiver
    android:name=".ProximityEventReceiver"
    android:enabled="true"
    android:exported="true">
    <intent-filter>
        <action android:name="co.nearbee.PROXIMITY_EVENT" />
    </intent-filter>
</receiver>
```

#### 3. Add this metaData with the class name
```xml
<meta-data
    android:name="co.nearbee.notification_override"
    android:value=".ProximityEventReceiver" />
```