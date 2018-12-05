package example.com.wifi_4;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    private static List<String> availableSSIDList;

    private static final String MY_TAG = "WIFI_SERVICE";

    private static boolean serviceRunning = false;

    private static Bitmap bmp_color_image_green;
    private static Bitmap bmp_color_image_gray;
    private static Bitmap bmp_color_image_red;
    private static Bitmap bmp_color_image_weak_signal_green;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = WifiConnectorWidgetConfigureActivity.loadTitlePref(context, appWidgetId);
        String widgetSSID = WifiConnectorWidgetConfigureActivity.loadSSIDPref(context, appWidgetId);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.wifi_connector_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        bmp_color_image_green = BitmapFactory.decodeResource(context.getResources(), R.drawable.green_on);
        bmp_color_image_gray = BitmapFactory.decodeResource(context.getResources(), R.drawable.gray_circle);
        bmp_color_image_red = BitmapFactory.decodeResource(context.getResources(), R.drawable.red_off);
        bmp_color_image_weak_signal_green = BitmapFactory.decodeResource(context.getResources(), R.drawable.weak_signal);

        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        if (wifiManager.isWifiEnabled())// If wifi is on, we can do wifi getters
        {
            WifiInfo info = wifiManager.getConnectionInfo ();

            availableSSIDList = getAvailableSSIDList(wifiManager);

            String current_connected_SSID ;
            current_connected_SSID = info.getSSID();

            boolean result;

            result = current_connected_SSID.equals("\"" + widgetSSID + "\"");

            views.setImageViewBitmap(R.id.circle_on_off_image_Level_1, bmp_color_image_gray);

            // If Connected do one thing...
            if(result)
            {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 4);

                switch (level) {
                    case 3:
                        views.setImageViewBitmap(R.id.circle_on_off_image_Level_1,bmp_color_image_green);
                        break;
                    case 2:
                        views.setImageViewBitmap(R.id.circle_on_off_image_Level_1,bmp_color_image_weak_signal_green);
                        break;
                    case 1:
                        views.setImageViewBitmap(R.id.circle_on_off_image_Level_1,bmp_color_image_weak_signal_green);
                        break;
                    case 0:
                        // views.setImageViewBitmap(R.id.circle_on_off_image_Level_1,bmp_color_image_red);
                        break;
                }
            }

        }
        else
        {
            views.setImageViewBitmap(R.id.circle_on_off_image_Level_1, bmp_color_image_red);
        }

        int color = WifiConnectorWidgetConfigureActivity.loadColorPref(context,appWidgetId);//todo. chnage this for individual appIds. currently just loads all one color. Have separate DEFAULT color preference

        views.setInt(R.id.appwidget_text, "setBackgroundColor", Color.TRANSPARENT);
        views.setInt(R.id.appwidget_text, "setTextColor", color);

        Intent intent = new Intent(context, WifiConnectorWidget.class);

        intent.putExtra("appWidgetID", appWidgetId);

        Log.d("TAGGGGGGGG", widgetText.toString());
        intent.setAction(WIDGET_CLICKED_ACTION);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, appWidgetId, intent, 0);


        views.setOnClickPendingIntent(R.id.appwidget_main_body, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }



    public static void updateAppWidgetFromService(Context context, AppWidgetManager appWidgetManager) {

        appWidgetManager = AppWidgetManager.getInstance(context);
        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(context, WifiConnectorWidget.class));

        for (int appWidgetId : appWidgetIds) {

            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
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

        Intent serviceIntent = new Intent(context, CheckStateForever.class);

        if(serviceRunning) {
            context.stopService(serviceIntent);
            Toast.makeText(context, "serviceStopped", Toast.LENGTH_SHORT).show();

        } else {
            context.startService(serviceIntent);
            Toast.makeText(context, "service Started", Toast.LENGTH_SHORT).show();

        }
//        IntentFilter filters = new IntentFilter();
//        filters.addAction("android.net.wifi.WIFI_STATE_CHANGED");
//        filters.addAction("android.net.wifi.STATE_CHANGE");
//        filters.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
//        filters.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
//        filters.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
//        filters.addAction(WifiManager.EXTRA_WIFI_STATE);
//
//        try
//        {
//            context.getApplicationContext().registerReceiver(wifiStateReceiver, filters);
//        }catch(Exception e)
//        {
//            Toast.makeText(context, "Receiver could not be started", Toast.LENGTH_SHORT).show();
//        }

        //Toast.makeText(context, "Receiver Registered", Toast.LENGTH_SHORT).show();

        //WifiConnectorWidgetConfigureActivity.saveWidgetColorPref(context,WifiConnectorWidgetConfigureActivity.getDefaultWidgetColor());
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled

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
            //wifiManager.disableNetwork(wifiManager.getConnectionInfo().getNetworkId());

            int netId;

            if (!wifiManager.isWifiEnabled())
                wifiManager.setWifiEnabled(true);

            for (WifiConfiguration tmp : wifiManager.getConfiguredNetworks())
                if (tmp.SSID.equals("\"" + _ssid + "\"")) {

                    netId = tmp.networkId;

                    if(wifiManager.enableNetwork(netId, true)) {
                        Toast.makeText(context, "Connecting to:\n" + _ssid, Toast.LENGTH_SHORT).show();

                        break;
                    }
                }

        }

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(context, WifiConnectorWidget.class));
        onUpdate(context, appWidgetManager, appWidgetIds);

}

