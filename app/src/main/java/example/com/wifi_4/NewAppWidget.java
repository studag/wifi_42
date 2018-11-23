package example.com.wifi_4;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class NewAppWidget extends AppWidgetProvider {

    private void updateAppWidget(Context context,
                                 AppWidgetManager appWidgetManager,
                                 int appWidgetId) {
//
//        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
//
//        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://erenutku.com")); // working
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://erenutku.com")); // working
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
//
//        views.setOnClickPendingIntent(R.id.appwidget_button, pendingIntent);

       // appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them.
        for (int appWidgetId : appWidgetIds) {


            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);

            //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://erenutku.com")); // working
            //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://erenutku.com")); // working
            Intent intent = new Intent(context,ConnectWifiService.class);
            //intent.putExtra("ssid", "WifiMcWifiFace");
            intent.putExtra("ssid", "WifiMcWifiFace_forService");

            //intent.putExtra("SSID", "WifiMcWifiFace");
            //intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));



            //PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

            views.setOnClickPendingIntent(R.id.appwidget_button, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
            //updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
}