package example.com.wifi_4;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class WifiConnectorWidget extends AppWidgetProvider {

    private static final String PREFS_NAME = "example.com.wifi_4.WifiConnectorWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = WifiConnectorWidgetConfigureActivity.loadTitlePref(context, appWidgetId);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.wifi_connector_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        Intent intent = new Intent(context,ConnectWifiService.class);

        //intent.putExtra("ssid", widgetText.toString());
        intent.putExtra("appWidgetID", appWidgetId);


        Log.d("TAGGGGGGGG", widgetText.toString());
        intent.setAction("dummy");

        PendingIntent pendingIntent = PendingIntent.getService(context, appWidgetId, intent, PendingIntent.FLAG_CANCEL_CURRENT);


        //views.setOnClickPendingIntent(R.id.appwidget_button, pendingIntent);
        //views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);
        views.setOnClickPendingIntent(R.id.appwidget_main_body, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them.
        for (int appWidgetId : appWidgetIds) {

            updateAppWidget(context, appWidgetManager, appWidgetId);
            Log.d("TAGGGGGGGG", "Updating appWidgetId" + appWidgetId );
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            WifiConnectorWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }


}

