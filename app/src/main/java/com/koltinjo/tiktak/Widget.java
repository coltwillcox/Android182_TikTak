package com.koltinjo.tiktak;

import android.app.ActivityManager;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

/**
 * Created by colt on 15.12.2016.
 */

// https://www.youtube.com/watch?v=g6r1TGIwIig
public class Widget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        // Widget is using updatePeriodMillis = 21600000 millis (6 hours) to recalibrate counters.
        // Executors are set to 60 secs and 60 mins + few millis added for calculations.
        // Those few millis can make displays to look laggy (about 1-2 cumulative seconds per 6 hours).
        if (isServiceRunning(context, ServiceUpdate.class)) {
            context.stopService(new Intent(context, ServiceUpdate.class));
        }
        context.startService(new Intent(context, ServiceUpdate.class));
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        if (!isServiceRunning(context, ServiceUpdate.class)) {
            context.startService(new Intent(context, ServiceUpdate.class));
        }
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        context.stopService(new Intent(context, ServiceUpdate.class));
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        context.stopService(new Intent(context, ServiceUpdate.class));
    }

    private boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}