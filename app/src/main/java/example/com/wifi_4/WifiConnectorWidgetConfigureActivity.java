package example.com.wifi_4;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * The configuration screen for the {@link WifiConnectorWidget WifiConnectorWidget} AppWidget.
 */
public class WifiConnectorWidgetConfigureActivity extends Activity {

    private static final String PREFS_NAME = "example.com.wifi_4.WifiConnectorWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";

    private static final String SSID_PREFIX = "SSID_PREFIX";
    //private static final String DEFAULT_COLOR_WIDGET_STRING_HEX = "#4EA651";
    private static String DEFAULT_COLOR_WIDGET_STRING_HEX ;


    private static  int DEFAULT_COLOR_WIDGET ;

    private String _ssid;

    private WifiManager wifiManager;
    private List<WifiConfiguration> configuredNetworkList;
    private RadioGroup predefinedRadioGroup;

    private String widgetTextEntered;
    private String widgetSSIDClicked;
    private Button addWidgetButton;
    private Button colorPickerButton;

    int radioitemselected = -1;

    private static ColorPicker cp;
    private static int widget_main_color;

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    EditText mAppWidgetText_label;
    EditText mAppWidgetText_ssid;

    View.OnClickListener mColorOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = WifiConnectorWidgetConfigureActivity.this;

            cp = new ColorPicker(WifiConnectorWidgetConfigureActivity.this);
            /* Show color picker dialog */

            cp.setColor(widget_main_color);
            cp.show();

            cp.enableAutoClose(); // Enable auto-dismiss for the dialog

            /* Set a new Listener called when user click "select" */
            cp.setCallback(new ColorPickerCallback() {
                @Override
                public void onColorChosen(@ColorInt int color) {
                    // Do whatever you want
                    widget_main_color = color;
                    Toast.makeText(context, "widget_main_color: " + widget_main_color, Toast.LENGTH_SHORT).show();
                    // Examples

                    colorPickerButton.setBackgroundColor(color);

                    Log.d("Alpha", Integer.toString(Color.alpha(color)));
                    Log.d("Red", Integer.toString(Color.red(color)));
                    Log.d("Green", Integer.toString(Color.green(color)));
                    Log.d("Blue", Integer.toString(Color.blue(color)));

                    Log.d("Pure Hex", Integer.toHexString(color));
                    Log.d("#Hex no alpha", String.format("#%06X", (0xFFFFFF & color)));
                    Log.d("#Hex with alpha", String.format("#%08X", (0xFFFFFFFF & color)));


                    //saveWidgetColorPref(context,color);
                    // If the auto-dismiss option is not enable (disabled as default) you have to manually dimiss the dialog
                    // cp.dismiss();
                }


            });

            Toast.makeText(context, "...now here ...", Toast.LENGTH_SHORT).show();

        }
    };

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = WifiConnectorWidgetConfigureActivity.this;

            // When the button is clicked, store the string locally
            widgetTextEntered = mAppWidgetText_label.getText().toString();

            saveWidgetColorPref(context,widget_main_color);
            saveTitlePref(context, mAppWidgetId, widgetTextEntered);

            RadioButton rb = predefinedRadioGroup.findViewById(radioitemselected);
            widgetSSIDClicked = (String) rb.getText();

            saveSSIDPref(context, mAppWidgetId, widgetSSIDClicked);

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            WifiConnectorWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            resultValue.putExtra(SSID_PREFIX,_ssid);

            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    public WifiConnectorWidgetConfigureActivity() {

        super();

    }

//     Write the prefix to the SharedPreferences object for this widget
    static void saveTitlePref(Context context, int appWidgetId, String title) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId + "_title", title);
        prefs.apply();
    }
    static void saveSSIDPref(Context context, int appWidgetId, String ssid) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId + "_ssid", ssid);
        prefs.apply();
    }
    static void saveWidgetColorPref(Context context, Integer color) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_PREFIX_KEY + "_widget_main_color", color);
        prefs.apply();
    }



    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadTitlePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId + "_title", null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return context.getString(R.string.appwidget_text_title);
        }
    }

    static String loadSSIDPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String ssidValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId + "_ssid", null);
        if (ssidValue != null) {
            return ssidValue;
        } else {
            return context.getString(R.string.appwidget_text_ssid);
        }
    }

    static List<String> loadSSIDPrefs(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        List <String> ssidValues = (List<String>) prefs.getStringSet(PREF_PREFIX_KEY + "_ssid", null);
        if (ssidValues != null) {
            return ssidValues;
        } else {
            return null;
        }
    }


    static Integer loadColorPref(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);

        Log.d("COLORS", "DEFAULT_COLOR_WIDGET: " + DEFAULT_COLOR_WIDGET);
        int colorValue = prefs.getInt(PREF_PREFIX_KEY  +  "_widget_main_color",  DEFAULT_COLOR_WIDGET);


        if (colorValue != 0)
            return colorValue;
        else

            return DEFAULT_COLOR_WIDGET;
    }

    static void deletePrefs(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    public static Integer getDefaultWidgetColor() {
        return DEFAULT_COLOR_WIDGET;
    }


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.wifi_connector_widget_configure);

        DEFAULT_COLOR_WIDGET_STRING_HEX = getString(R.string.widget_default_hex_green);

        DEFAULT_COLOR_WIDGET = Color.parseColor(DEFAULT_COLOR_WIDGET_STRING_HEX);

        addWidgetButton = findViewById(R.id.add_button);
        colorPickerButton = findViewById(R.id.modify_color);


        widget_main_color = loadColorPref(getApplicationContext());


        //widget_main_color = 28416;
        colorPickerButton.setBackgroundColor(widget_main_color);


        addWidgetButton.setEnabled(false);

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


        mAppWidgetText_label    = (EditText) findViewById(R.id.appwidget_edittext_widget_title);

        mAppWidgetText_ssid     = (EditText) findViewById(R.id.appwidget_edittext_ssid_label);

        // Purely read only and greyed out
        mAppWidgetText_ssid.setEnabled(false);
        mAppWidgetText_ssid.setFocusable(false);
        mAppWidgetText_ssid.setFocusableInTouchMode(false);
        mAppWidgetText_ssid.setClickable(false);

        findViewById(R.id.add_button).setOnClickListener(mOnClickListener);
        findViewById(R.id.modify_color).setOnClickListener(mColorOnClickListener);

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

//        String wighetTitle = loadTitlePref(WifiConnectorWidgetConfigureActivity.this, mAppWidgetId);


        mAppWidgetText_label.setText(loadTitlePref(WifiConnectorWidgetConfigureActivity.this, mAppWidgetId));
        mAppWidgetText_ssid.setText("Network SSID selected");


        predefinedRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                radioitemselected = predefinedRadioGroup.getCheckedRadioButtonId();

                if(group.getCheckedRadioButtonId() == -1)
                {
                }
                 else
                {
                    RadioButton rb = predefinedRadioGroup.findViewById(radioitemselected);

                    String rb_ssid = (String) rb.getText();

                    _ssid = rb_ssid;

                    mAppWidgetText_label.setText(rb_ssid);
                    mAppWidgetText_ssid.setText(rb_ssid);

                    addWidgetButton.setEnabled(true);
                }

            }
        });

    }

}

