package example.com.wifi_4;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ConnectWifiService extends Service {

    String _ssid;

    private WifiManager wifiManager;
    private List<WifiConfiguration> configuredNetworkList;

    private static final String MY_TAG = "WIFI_SERVICE";
    public ConnectWifiService() {

    }


    public int onStartCommand(Intent intent, int flags, int startId)
    {
        _ssid = intent.getStringExtra("ssid");

        connectToWifi(_ssid);

        return START_STICKY;

    }
    public void connectToWifi(String ssid)
    {
        Log.d(MY_TAG, "SSID to connect to: " + ssid);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        configuredNetworkList =  wifiManager.getConfiguredNetworks();
        int netId;

        for (WifiConfiguration tmp : wifiManager.getConfiguredNetworks())
            if (tmp.SSID.equals( "\""+ ssid +"\""))
            {
                Log.d(MY_TAG, "Found Match: " + tmp.SSID);
                if(wifiManager.setWifiEnabled(false))
                    wifiManager.setWifiEnabled(true);

                netId = tmp.networkId;
                wifiManager.enableNetwork(netId, true);
                break;
            }

        List<String> ssidList = getSortedFilteredConfiguredNetworksSSIDs(configuredNetworkList);

        //MyWifiUtils.logList(MY_TAG, ssidList);


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


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

        throw new UnsupportedOperationException("Not yet implemented");
    }


}
