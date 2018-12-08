package example.com.wifi_4;

import android.app.Service;
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
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
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

}
