package vortex.vp_today.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import vortex.vp_today.util.Util;

/**
 * @author Simon Dräger
 * @version 2.3.18
 */

public class NetworkReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Util.isInternetConnected(context)) {
        }
    }
}