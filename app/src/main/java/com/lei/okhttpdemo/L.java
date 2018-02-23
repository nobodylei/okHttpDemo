package com.lei.okhttpdemo;

import android.util.Log;

/**
 * Created by yanle on 2018/2/23.
 */

public class L {

    private static final String TAG = "tag_okhttp";
    private static boolean debug = true;

    public static void e(String msg) {
        if(debug) {
            Log.e(TAG, msg);
        }
    }
}
