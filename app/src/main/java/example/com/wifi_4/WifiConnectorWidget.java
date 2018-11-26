package example.com.wifi_4;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class WifiConnectorWidget extends AppWidgetProvider {

    private static String WIDGET_CLICKED_ACTION = "wifiaction";

    String _ssid;

    private WifiManager wifiManager;
    private List<WifiConfiguration> configuredNetworkList;

    private static final String MY_TAG = "WIFI_SERVICE";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = WifiConnectorWidgetConfigureActivity.loadTitlePref(context, appWidgetId);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.wifi_connector_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);


        int color = WifiConnectorWidgetConfigureActivity.loadColorPref(context);

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

        context.getApplicationContext().registerReceiver(wifiStateReceiver, filters);
        Toast.makeText(context, "Receiver Registered", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        //context.getApplicationContext().unregisterReceiver(wifiStateReceiver);
        //Toast.makeText(context, "Receiver Un-Registered", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        final String action = intent.getAction();

        int wifiStateExtra = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                WifiManager.WIFI_STATE_UNKNOWN);


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

                    if (wifiManager.setWifiEnabled(false))
                        wifiManager.setWifiEnabled(true);

                    netId = tmp.networkId;
                    wifiManager.enableNetwork(netId, true);
                    Toast.makeText(context, _ssid + "\nappID=" + appId, Toast.LENGTH_SHORT).show();
                    break;
                }

        }


        if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
            // save the connected state to get in onUpdate

          //  AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            Toast.makeText(context, "Change State of Wifi", Toast.LENGTH_SHORT).show();

        }




}


    private List<String> getSortedFilteredConfiguredNetworksSSIDs(List<WifiConfiguration> wifiConfiguredNetworkList)
    {


        ArrayList<String> unsorted_SSIDs = new ArrayList<String>();
        List <String> sNetworks_filtered;

        for (int i=0; i<= wifiConfiguredNetworkList.size() -1 ; i++) {

            if (wifiConfiguredNetworkList.get(i).SSID != null) {
                String ssid_to_add_to_list = wifiConfiguredNetworkList.get(i).SSID;
                unsorted_SSIDs.add(ssid_to_add_to_list);
            }
        }

        sNetworks_filtered = MyWifiUtils.sortByAlphaAndRemoveBlanksAndDuplicates(unsorted_SSIDs);

        List<String> ssidListFilteredSortedCut;
        ssidListFilteredSortedCut = MyWifiUtils.removeDoubleQuotesFromSSIDs(sNetworks_filtered);

        return ssidListFilteredSortedCut;

    }
    private BroadcastReceiver wifiStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int wifiStateExtra = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);

            if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION))
            {
               // Toast.makeText(context, "NETWORK_STATE_CHANGED_ACTION", Toast.LENGTH_SHORT).show();
            }
            else if (intent.getAction().equals(WifiManager.NETWORK_IDS_CHANGED_ACTION))
            {
                Toast.makeText(context, "NETWORK_IDS_CHANGED_ACTION", Toast.LENGTH_SHORT).show();
            }

            final String action = intent.getAction();
            if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
                Toast.makeText(context, "SUPPLICANT_CONNECTION_CHANGE_ACTION", Toast.LENGTH_SHORT).show();
            }


            switch (wifiStateExtra) {
                case WifiManager.WIFI_STATE_ENABLED:
                    Toast.makeText(context, "Wifi Enabled", Toast.LENGTH_SHORT).show();

                    break;
                case WifiManager.WIFI_STATE_DISABLED:
                    Toast.makeText(context, "Wifi disabled", Toast.LENGTH_SHORT).show();

                    break;
            }




        }

    };

}

