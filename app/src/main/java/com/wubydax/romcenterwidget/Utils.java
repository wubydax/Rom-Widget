package com.wubydax.romcenterwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

/**
 * Created by Anna Berkovitch on 03/02/2016. Duh
 */
public class Utils {
    Context c;
    int widgetId;
    private static final String LOG_TAG = "Utils";


    public Utils(Context context, int widgetId) {
        c = context;
        this.widgetId = widgetId;
    }

    public void setUpRemoteViews() {
        PendingIntent pi;
        RemoteViews rv = new RemoteViews(c.getPackageName(), R.layout.widget_layout);
        PackageManager pm = c.getPackageManager();
        SharedPreferences sp = c.getSharedPreferences("widget_prefs" + String.valueOf(widgetId), Context.MODE_PRIVATE);
        int iconSize = Math.round(c.getResources().getDimension(R.dimen.app_icon_size));
        Intent startConfig = new Intent(c.getApplicationContext(), MyWidgetProvider.class);
        startConfig.setAction(MyWidgetProvider.OPEN_CONFIG_ACTION + String.valueOf(widgetId));
        PendingIntent configPendingIntent = PendingIntent.getBroadcast(c.getApplicationContext(), 0, startConfig, 0);
        rv.setOnClickPendingIntent(R.id.openConfig, configPendingIntent);
        Intent openXda = new Intent(Intent.ACTION_VIEW);
        openXda.setData(Uri.parse(c.getResources().getString(R.string.home_xda_link)));
        PendingIntent openXdaPendingIntent = PendingIntent.getActivity(c, 0, openXda, 0);
        rv.setOnClickPendingIntent(R.id.xdaContainer, openXdaPendingIntent);
        for (int i = 1; i < 6; i++) {
            String appNumberString = String.valueOf(i);
            String packageName = sp.getString("app" + appNumberString, "");
            Intent openAppIntent = pm.getLaunchIntentForPackage(packageName);


            if (openAppIntent == null) {
                pi = PendingIntent.getBroadcast(c.getApplicationContext(), 0, startConfig, 0);
            } else {
                pi = PendingIntent.getActivity(c, 0, openAppIntent, 0);
            }
            int viewId = c.getResources().getIdentifier("appContainer" + appNumberString, "id", c.getPackageName());
            int imageViewId = c.getResources().getIdentifier("appIcon" + appNumberString, "id", c.getPackageName());

            try {
                rv.setImageViewBitmap(imageViewId, getBitmapFromDrawable(pm.getApplicationIcon(packageName), iconSize));
            } catch (PackageManager.NameNotFoundException e) {
                rv.setImageViewResource(imageViewId, R.drawable.add);
            }


            rv.setOnClickPendingIntent(viewId, pi);

        }
        int numShortcuts = Integer.parseInt(sp.getString("widget_size", "4"));
        int currentWeightSum = sp.getInt("weight_sum", 4);
        float newWeightSum = (float) numShortcuts;
        if (currentWeightSum > numShortcuts) {
            for (int i = numShortcuts + 1; i <= currentWeightSum; i++) {

                int containerId = c.getResources().getIdentifier("appContainer" + String.valueOf(i), "id", c.getPackageName());
                Log.d(LOG_TAG, "setUpRemoteViews removing appContainer" + i);
                rv.setInt(containerId, "setVisibility", View.GONE);
            }
        } else if (currentWeightSum < numShortcuts) {
            for (int i = currentWeightSum + 1; i <= numShortcuts; i++) {
                int containerId = c.getResources().getIdentifier("appContainer" + String.valueOf(i), "id", c.getPackageName());
                rv.setInt(containerId, "setVisibility", View.VISIBLE);
                Log.d(LOG_TAG, "setUpRemoteViews adding appContainer" + i);


            }
        }

        rv.setFloat(R.id.shortcutsContainerLayout, "setWeightSum", newWeightSum);
        rv.setInt(R.id.mainContainerLayout, "setBackgroundColor", sp.getInt("bg_color", Color.TRANSPARENT));
        rv.setTextColor(R.id.widgetTitle, sp.getInt("text_color", Color.WHITE));
        sp.edit().putInt("weight_sum", numShortcuts).commit();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(c);
        appWidgetManager.updateAppWidget(widgetId, rv);
    }

    private Bitmap getBitmapFromDrawable(Drawable icon, int size) {
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        icon.setBounds(0, 0, size, size);
        icon.draw(canvas);

        return bitmap;
    }
}
