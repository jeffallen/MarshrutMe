package org.nella.mm;

import android.location.Location;

import org.osmdroid.views.overlay.mylocation.IMyLocationConsumer;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;

import java.util.Timer;
import java.util.TimerTask;

public class MockLocationProvider implements IMyLocationProvider {
    IMyLocationConsumer consumer;
    Timer t;
    TimerTask task;
    double lat, lon;
    MockLocationProvider me;
    long period = 2000; /* ms */

    public boolean startLocationProvider(IMyLocationConsumer myLocationConsumer) {
        me = this;
        consumer = myLocationConsumer;
        lat = 40.501649;
        lon = 72.819181;

        task = new TimerTask() {
            @Override
            public void run() {
                lat += 0.0001;
                lon += 0.0001;
                Location l = new Location("mock");
                l.setLatitude(lat);
                l.setLongitude(lon);
                consumer.onLocationChanged(l, me);
            }
        };
        t = new Timer("mock gps");
        t.scheduleAtFixedRate(task, period, period);
        return true;
    };

    public void stopLocationProvider() {
        t.cancel();
        return;
    };

    public Location getLastKnownLocation() {
        Location l = new Location("mock");
        l.setLatitude(lat);
        l.setLongitude(lon);
        consumer.onLocationChanged(l, this);
        return l;
    };

}
