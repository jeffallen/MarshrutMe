package org.nella.mm;

import android.content.Context;
import android.util.AttributeSet;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.views.MapView;

public class MyMapView extends MapView {
    public BoundingBoxE6 boundingBox;

    /**
     * Constructor used by XML layout resource (uses default tile source).
     */
    public MyMapView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
        super.onLayout(arg0, arg1, arg2, arg3, arg4);

        // Now that we have laid out the map view,
        // zoom to any bounding box
        if (this.boundingBox != null) {
            this.zoomToBoundingBox(this.boundingBox);
            boundingBox = null;
        }
    }
}