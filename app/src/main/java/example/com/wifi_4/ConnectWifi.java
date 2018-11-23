package example.com.wifi_4;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class ConnectWifi extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_connect_wifi);

        super.onCreate(savedInstanceState);

        String ssid;

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            ssid= "NULLLLL";
        } else {
            ssid= extras.getString("ssid");
        }

        connectToWifi(ssid);
    }

    public void connectToWifi(String ssid)
    {
        Log.d("CONNECT_WIFI", ssid);
    }
}
