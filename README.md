# NearBee SDK for Android

You will need an API key for the NearBee SDK service to work.

## Integration with your existing project in Android Studio

### In the `build.gradle` file of the app, add the following in the dependencies section:

```groovy
dependencies {
    ...
    implementation 'co.nearbee:nearbeesdk:1.0.2'
}
```

##### Latest version

[ ![Download](https://api.bintray.com/packages/mobstac/maven/nearbeesdk/images/download.svg) ](https://bintray.com/mobstac/maven/nearbeesdk/_latestVersion) 


## Permissions
#### NearBee requires the following permissions.
```xml
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```

### Runtime permissions

##### Location
NearBee requires the location permission to scan for Beacons.


## Usage

#### 1. Add your API key and Orgnization ID to the `AndroidManifest.xml` as follows

```xml
<application>
…
…
    <meta-data
        android:name="co.nearbee.api_key"
        android:value="MY_DEV_TOKEN" />

    <meta-data
        android:name="co.nearbee.organization_id"
        android:value="123" />
…
…
</application>
```

#### 2. Initialize SDK
```java
NearBee.Builder builder = new NearBee.Builder(context)
                .setBackgroundNotificationsEnabled(true)
                .setCallBackInterval(5);
nearBee = builder.build();
```

##### Builder options
```java
setBackgroundNotificationsEnabled(true);
```
Enables notifications from Nearby beacons in the Background. If any requirements are not met an error will be logged by the Background service. To change the behaviour (enabled to disabled and vice-versa) you need to re-initialize the SDK via the Builder as mentioned above.

```java
setCallBackInterval(timeInSeconds);
```
Sets the time between callbacks for displaying beacon notifications in the foreground. This has no effect on the background notification behaviour.

#### 3. Displaying available beacon notifications in Application UI
```java
// Start scanning and get updates
nearBee.startScanning(new NearBeeListener() {
    @Override
    public void onUpdate(ArrayList<NearBeeBeacon> beaconsInRange) {
        // An updated list of beacons currently in range
    }

    @Override
    public void onBeaconLost(ArrayList<NearBeeBeacon> lostBeacons) {
        // List of beacons that went out of range
    }

    @Override
    public void onBeaconFound(ArrayList<NearBeeBeacon> foundBeacons) {
        // List of beacons that appeared after last update
    }

    @Override
    public void onError(NearBeeException exception) {
        // Any error preventing Nearbee from scanning for beacons.
        // This error must be resolved and nearBee.startScanning should be called again.
    }
});
```

```java
// Stop scanning 
nearBee.stopScanning();
// Should ideally be done when the app goes to the background
```