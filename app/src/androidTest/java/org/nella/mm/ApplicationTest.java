package org.nella.mm;

import android.app.Application;
import android.test.ApplicationTestCase;

import java.nio.ByteBuffer;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void testDecode() throws Exception {
        byte[] by = new byte[8];
        by[0] = 0;
        by[1] = 1;
        by[2] = 2;
        by[3] = 3;
        by[4] = 4;
        by[5] = 5;
        by[6] = 6;
        by[7] = 7;
        ByteBuffer bb = ByteBuffer.wrap(by);
        long l = bb.getLong();
        assert l == 0x01020304050607L;
    }
}