package com.wubydax.romcenterwidget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class ConfigActivity extends AppCompatActivity {
    private int widgetId;
    private int bgColor, textColor, weightSum;
    private String widgetSize;
    private String[] packageNames;
    private SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            widgetId = extra.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
                setResult(RESULT_CANCELED);
                finish();
            }

        }
        if (savedInstanceState == null) {
            PrefsFragment prefsFragment = new PrefsFragment();
            Bundle args = new Bundle();
            args.putInt(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            prefsFragment.setArguments(args);
            getFragmentManager().beginTransaction().add(R.id.container, prefsFragment).commit();
        }
        sp = getSharedPreferences("widget_prefs" + String.valueOf(widgetId), Context.MODE_PRIVATE);
        packageNames = new String[5];
        for (int i = 0; i < 5; i++) {
            packageNames[i] = sp.getString("app" + String.valueOf(i + 1), "");
        }
        bgColor = sp.getInt("bg_color", Color.TRANSPARENT);
        textColor = sp.getInt("text_color", Color.WHITE);
        widgetSize = sp.getString("widget_size", "4");
        weightSum = sp.getInt("weight_sum", 4);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.config_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_save:
                Utils utils = new Utils(this, widgetId);
                utils.setUpRemoteViews();
                Intent i = new Intent();
                i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
                setResult(RESULT_OK, i);
                finish();
                break;
            case R.id.menu_cancel:
                restorePrefs();
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void restorePrefs() {
        SharedPreferences.Editor ed = sp.edit();
        for (int i = 0; i < packageNames.length; i++) {
            ed.putString("app" + String.valueOf(i + 1), packageNames[i]).commit();
        }
        ed.putString("widget_size", widgetSize);
        ed.putInt("bg_color", bgColor);
        ed.putInt("text_color", textColor);
        ed.putInt("weight_sum", weightSum);

    }

    @Override
    public void onBackPressed() {
        restorePrefs();
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}
