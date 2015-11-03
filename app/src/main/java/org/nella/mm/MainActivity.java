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
import org.osmdroid.bonuspack.overlays.BasicInfoWindow;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.IMyLocationConsumer;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.osmdroid.bonuspack.overlays.Polyline;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements IMyLocationConsumer {
    public static final String tag = "mm.MainActivity";

    Routedb.Db rdb = null;

    MyLocationNewOverlay myLocationOverlay = null;

    SingleItemOverlay nearestOverlay = null;
    Drawable nearestMark;
    MapView mapView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

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

            // Set initial center and zoom using the bounds of the database.
            Routedb.Box b = rdb.Bounds();
            double latspan = (b.getN() - b.getS());
            double lonspan = (b.getE() - b.getW());
            GeoPoint gp = new GeoPoint(
                    b.getS() + latspan/2,
                    b.getW() + lonspan / 2);
            Log.d(tag, "setCenter " + gp);
            mapView.getController().setCenter(gp);
            // Level 13 is hardcoded here because zoomToSpan didn't work, and 13 covers
            // the kind of zone mass transit users are generally looking for.
            mapView.getController().setZoom(13);
        } catch (Exception e) {
            Log.e(tag, "could not load routedb: " + e);
            rdb = null;
        }

        addRouteOverlays();

        IMyLocationProvider lp;
        //lp = new MockLocationProvider();
        lp = new GpsMyLocationProvider(this);
        SnoopingLocationProvider slp = new SnoopingLocationProvider(lp, this);

        myLocationOverlay = new MyLocationNewOverlay(this, slp, mapView);
        mapView.getOverlays().add(myLocationOverlay);
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.runOnFirstFix(new Runnable() {
            public void run() {
                mapView.getController().animateTo(myLocationOverlay.getMyLocation());
            }
        });
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    // dark purple
    private static final int track_color = 0xFF800080;

    private void addRouteOverlays() {
        if (rdb == null) {
            return;
        }

        String packageName = mapView.getContext().getPackageName();
        int resid = mapView.getContext().getResources().getIdentifier("layout/bonuspack_bubble", null, packageName);

        long n = rdb.Routes();
        for (long i = 0; i < n; i++) {
            try {
                byte[] rfb = rdb.Route(i);
                ByteBuffer bb = ByteBuffer.wrap(rfb);
                Route route = Route.getRootAsRoute(bb);

                ArrayList<GeoPoint> list = new ArrayList<>();

                org.nella.mm.GeoPoint gp = new org.nella.mm.GeoPoint();
                for (int j = 0; j < route.pathLength(); j++) {
                    gp = route.path(gp, j);
                    list.add(new GeoPoint(gp.lat(), gp.lon()));
                }

                Polyline pl = new Polyline(this);
                pl.setColor(track_color);
                pl.setPoints(list);
                BasicInfoWindow biw = new BasicInfoWindow(resid, mapView);
                pl.setInfoWindow(biw);
                pl.setTitle(route.name());
                // Add the paths at the front of the overlay list so that they
                // are lower in the z-order.
                mapView.getOverlays().add(0, pl);
           } catch (Exception e) {
                Log.d(tag, "point decode: " + e + e.getStackTrace());
                break;
            }

        }

    }

    public void onLocationChanged(Location location, IMyLocationProvider provider) {
        if (rdb == null) {
            return;
        }

        try {
            Routedb.Stop s = rdb.Nearest(location.getLatitude(), location.getLongitude());
            Log.d(tag, "New nearest: " + s);
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
