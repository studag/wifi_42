package example.com.wifi_4;


import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class WifiBroadcastReceiver  extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals("android.net.wifi.STATE_CHANGE")) {
            // save the connected state to get in onUpdate
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(context, WifiConnectorWidget.class));
            WifiConnectorWidget.updateAppWidgetFromService(context,appWidgetManager);
            //onUpdate(context, appWidgetManager, appWidgetIds);


        }
    }

}