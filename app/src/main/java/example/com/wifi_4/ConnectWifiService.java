package example.com.wifi_4;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ConnectWifiService extends Service {

    String _ssid;

    public ConnectWifiService() {
        Log.d("SERVICE SSS", "Here now bitch");

    }

    public int onStartCommand(Intent intent, int flags, int startId)
    {
        _ssid = intent.getStringExtra("ssid");

        connectToWifi(_ssid);

        return START_STICKY;

    }
    public void connectToWifi(String ssid)
    {
        Log.d("CONNECT_WIFI", ssid);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

        throw new UnsupportedOperationException("Not yet implemented");
    }
}
