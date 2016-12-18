package com.koltinjo.tiktaktiktak;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by colt on 16.12.2016.
 */

public class ServiceUpdate extends Service {

    private ScheduledExecutorService executorService;
    private Calendar calendar;

    @Override
    public void onCreate() {
        super.onCreate();

        calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        // Initial update.
        updateAll();

        // Calculate initial seconds delay.
        int secondCurrent = calendar.get(Calendar.SECOND);
        int delayTime = 60 - secondCurrent;

        // Calculate initial minutes delay.
        int minuteCurrent = calendar.get(Calendar.MINUTE);
        int delayDate = 60 - minuteCurrent;

        executorService = Executors.newScheduledThreadPool(1);
        // Scheduler to update time. Don't run to often.
        executorService.scheduleWithFixedDelay(
                new Runnable() {
                    @Override
                    public void run() {
                        updateTime();
                    }
                },
                delayTime,
                60,
                TimeUnit.SECONDS
        );
        // Scheduler to udpdate date, image and quote.
        executorService.scheduleWithFixedDelay(
                new Runnable() {
                    @Override
                    public void run() {
                        updateDate();
                    }
                },
                delayDate,
                60,
                TimeUnit.MINUTES
        );
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d("oiram", "onConfigurationChanged");
        updateAll();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executorService.shutdownNow();
        executorService = null;
    }

    private void updateTime() {
        RemoteViews remoteViews = buildTimeUpdate();
        ComponentName widget = new ComponentName(this, Widget.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        appWidgetManager.updateAppWidget(widget, remoteViews);
    }

    private RemoteViews buildTimeUpdate() {
        // Widget views.
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.widget);

        // Clock data.
        Date date = new Date();
        calendar.setTime(date);

        // Hours. Add "0" if hours are < 10.
        String hour = (calendar.get(Calendar.HOUR_OF_DAY) < 10 ? "0" : "") + calendar.get(Calendar.HOUR_OF_DAY);
        remoteViews.setTextViewText(R.id.textview_hours, hour);

        // Minutes. Add "0" if minutes are < 10.
        String minute = (calendar.get(Calendar.MINUTE) < 10 ? "0" : "") + calendar.get(Calendar.MINUTE);
        remoteViews.setTextViewText(R.id.textview_minutes, minute);

        return remoteViews;
    }

    private void updateDate() {
        RemoteViews remoteViews = buildDateUpdate();
        ComponentName widget = new ComponentName(this, Widget.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        appWidgetManager.updateAppWidget(widget, remoteViews);
    }

    private RemoteViews buildDateUpdate() {
        // Widget views.
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.widget);

        // Clock data.
        Date date = new Date();
        calendar.setTime(date);

        // Date.
        String dateFormated = SimpleDateFormat.getDateInstance().format(date);
        remoteViews.setTextViewText(R.id.textview_date, dateFormated);

        // Image
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour >= 6 && hour < 18) {
            remoteViews.setImageViewResource(R.id.imageview, R.drawable.background_day);
        } else {
            remoteViews.setImageViewResource(R.id.imageview, R.drawable.background_night);
        }

        // Quote.
        String[] quotes = getResources().getStringArray(R.array.quotes);
        Random random = new Random();
        String quote = quotes[random.nextInt(quotes.length)];
        remoteViews.setTextViewText(R.id.textview_quote, quote);

        return remoteViews;
    }

    private void updateAll() {
        RemoteViews remoteViews = buildAllUpdate();
        ComponentName widget = new ComponentName(this, Widget.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        appWidgetManager.updateAppWidget(widget, remoteViews);
    }

    private RemoteViews buildAllUpdate() {
        // Widget views.
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.widget);

        // Clock data.
        Date date = new Date();
        calendar.setTime(date);

        // Hours. Add "0" if hours are < 10.
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        String hourString = (hour < 10 ? "0" : "") + hour;
        remoteViews.setTextViewText(R.id.textview_hours, hourString);

        // Minutes. Add "0" if minutes are < 10.
        String minute = (calendar.get(Calendar.MINUTE) < 10 ? "0" : "") + calendar.get(Calendar.MINUTE);
        remoteViews.setTextViewText(R.id.textview_minutes, minute);

        // Date.
        String dateFormated = SimpleDateFormat.getDateInstance().format(date);
        remoteViews.setTextViewText(R.id.textview_date, dateFormated);

        // Image
        if (hour >= 6 && hour < 18) {
            remoteViews.setImageViewResource(R.id.imageview, R.drawable.background_day);
        } else {
            remoteViews.setImageViewResource(R.id.imageview, R.drawable.background_night);
        }

        // Quote.
        String[] quotes = getResources().getStringArray(R.array.quotes);
        Random random = new Random();
        String quote = quotes[random.nextInt(quotes.length)];
        remoteViews.setTextViewText(R.id.textview_quote, quote);

        return remoteViews;
    }

}