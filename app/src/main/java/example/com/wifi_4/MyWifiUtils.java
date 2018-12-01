package example.com.wifi_4;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.content.Context.WIFI_SERVICE;

public class MyWifiUtils {


    public static boolean isWifiSSIDConnected(Context context, WifiManager wifiManager ,String ssid)
    {

        //WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo ();

        //String current_connected_SSID  = info.getSSID();
        String current_connected_SSID  = WifiConnectorWidget.getSsidConnectedNow();

        if(!wifiManager.isWifiEnabled())
        {
            WifiConnectorWidget.setSsidConnectedNow("");
            return false;
        }

//            wifiManager.setWifiEnabled(true);
//
//        netId = tmp.networkId;
//        wifiManager.enableNetwork(netId, true);
//        break;



        if (current_connected_SSID.equals( "\""+ ssid +"\""))
            return true;
        else
            return false;
    }




    public static List<String> removeDoubleQuotesFromSSIDs(List<String> originalList)
    {
        MyWifiUtils.logList("DAGGERS: removeDoubleQuotesFromSSIDs: ", originalList);

        List<String> toReturn = new ArrayList<String>();

        for (int i=0; i<= originalList.size() -1 ; i++)
        {
            if (originalList.get(i) != null)
            {
                String ssid_to_add_to_list = originalList.get(i);

                String ssid_without_quotes =  ssid_to_add_to_list.substring(1, ssid_to_add_to_list.length() - 1);

                toReturn.add(ssid_without_quotes);
            }
        }

        return toReturn;
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

        Set<String> removeDuplicates_set = new HashSet<>();
        removeDuplicates_set.addAll(listOfStrings);

        listOfStrings.clear();
        listOfStrings.addAll(removeDuplicates_set);

        Collections.sort(listOfStrings, String.CASE_INSENSITIVE_ORDER);   // sort in alphabetical order. CAPS don't matter

        return listOfStrings;
    }

    public static void logList(String TAG, List<String> list)
    {
        for (String listItem: list)
        {
            Log.d(TAG, listItem);
        }

    }

    public void connectToWifi(Context context, String ssid) {

        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);

        int netId;

        for (WifiConfiguration tmp : wifiManager.getConfiguredNetworks())
            if (tmp.SSID.equals( "\""+ ssid +"\""))
            {
                if(wifiManager.setWifiEnabled(false))
                    wifiManager.setWifiEnabled(true);

                netId = tmp.networkId;
                wifiManager.enableNetwork(netId, true);
                break;
            }

    }
}
