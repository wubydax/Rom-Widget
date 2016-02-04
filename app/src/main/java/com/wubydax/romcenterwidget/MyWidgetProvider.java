package com.wubydax.romcenterwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.File;

/**
 * Created by Anna Berkovitch on 01/02/2016. duh
 */
public class MyWidgetProvider extends AppWidgetProvider {

    public static final String OPEN_CONFIG_ACTION = "OPEN_CONFIG";
    private static final String LOG_TAG = "WidgetProvider";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        int dpi = context.getResources().getDisplayMetrics().densityDpi;

        Log.d(LOG_TAG, "onUpdate is called and dpi is " + dpi);
        for (int appWidgetId : appWidgetIds) {
            Utils utils = new Utils(context, appWidgetId);
            utils.setUpRemoteViews();

        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();

        if (action.startsWith(OPEN_CONFIG_ACTION)) {
            int widgetId = Integer.parseInt(action.substring(OPEN_CONFIG_ACTION.length()));
            Intent startConfig = new Intent(context, ConfigActivity.class);
            startConfig.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startConfig.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            context.startActivity(startConfig);
        }
    }


    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        for (int appWidgetId : appWidgetIds) {
            deleteOldPrefs(appWidgetId, context);
        }
    }


    private void deleteOldPrefs(int deletedWidgetId, Context context) {
        String fileName = "widget_prefs" + String.valueOf(deletedWidgetId);
        context.getSharedPreferences(fileName, Context.MODE_PRIVATE).edit().clear().commit();
        File file = new File(context.getFilesDir().getParent() + File.separator + "shared_prefs" + File.separator + fileName + ".xml");
        if (file.exists()) {
            file.delete();
        }
    }
}
