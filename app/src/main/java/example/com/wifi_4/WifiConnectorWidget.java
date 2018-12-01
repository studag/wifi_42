package example.com.wifi_4;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.List;

public class WifiConnectorWidget extends AppWidgetProvider {

    private static String ssidConnectedNow = "";

    public static String getSsidConnectedNow() {
        return ssidConnectedNow;
    }

    public static void setSsidConnectedNow(String ss) {
        ssidConnectedNow = ss;
    }


    private static int level = 0;

    private static String WIDGET_CLICKED_ACTION = "wifiaction";

    private static int enabledSSIDWidget = 0;

    static WifiManager wifiManager;
    String _ssid;

    private List<WifiConfiguration> configuredNetworkList;

    private static final String MY_TAG = "WIFI_SERVICE";

    private static Bitmap bmp_color_image_green;
    private static Bitmap bmp_color_image_gray;
    private static Bitmap bmp_color_image_red;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = WifiConnectorWidgetConfigureActivity.loadTitlePref(context, appWidgetId);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.wifi_connector_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        if(MyWifiUtils.isWifiSSIDConnected(context,wifiManager, WifiConnectorWidgetConfigureActivity.loadSSIDPref(context,appWidgetId)))
        {
            bmp_color_image_green = BitmapFactory.decodeResource(context.getResources(), R.drawable.green_on);
            bmp_color_image_gray = BitmapFactory.decodeResource(context.getResources(), R.drawable.gray_circle);
            bmp_color_image_red = BitmapFactory.decodeResource(context.getResources(), R.drawable.red_off);

            switch (level) {
                case 3:
                    views.setImageViewBitmap(R.id.circle_on_off_image_Level_1,bmp_color_image_green);
                    views.setImageViewBitmap(R.id.circle_on_off_image_Level_2,bmp_color_image_green);
                    views.setImageViewBitmap(R.id.circle_on_off_image_Level_3,bmp_color_image_green);
                    break;
                case 2:
                    views.setImageViewBitmap(R.id.circle_on_off_image_Level_1,bmp_color_image_green);
                    views.setImageViewBitmap(R.id.circle_on_off_image_Level_2,bmp_color_image_green);
                    views.setImageViewBitmap(R.id.circle_on_off_image_Level_3,bmp_color_image_gray);
                    break;
                case 1:
                    views.setImageViewBitmap(R.id.circle_on_off_image_Level_1,bmp_color_image_green);
                    views.setImageViewBitmap(R.id.circle_on_off_image_Level_2,bmp_color_image_gray);
                    views.setImageViewBitmap(R.id.circle_on_off_image_Level_3,bmp_color_image_gray);
                    break;
                case 0:
                    views.setImageViewBitmap(R.id.circle_on_off_image_Level_1,bmp_color_image_gray);
                    views.setImageViewBitmap(R.id.circle_on_off_image_Level_2,bmp_color_image_red);
                    views.setImageViewBitmap(R.id.circle_on_off_image_Level_3,bmp_color_image_gray);
                    break;
            }
        }
        else{
            views.setImageViewBitmap(R.id.circle_on_off_image_Level_1,null);
            views.setImageViewBitmap(R.id.circle_on_off_image_Level_2,null);
            views.setImageViewBitmap(R.id.circle_on_off_image_Level_3,null);
        }

        int color = WifiConnectorWidgetConfigureActivity.loadColorPref(context);//todo. chnage this for individual appIds. currently just loads all one color. Have separate DEFAULT color preference

        views.setInt(R.id.appwidget_main_body, "setBackgroundColor", color);
        views.setInt(R.id.appwidget_text, "setBackgroundColor", color);

        Intent intent = new Intent(context, WifiConnectorWidget.class);
//        Intent intent = new Intent(context,ConnectWifiService.class);

        //intent.putExtra("ssid", widgetText.toString());
        intent.putExtra("appWidgetID", appWidgetId);

        Log.d("TAGGGGGGGG", widgetText.toString());
        intent.setAction(WIDGET_CLICKED_ACTION);

//        PendingIntent pendingIntent = PendingIntent.getService(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, appWidgetId, intent, 0);

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
            Log.d("TAGGGGGGGG", "Updating appWidgetId" + appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            WifiConnectorWidgetConfigureActivity.deletePrefs(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created

        IntentFilter filters = new IntentFilter();
        filters.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filters.addAction("android.net.wifi.STATE_CHANGE");
        filters.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
        filters.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filters.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filters.addAction(WifiManager.EXTRA_WIFI_STATE);

        context.getApplicationContext().registerReceiver(wifiStateReceiver, filters);
        Toast.makeText(context, "Receiver Registered", Toast.LENGTH_SHORT).show();

        WifiConnectorWidgetConfigureActivity.saveWidgetColorPref(context,WifiConnectorWidgetConfigureActivity.getDefaultWidgetColor());
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        try {
            context.getApplicationContext().unregisterReceiver(wifiStateReceiver);
        }catch(IllegalArgumentException e)
        {
            //do nothing
        }
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        final String action = intent.getAction();

        if (action.equals(WIDGET_CLICKED_ACTION)) {

            int appId = intent.getIntExtra("appWidgetID", 0);

            _ssid = WifiConnectorWidgetConfigureActivity.loadSSIDPref(context, appId);

            Log.d(MY_TAG, "SSID to connect to: " + _ssid);

            wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            wifiManager.disableNetwork(wifiManager.getConnectionInfo().getNetworkId());

            configuredNetworkList = wifiManager.getConfiguredNetworks();
            int netId;

            for (WifiConfiguration tmp : wifiManager.getConfiguredNetworks())
                if (tmp.SSID.equals("\"" + _ssid + "\"")) {

                    netId = tmp.networkId;

                    if (wifiManager.setWifiEnabled(false))
                    {
                        wifiManager.setWifiEnabled(true);
                    }


                    wifiManager.enableNetwork(netId, true);

                    Toast.makeText(context, _ssid + "\nappID=" + appId, Toast.LENGTH_SHORT).show();

                    break;
                }

        }

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(context, WifiConnectorWidget.class));
        onUpdate(context, appWidgetManager, appWidgetIds);

}

    private BroadcastReceiver wifiStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int wifiStateExtra = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
            final String action = intent.getAction();

            wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(context, WifiConnectorWidget.class));
            setSsidConnectedNow(wifiManager.getConnectionInfo().getSSID());

            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 4);

            if (action.equals("android.net.wifi.STATE_CHANGE")) {
                // save the connected state to get in onUpdate
                onUpdate(context, appWidgetManager, appWidgetIds);

            }

            switch (wifiStateExtra) {

                case WifiManager.WIFI_STATE_ENABLED:
                    Toast.makeText(context, "Wifi Enabled", Toast.LENGTH_SHORT).show();
                    onUpdate(context, appWidgetManager, appWidgetIds);
                    break;

                case WifiManager.WIFI_STATE_DISABLED:
                    Toast.makeText(context, "Wifi disabled", Toast.LENGTH_SHORT).show();
                    setSsidConnectedNow(wifiManager.getConnectionInfo().getSSID());
                    onUpdate(context, appWidgetManager, appWidgetIds);
                    break;
            }
        }
    };



}

