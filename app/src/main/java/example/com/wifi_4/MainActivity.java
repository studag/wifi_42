package example.com.wifi_4;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Switch wifiSwitch;
    private WifiManager wifiManager;
    private Button connectButton;
    private Button widgetCreateButton;
    private RadioGroup predefinedRadioGroup;
    //private RadioGroup availableRadioGroup;
    private List <WifiConfiguration> configuredNetworkList;
    private List <ScanResult> availableNetworkList;
    private static final String TAG_WIFI = "WIFI DEBUG";


    //private static HashMap<Integer, WidgetWifiInformation> hmap = new HashMap<Integer, WidgetWifiInformation>();;

    //public static HashMap<Integer, WidgetWifiInformation> getHmap() {
//        return hmap;
//    }

    //public static void setHmap(HashMap<Integer, WidgetWifiInformation> hmap) {
    //    MainActivity.hmap = hmap;
    //}



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wifiSwitch = findViewById(R.id.wifi_switch);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        connectButton = findViewById(R.id.button_to_connect);
        connectButton.setEnabled(false);

        widgetCreateButton = findViewById(R.id.button_to_make_widget);
        widgetCreateButton.setEnabled(true);

        final int REQUEST_PICK_APPWIDGET = 9;

        //availableRadioGroup =  findViewById(R.id.availableRadioGroup);

       // populateAvailableNetworksList();
        availableNetworkList = wifiManager.getScanResults();

        populateConfiguredNetworksList();

        widgetCreateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "To create a widget, please exit the app and press and hold on the main screen. Then click the widget button and select OneClick Wifi", Toast.LENGTH_LONG).show();


                //startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
                Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);

                pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

                String RESULT = "";

                AppWidgetProviderInfo ap;

                startActivityForResult(pickIntent,REQUEST_PICK_APPWIDGET);

                Toast.makeText(MainActivity.this, "Does nothing yet = " + RESULT, Toast.LENGTH_LONG).show();
                //
                //
                //todo Add functionality onclick listener for Create widget button, to actually launch a WifiCongigWidget activity and creata a widget
                //
                //

            }


        });




        connectButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                int radioitemselected = predefinedRadioGroup.getCheckedRadioButtonId();

                if (predefinedRadioGroup.getCheckedRadioButtonId() == -1)
                {
                    Toast.makeText(MainActivity.this, "Select Wi-Fi", Toast.LENGTH_SHORT).show();
                }
                else {

                    RadioButton rb = predefinedRadioGroup.findViewById(radioitemselected);

                    String rb_ssid = (String) rb.getText();

                    Log.d(TAG_WIFI, "Radio Button SSID before = " + rb_ssid);
                    String rb_ssid_cut = rb_ssid.substring(1, rb_ssid.length() - 1);
                    Log.d(TAG_WIFI, "Radio Button SSID after = " + rb_ssid_cut);

                    connectToWifi(rb_ssid);
                }
            }

            private void connectToWifi(String ssid) {

                WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
                int netId = -1;


                for (WifiConfiguration tmp : wifiManager.getConfiguredNetworks())
                    if (tmp.SSID.equals( "\""+ ssid +"\""))
                    {
                        Toast.makeText(MainActivity.this, "Connecting..." + ssid, Toast.LENGTH_SHORT).show();
                        if(wifiManager.setWifiEnabled(false))
                            wifiManager.setWifiEnabled(true);

                        netId = tmp.networkId;
                        wifiManager.enableNetwork(netId, true);
                        break;
                    }
            }

        });

        wifiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    wifiManager.setWifiEnabled(true);
                    wifiSwitch.setText("WiFi is ON");
                } else {
                    wifiManager.setWifiEnabled(false);
                    wifiSwitch.setText("WiFi is OFF");
                }
            }
        });

        predefinedRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if(group.getCheckedRadioButtonId() == -1)
                {
                    connectButton.setEnabled(false);
                }
                if(checkedId == -1 ){
                    connectButton.setEnabled(false);
                }
                else
                    connectButton.setEnabled(true);

            }
        });

