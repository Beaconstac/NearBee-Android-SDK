# NearBee SDK for Android

You will need an API key for the NearBee SDK service to work.

## Integration with your existing project in Android Studio

### Add this to your project level build.gradle
```groovy
allprojects {
    repositories {
        …
        maven {
            url  "https://dl.bintray.com/mobstac/maven"
        }
        …
    }
}
```

### In the `build.gradle` file of the app, add the following in the dependencies section:

```groovy
dependencies {
    …
    implementation 'co.nearbee:nearbeesdk:2.1.5'
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
nearBee.startScanning(new NearBeaconListener() {

    @Override
    public void onUpdate(ArrayList<NearBeacon> beaconsInRange) {
        // An updated list of beacons currently in range
    }

    @Override
    public void onBeaconLost(ArrayList<NearBeacon> lostBeacons) {
        // List of beacons that went out of range
    }

    @Override
    public void onBeaconFound(ArrayList<NearBeacon> foundBeacons) {
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

#### 4. Changing background notification state
```java
// Change background notification state after initialization
nearBee.enableBackgroundNotifications(true);
```

#### 5. Clear local beacon cache
```java
// This clears the cache and forces the server to re-fetch beacon notifications
nearBee.clearNotificationCache();
```

### Getting attachment data from the Beacon object

There are two types of attachments -

#### PhysicalWeb
These are extracted from the physical web url of the beacon
```java
beacon.getAttachments().getPhysicalWebAttachment();
```
PhysicalWeb properties

```java
// Title
physicalWeb.getTitle();
// Description
physicalWeb.getDescription();
// Url for the icon
physicalWeb.getIconURL();
// Physical web url
physicalWeb.getUrl();
// Returns if the url is currexntly active
physicalWeb.isActive();
```

#### ProximityAttachment
This comes from Google Nearby attachment for a specific beacon
```java
// returns a list of ProximityAttachment objects
beacon.getAttachments().getProximityApiAttachments();
```
ProximityAttachment properties

In addition to all the properties from physical web, ProximityAttachment has extra properties
```java
// Url for banner image
proximityAttachment.getBannerImageURL();
// Banner type, portrait = 1 or landscape = 2
proximityAttachment.getBannerType();
// ISO code for the language
proximityAttachment.getLanguage();
```

### Convenience methods for getting attachments
Get the `ProximityAttachment` for the current device locale language.
##### getAttachmentForCurrentLanguage()
Will return `null` if ProximityAttachment is not available for that language
```java
beacon.getAttachmentForCurrentLanguage(context);
```
##### getBestAvailableAttachment()
Returns a `BeaconAttachment` object, which will be a `ProximityAttachment` if an attachment is found for the current language, or a `PhysicalWeb` otherwise

Will return `null` if no attachments are present for this beacon
```java
beacon.getBestAvailableAttachment(context);
```

### Launch the url associated with the beacon
```java
// BeaconAttachment is the base class for both ProximityAttachment and PhysicalWeb
beacon.launchUrl(context, beaconAttachment);
```

### Getting business data from the Beacon object
Beacons may contain a Business object which is the `Place` associated with the beacon
```java
Business business = beacon.getBusiness();
```
They have following properties
```java
// Color associated with the place (int)
business.getColor();
// Color code associated with the place (Hex code)
business.getColorCode();
// Cover image url
business.getCoverURL();
// Google place id
business.getGooglePlaceID();
// Place name
business.getName();
// Icon image url
business.getIconURL();
```


### Overriding beacon notification on-click behaviour

##### 1. Add a class which extends NotificationManager

```java
package com.your_app_package;

import android.content.Context;
import android.content.Intent;

import co.nearbee.NotificationManager;
import co.nearbee.models.BeaconAttachment;
import co.nearbee.models.NearBeacon;


public class MyNotificationManager extends NotificationManager {

    public MyNotificationManager(Context context) {
        super(context);
    }

    @Override
    public Intent getAppIntent(Context context) {
        // This intent is for handling grouped notification click
        return new Intent(context, MainActivity.class);
    }

    @Override
    public Intent getBeaconIntent(Context context, NearBeacon nearBeacon) {
        // This intent is for handling individual notification click
        // Pass the intent of the activity that you want to be opened on click
        if (nearBeacon.getBusiness() != null) {
            BeaconAttachment attachment = nearBeacon.getBestAvailableAttachment(context);
            if (attachment != null) {
                final Intent intent = new Intent(context, MainActivity.class);
                // pass the url from the beacon, so that it can be opened from your activity
                intent.putExtra("url", attachment.getUrl());
                return intent;
            }
        }
        return null;
    }

}

```

##### 2. Add this metadata to your `AndroidManifest.xml`

```xml
<meta-data
    android:name="co.nearbee.notification_util"
    android:value=".MyNotificationManager" />
```

## Geofencing


#### Start monitoring

```java
nearBee.startGeoFenceMonitoring().setSuccessListener(new SuccessListener<Void>() {
    @Override
    public void onSuccess(Void aVoid) {
        Log.d("NearBee GeoFence", "Setup success");
    }
}).setErrorListener(new ErrorListener() {
    @Override
    public void onError(Exception e) {
        Log.d("NearBee GeoFence", "Setup failed");
    }
});
```

#### Stop monitoring
```java
nearBee.stopGeoFenceMonitoring().setSuccessListener(new SuccessListener<Void>() {
    @Override
    public void onSuccess(Void aVoid) {
        Log.d("NearBee GeoFence", "Removal success");
    }
}).setErrorListener(new ErrorListener() {
    @Override
    public void onError(Exception e) {
        Log.d("NearBee GeoFence", "Removal failed");
    }
});
```

#### Check monitoring state
```java
boolean enabled = nearBee.isGeoFenceMonitoringEnabled();
```

### Overriding GeoFence notification on-click behaviour

##### 1. Add a class which extends NotificationManager

```java
package com.your_app_package;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import co.nearbee.geofence.GeoAttachment;
import co.nearbee.geofence.GeoNotificationManager;
import co.nearbee.geofence.repository.models.GeoFence;

public class MyGeoNotificationManager extends GeoNotificationManager {

    public MyGeoNotificationManager(Context context) {
        super(context);
    }

    @Nullable
    @Override
    public Intent getGeoFenceIntent(Context context, GeoFence geoFence) {
        GeoAttachment attachment = geoFence.getAttachment();
        if (attachment != null) {
            final Intent intent = new Intent(context, MyActivity.class);
            // pass the url from the beacon, so that it can be opened from your activity
            intent.putExtra("url", attachment.getUrl());
            return intent;
        }
        return null;
    }

    @Override
    public Intent getAppIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

}

```

##### 2. Add this metadata to your `AndroidManifest.xml`

```xml
<meta-data
    android:name="co.nearbee.geo_notification_util"
    android:value=".MyGeoNotificationManager" />
```

## Handle notifications manually

By default the NearBee SDK displays the notifications. To disable this and take control of Beacon and GeoFence events follow [this guide](NotificationOverride.md).