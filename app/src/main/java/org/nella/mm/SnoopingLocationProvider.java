package org.nella.mm;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import org.osmdroid.views.overlay.mylocation.IMyLocationConsumer;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;

public class SnoopingLocationProvider implements IMyLocationProvider, IMyLocationConsumer {
    IMyLocationConsumer fwdTo = null;
    IMyLocationConsumer lc = null;
    IMyLocationProvider lp = null;

    public SnoopingLocationProvider(IMyLocationProvider lp0, IMyLocationConsumer fwdTo0) {
        fwdTo = fwdTo0;
        lp = lp0;
    }

    @Override
    public boolean startLocationProvider(IMyLocationConsumer myLocationConsumer) {
        lc = myLocationConsumer;
        return lp.startLocationProvider(this);
    }

    @Override
    public void stopLocationProvider() {
        lp.stopLocationProvider();
        lc = null;
    }

    @Override
    public Location getLastKnownLocation() {
        return lp.getLastKnownLocation();
    }

    @Override
    public void onLocationChanged(Location l, IMyLocationProvider p) {
        if (lc != null) {
            lc.onLocationChanged(l, p);
        }
        fwdTo.onLocationChanged(l, p);
    }

}