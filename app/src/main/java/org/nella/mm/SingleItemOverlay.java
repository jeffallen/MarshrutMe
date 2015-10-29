package org.nella.mm;

import android.graphics.Point;
import android.graphics.drawable.Drawable;

import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IMapView;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;

public class SingleItemOverlay extends ItemizedOverlay<OverlayItem> {

    OverlayItem item = null;

    public SingleItemOverlay(Drawable pDefaultMarker,
                             ResourceProxy pResourceProxy) {
        super(pDefaultMarker, pResourceProxy);
    }

    public void setItem(GeoPoint p, String title, String snippet) {
        item = new OverlayItem(title, snippet, p);
        populate();
    }

    @Override
    public boolean onSnapToItem(int arg0, int arg1, Point arg2, IMapView arg3) {
        return false;
    }

    @Override
    protected OverlayItem createItem(int arg0) {
        return item;
    }

    @Override
    public int size() {
        if (item == null) {
            return 0;
        }
        return 1;
    }

}