//    private BroadcastReceiver wifiStateReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            int wifiStateExtra = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
//            final String action = intent.getAction();
//
//            wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//
//            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
//            int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(context, WifiConnectorWidget.class));
//            setSsidConnectedNow(wifiManager.getConnectionInfo().getSSID());
//
//            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//            level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 4);
//
//
//            switch (wifiStateExtra) {
//
//                case WifiManager.WIFI_STATE_ENABLED:
//                    Toast.makeText(context, "Wifi Enabled", Toast.LENGTH_SHORT).show();
//                    onUpdate(context, appWidgetManager, appWidgetIds);
//                    break;
//
//                case WifiManager.WIFI_STATE_DISABLED:
//                    Toast.makeText(context, "Wifi disabled", Toast.LENGTH_SHORT).show();
//                    onUpdate(context, appWidgetManager, appWidgetIds);
//                    break;
//            }
//            //            if (action.equals("android.net.wifi.STATE_CHANGE")) {
//            if (action.equals("android.net.wifi.STATE_CHANGE")) {
//                // save the connected state to get in onUpdate
//                onUpdate(context, appWidgetManager, appWidgetIds);
//
//            }
//        }
//    };

    public static List<String> getAvailableSSIDList(WifiManager wifiManager)
    {
        List <ScanResult> availableNetworkList = wifiManager.getScanResults();

        List <String> sAvail_unfiltered = new ArrayList();
        List <String> sAvail_filtered;

        for(ScanResult list_item: availableNetworkList)
        {
            sAvail_unfiltered.add(list_item.SSID);
        }

        sAvail_filtered = sortByAlphaAndRemoveBlanksAndDuplicates(sAvail_unfiltered);

        return sAvail_filtered;
    }

    public static boolean isWifiSSIDConnected(Context context, WifiManager wifiManager ,String ssid)
    {
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo ();


        String current_connected_SSID  = WifiConnectorWidget.getSsidConnectedNow();
        current_connected_SSID = info.getSSID();


        if(!wifiManager.isWifiEnabled()) {
            WifiConnectorWidget.setSsidConnectedNow("");
            return false;
        }

        return current_connected_SSID.equals("\"" + ssid + "\"");
    }

    public static List<String> sortByAlphaAndRemoveBlanksAndDuplicates(List<String> listOfStrings)
    {
        listOfStrings.removeAll(Arrays.asList("")); // Remove blanks

        for (int i=0; i <= listOfStrings.size() -1; i++)
        {
            if (!listOfStrings.get(i).matches(".*\\w.*")) // Remove whitespace only entries
            {
                listOfStrings.remove(i);
            }
        }

        Set<String> removeDuplicates_set = new HashSet<>(listOfStrings);

        listOfStrings.clear();
        listOfStrings.addAll(removeDuplicates_set);

        Collections.sort(listOfStrings, String.CASE_INSENSITIVE_ORDER);   // sort in alphabetical order. CAPS don't matter

        return listOfStrings;
    }


}

