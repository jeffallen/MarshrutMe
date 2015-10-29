package org.nella.mm;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.mylocation.IMyLocationConsumer;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements IMyLocationConsumer {
    public static final String tag = "mm.MainActivity";

    Routedb.Db rdb = null;

    MyLocationNewOverlay myLocationOverlay = null;

    SingleItemOverlay nearestOverlay = null;
    Drawable nearestMark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MyMapView mapView = (MyMapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);

        // Prepare the nearest mark overlay. See onLocationChanged for when it
        // is set.
        nearestMark = ContextCompat.getDrawable(this, android.R.drawable.star_on);
        int markerWidth = nearestMark.getIntrinsicWidth();
        int markerHeight = nearestMark.getIntrinsicHeight();
        nearestMark.setBounds(0, markerHeight, markerWidth, 0);
        ResourceProxy resourceProxy = new DefaultResourceProxyImpl(getApplicationContext());
        nearestOverlay = new SingleItemOverlay(nearestMark, resourceProxy);
        mapView.getOverlays().add(nearestOverlay);

        AssetManager am = this.getAssets();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            InputStream is = am.open("routedb.zip");

            int nRead;
            byte[] data = new byte[16384];

            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
        } catch (Exception e) {
            Log.e(tag, "Could not open routedb:" + e);
        }

        try {
            rdb = Routedb.Load(buffer.toByteArray());
            Routedb.Box b = rdb.Bounds();
            mapView.boundingBox = new BoundingBoxE6(b.getN(), b.getE(), b.getS(), b.getW());
        } catch (Exception e) {
            Log.e(tag, "could not load routedb: " + e);
        }

        IMyLocationProvider lp;
        lp = new MockLocationProvider();
        // lp = new GpsMyLocationProvider(this);
        SnoopingLocationProvider slp = new SnoopingLocationProvider(lp, this);

        myLocationOverlay = new MyLocationNewOverlay(this, slp, mapView);
        mapView.getOverlays().add(myLocationOverlay);
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.runOnFirstFix(new Runnable() {
            public void run() {
                mapView.getController().animateTo(myLocationOverlay.getMyLocation());
            }
        });

//        TextView nearest = (TextView) findViewById(R.id.nearest);
//        nearest.setText("yo");

        // check if GPS enabled
//        GPSTracker gpsTracker = new GPSTracker(this);

//        if (gpsTracker.getIsGPSTrackingEnabled())
//        {
//            String stringLatitude = String.valueOf(gpsTracker.latitude);
//            String stringLongitude = String.valueOf(gpsTracker.longitude);
//             stringLatitude+","+stringLongitude);
//        } else {
//            showSettingsAlert();
//        }

    }

    public void onLocationChanged(Location location, IMyLocationProvider provider) {
        Log.d(tag, "New location: " + location);
        try {
            Routedb.Stop s = rdb.Nearest(location.getLatitude(), location.getLongitude());
            GeoPoint pt = new GeoPoint(s.getLat(), s.getLon());
            nearestOverlay.setItem(pt, "nearest", "nearest");
        } catch (Exception e) {
            Log.d(tag, "nearest failed: " + e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Function to show settings alert dialog
     */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle(R.string.gps_settings);
        alertDialog.setMessage(R.string.gps_settings_must);

        alertDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        //On pressing cancel button
        alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (myLocationOverlay != null) {
            myLocationOverlay.enableMyLocation();
            myLocationOverlay.enableFollowLocation();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (myLocationOverlay != null) {
            myLocationOverlay.disableMyLocation();
            myLocationOverlay.disableFollowLocation();
        }
    }
}
