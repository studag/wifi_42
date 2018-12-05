package example.com.wifi_4;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class CheckStateForever extends Service {
    public CheckStateForever() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IntentFilter filters = new IntentFilter();

          filters.addAction("android.net.wifi.STATE_CHANGE");

        try
        {
            getApplicationContext().registerReceiver(receiver , filters);
        }catch(Exception e)
        {
            ///do nothing
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
      //  unregisterReceiver(receiver);
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
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
    };
}
