package example.com.wifi_4;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class CheckStateForeverService extends Service {

    private WifiBroadcastReceiver receiver = null;

    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter filters = new IntentFilter();
        filters.addAction("android.net.wifi.STATE_CHANGE");


        receiver = new WifiBroadcastReceiver();
        registerReceiver(receiver,filters);
        this.registerReceiver(receiver,filters);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartService = new Intent(getApplicationContext(),
                this.getClass());
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService(
                getApplicationContext(), 1, restartService,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);
//        return START_STICKY;
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
      //  unregisterReceiver(receiver);
        super.onDestroy();
        // Unregister screenOnOffReceiver when destroy.
        if(receiver!=null)
        {
            unregisterReceiver(receiver);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {

//        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
//        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(getApplicationContext(), WifiConnectorWidget.class));
//        WifiConnectorWidget.updateAppWidgetFromService(getApplicationContext(),appWidgetManager);

        return super.registerReceiver(receiver, filter);
    }
}
