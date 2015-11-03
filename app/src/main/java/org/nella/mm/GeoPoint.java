// automatically generated, do not modify

package org.nella.mm;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class GeoPoint extends Struct {
  public GeoPoint __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public int lat() { return bb.getInt(bb_pos + 0); }
  public int lon() { return bb.getInt(bb_pos + 4); }

  public static int createGeoPoint(FlatBufferBuilder builder, int lat, int lon) {
    builder.prep(4, 8);
    builder.putInt(lon);
    builder.putInt(lat);
    return builder.offset();
  }
};

