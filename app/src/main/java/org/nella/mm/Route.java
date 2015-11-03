// automatically generated, do not modify

package org.nella.mm;

import java.nio.*;
import java.lang.*;

import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class Route extends Table {
    public static Route getRootAsRoute(ByteBuffer _bb) { return getRootAsRoute(_bb, new Route()); }
    public static Route getRootAsRoute(ByteBuffer _bb, Route obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
    public Route __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

    public String country() { int o = __offset(4); return o != 0 ? __string(o + bb_pos) : null; }
    public ByteBuffer countryAsByteBuffer() { return __vector_as_bytebuffer(4, 1); }
    public String city() { int o = __offset(6); return o != 0 ? __string(o + bb_pos) : null; }
    public ByteBuffer cityAsByteBuffer() { return __vector_as_bytebuffer(6, 1); }
    public String name() { int o = __offset(8); return o != 0 ? __string(o + bb_pos) : null; }
    public ByteBuffer nameAsByteBuffer() { return __vector_as_bytebuffer(8, 1); }
    public GeoPoint path(int j) { return path(new GeoPoint(), j); }
    public GeoPoint path(GeoPoint obj, int j) { int o = __offset(10); return o != 0 ? obj.__init(__vector(o) + j * 8, bb) : null; }
    public int pathLength() { int o = __offset(10); return o != 0 ? __vector_len(o) : 0; }

    public static int createRoute(FlatBufferBuilder builder,
                                  int country,
                                  int city,
                                  int name,
                                  int path) {
        builder.startObject(4);
        Route.addPath(builder, path);
        Route.addName(builder, name);
        Route.addCity(builder, city);
        Route.addCountry(builder, country);
        return Route.endRoute(builder);
    }

    public static void startRoute(FlatBufferBuilder builder) { builder.startObject(4); }
    public static void addCountry(FlatBufferBuilder builder, int countryOffset) { builder.addOffset(0, countryOffset, 0); }
    public static void addCity(FlatBufferBuilder builder, int cityOffset) { builder.addOffset(1, cityOffset, 0); }
    public static void addName(FlatBufferBuilder builder, int nameOffset) { builder.addOffset(2, nameOffset, 0); }
    public static void addPath(FlatBufferBuilder builder, int pathOffset) { builder.addOffset(3, pathOffset, 0); }
    public static void startPathVector(FlatBufferBuilder builder, int numElems) { builder.startVector(8, numElems, 4); }
    public static int endRoute(FlatBufferBuilder builder) {
        int o = builder.endObject();
        return o;
    }
    public static void finishRouteBuffer(FlatBufferBuilder builder, int offset) { builder.finish(offset); }
};

