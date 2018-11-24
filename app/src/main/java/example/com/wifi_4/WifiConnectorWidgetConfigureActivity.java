package example.com.wifi_4;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * The configuration screen for the {@link WifiConnectorWidget WifiConnectorWidget} AppWidget.
 */
public class WifiConnectorWidgetConfigureActivity extends Activity {

    private static final String PREFS_NAME = "example.com.wifi_4.WifiConnectorWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";

    private WifiManager wifiManager;
    private List<WifiConfiguration> configuredNetworkList;
    private RadioGroup predefinedRadioGroup;



    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    EditText mAppWidgetText;
    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = WifiConnectorWidgetConfigureActivity.this;

            // When the button is clicked, store the string locally
            String widgetText = mAppWidgetText.getText().toString();

            //////set SSID here

            //ssid = "LivingRoomMcLivingRoomFace-2";
            /////////////////////


            saveTitlePref(context, mAppWidgetId, widgetText);


            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            WifiConnectorWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    public WifiConnectorWidgetConfigureActivity() {

        super();

    }

//     Write the prefix to the SharedPreferences object for this widget
    static void saveTitlePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.apply();
    }



    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadTitlePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return context.getString(R.string.appwidget_text);
        }
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.wifi_connector_widget_configure);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        configuredNetworkList =  wifiManager.getConfiguredNetworks();


        predefinedRadioGroup =  findViewById(R.id.bastard_RadioGroup);

        ArrayList<String> unsorted_SSIDs = new ArrayList<String>();

        for (int i=0; i<= configuredNetworkList.size() -1 ; i++) {

            if (configuredNetworkList.get(i).SSID != null) {
                String ssid_to_add_to_list = configuredNetworkList.get(i).SSID;
                unsorted_SSIDs.add(ssid_to_add_to_list);
            }
        }

        List <String> sNetworks_filtered;
        sNetworks_filtered = MyWifiUtils.sortByAlphaAndRemoveBlanksAndDuplicates(unsorted_SSIDs);

        for (int i=0; i<= configuredNetworkList.size() -1 ; i++)
        {
            if (configuredNetworkList.get(i).SSID != null)
            {
                String ssid_to_add_to_list = sNetworks_filtered.get(i);

                String without_quotes =  ssid_to_add_to_list.substring(1, ssid_to_add_to_list.length() - 1);

                RadioButton rbb = new RadioButton(this);
                rbb.setText(without_quotes);

                predefinedRadioGroup.addView(rbb);
            }
        }

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);


        mAppWidgetText = (EditText) findViewById(R.id.appwidget_text);
        findViewById(R.id.add_button).setOnClickListener(mOnClickListener);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        mAppWidgetText.setText(loadTitlePref(WifiConnectorWidgetConfigureActivity.this, mAppWidgetId));


        predefinedRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                int radioitemselected = predefinedRadioGroup.getCheckedRadioButtonId();

                if(group.getCheckedRadioButtonId() == -1)
                {
                }
                 else
                {
                    RadioButton rb = predefinedRadioGroup.findViewById(radioitemselected);

                    String rb_ssid = (String) rb.getText();

                    mAppWidgetText.setText(rb_ssid);
                }

            }
        });

    }

    private void populateRadioGroupWithConfiguredNetworks()
    {


    }
}