//        availableRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
//        {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//
//                //startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
//                startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
//
//
//                // checkedId is the RadioButton selected
//            }
//        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(wifiStateReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(wifiStateReceiver);
    }

    private BroadcastReceiver wifiStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int wifiStateExtra = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);

            switch (wifiStateExtra) {
                case WifiManager.WIFI_STATE_ENABLED:
                    wifiSwitch.setChecked(true);
                    wifiSwitch.setText("WiFi is ON");

                    //populateAvailableNetworksList();

                    if (predefinedRadioGroup.getCheckedRadioButtonId() == -1)
                    {
                        connectButton.setEnabled(false);
                    }
                    else
                        connectButton.setEnabled(true);
                    break;
                case WifiManager.WIFI_STATE_DISABLED:

//                    for (int i = 0; i < availableRadioGroup.getChildCount(); i++) {
//                        availableRadioGroup.getChildAt(i).setEnabled(false);
//                    }

                    //availableRadioGroup.clearCheck(); // causes onchanges receiver broadcast
                    availableNetworkList.clear();

                    wifiSwitch.setChecked(false);
                    wifiSwitch.setText("WiFi is OFF");
                    //connectButton.setEnabled(false);
                    break;
            }
        }
    };
    private void populateConfiguredNetworksList()
    {

        configuredNetworkList =  wifiManager.getConfiguredNetworks();

        ArrayList<String> unsorted_SSIDs = new ArrayList<String>();

        for (int i=0; i<= configuredNetworkList.size() -1 ; i++) {

            if (configuredNetworkList.get(i).SSID != null) {
                String ssid_to_add_to_list = configuredNetworkList.get(i).SSID;
                unsorted_SSIDs.add(ssid_to_add_to_list);
            }
        }

        List <String> sNetworks_filtered = MyWifiUtils.sortByAlphaAndRemoveBlanksAndDuplicates(unsorted_SSIDs);

        predefinedRadioGroup =  findViewById(R.id.predefinedRadioGroup);

        for (int i=0; i<= configuredNetworkList.size() -1 ; i++)
        {
            if (configuredNetworkList.get(i).SSID != null)
            {
                String ssid_to_add_to_list = sNetworks_filtered.get(i);

                String without_quotes =  ssid_to_add_to_list.substring(1, ssid_to_add_to_list.length() - 1);
                boolean match = false;

                for(ScanResult list_1_item: availableNetworkList) {

                    if(list_1_item.SSID.compareTo(without_quotes) == 0)
                    {
                        match = true;

                        break;
                    } else {
                        match = false;
                    }

                }

                RadioButton rb = new RadioButton(getApplicationContext());
                rb.setText(without_quotes);

                if (match)
                    rb.setBackgroundColor(Color.GREEN);

                predefinedRadioGroup.addView(rb);
            }
        }
    }

//    private void populateAvailableNetworksList()
//    {
//        availableNetworkList = wifiManager.getScanResults();
//
//        availableRadioGroup.removeAllViews();
//
//        List <String> sAvail_unfiltered = new ArrayList();
//
//        // Populate String List
//        for (ScanResult res : availableNetworkList) {
//            sAvail_unfiltered.add(res.SSID);
//        }
//
//        List <String> sAvail_filtered = new ArrayList();
//        // Filter String List
//        sAvail_filtered = MyWifiUtils.sortByAlphaAndRemoveBlanksAndDuplicates(sAvail_unfiltered);
//
//        for (String res : sAvail_filtered)
//        {
//            Log.d(TAG_WIFI, res);
//
//            RadioButton rb = new RadioButton(getApplicationContext());
//            rb.setText(res);
//            availableRadioGroup.addView(rb);
//
//        }
//    }






}

