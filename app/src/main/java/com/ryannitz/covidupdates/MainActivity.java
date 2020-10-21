package com.ryannitz.covidupdates;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.ryannitz.covidupdates.utility.AlarmUtility;
import com.ryannitz.covidupdates.utility.FileHandler;
import com.ryannitz.covidupdates.utility.JsonUtility;
import com.ryannitz.covidupdates.utility.Logger;
import com.ryannitz.covidupdates.utility.NotificationUtility;
import com.ryannitz.covidupdates.utility.Utility;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;



/*
TODO:
    Code:
        -add another json object similar to "attributes" called attributeDiffs that will record the difference from the previous record. (first call set to 0)
            -This can be used for the notifications and for the main screen to make it more user friendly when they click the UI "update" button
         -Add a secret area to debug
            -click to send notification
            -click to populate with fake data
            -toggle send to our group chat
        -use settings/preferences fragment instead of json parsing user settings (Still keep user json for user-data).
        -if for some reason the api makes a call in the background while the user is using the app, then toast a message
        -toast a message "Latest data now"
    Main Screen:
        -On json screen, add up and down arrows to increase/decrease the font size :)
        -add button to copy json
    About:
        -Total Data used
        -Source API
        -How often the source API updates
        -The typical data usage
        -The benefits of the app
            -VERY low data usage compared to news sites or the covid dashboard.
            -Push notifications when new cases are reported.
        -version
    Settings:
        -turn on/off the request alarm
            -Adjust api request interval
            -if off, no notifications
            -select data to be notified on.
        -turn on/off the notifications
            -select the feilds that you wish to be updated on.
                -default always update new cases, even if 0. Allow them to change specifically this as well
        -Messenger authentication.
            -log in/log out
        -request stored data
        -reset default settingFragment


ERRORS:
    Fresh install without touching anything. The first alarm call to API throws developer error toast.
 */


public class MainActivity extends AppCompatActivity{

    public static LinearLayout mainLinearLayout;
    public static LinearLayout dataViewHolder;
    public TextView dataSourceURLText;
    public ConstraintLayout header;
    public ImageView toggleRawJsonButton;
    public ImageButton incFontButton;
    public ImageButton decFontButton;
    public ImageButton copyJsonButton;

    private Context ctx;
    private Calendar debugStartTime;
    private int debugClickCount;
    public static boolean active = false;
    public static UserSettings userSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = getApplicationContext();
        setContentView(R.layout.activity_main);

        Toolbar myToolBar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolBar);

        //AccessToken accessToken = AccessToken.getCurrentAccessToken();
        Log.e("Storage:", ctx.getFilesDir().getAbsolutePath());
        userSettings = UserSettings.loadUserSettings(ctx);



        mainLinearLayout = (LinearLayout) findViewById(R.id.main_layout);
        dataViewHolder = (LinearLayout) findViewById(R.id.dataViewHolder);
        dataSourceURLText = (TextView) findViewById(R.id.dataSourceURLText);
        dataSourceURLText.setMovementMethod(LinkMovementMethod.getInstance());
        toggleRawJsonButton = (ImageView) findViewById(R.id.rawJsonButton);

        if(userSettings.getRawJsonOn()){
            toggleRawJsonButton.setImageResource(R.mipmap.ic_raw_json_foreground);
        }

        if(FileHandler.provinceHasJsonFile(ctx, FileHandler.NB_JSON_FILENAME)){
            Log.e(Logger.FILE, "JSON file Exists. Pulling data from file.");
            try {
                String jsonStr = FileHandler.getFileContents(ctx, FileHandler.NB_JSON_FILENAME);
                JSONObject json = new JSONObject(jsonStr);
                createDataViews(ctx, json);
            }catch(JSONException jse){
                jse.printStackTrace();
            }
        }else{
            Log.e(Logger.FILE, "JSON file not found. Creating new file.");
            CasesHTTPRequester fetchedData = new CasesHTTPRequester(ctx, userSettings, false, false);
            fetchedData.execute();
        }


        toggleRawJsonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userSettings.setRawJsonOn(!userSettings.getRawJsonOn());
                int id = userSettings.getRawJsonOn()? R.mipmap.ic_raw_json_foreground : R.mipmap.ic_raw_json_primary_foreground;
                toggleRawJsonButton.setImageResource(id);
                userSettings = UserSettings.updateSettings(ctx, userSettings);
                Log.e(Logger.USER, userSettings.getRawJsonOn()+"");
                try {
                    createDataViews(ctx, new JSONObject(FileHandler.getFileContents(ctx, FileHandler.NB_JSON_FILENAME)));
                    if(!userSettings.getRawJsonOn() && dataViewHolder.getChildCount() > 1){
                        View targetView = mainLinearLayout.findViewById(R.id.mainFooter);
                        targetView.getParent().requestChildFocus(targetView, targetView);
                    }
                }catch (JSONException jse){
                    jse.printStackTrace();
                }
            }
        });

        Button requestBtn = (Button) findViewById(R.id.callRequestButton);
        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CasesHTTPRequester fetchedData = new CasesHTTPRequester(ctx, userSettings, false, false);
                fetchedData.execute();
            }
        });

        header = findViewById(R.id.header);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(debugStartTime == null){
                    debugStartTime = Calendar.getInstance();
                    debugStartTime.setTimeInMillis(System.currentTimeMillis());
                }else{
                    if(debugStartTime.getTimeInMillis() + 5000 > System.currentTimeMillis() && debugClickCount < 8){
                        debugClickCount++;
                        Log.e(Logger.DEBUG, "Clicks:" + debugClickCount);
                        if(debugClickCount == 8){
                            CasesHTTPRequester casesHTTPRequester = new CasesHTTPRequester(ctx, userSettings, true, true);
                            casesHTTPRequester.execute();
                        }
                    }else{
                        debugClickCount = 0;
                        debugStartTime = null;
                    }
                }
            }
        });

        AlarmUtility.createNewAlarm(this);
        NotificationUtility.createDefaultNotificationChannel(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.about:
                Log.e("EVENT", "ABOUT ITZEM CLICKED");
                return true;
            case R.id.settings:
                Log.e("EVENT", "SETIGNS ITZEM CLICKED");
                return true;
            case R.id.testFragment:
                Log.e("EVENT", "testFragment CLICKED");
                //Fragment fragment = new TestingFragment();
                //FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                //transaction.replace(R.id.main_layout, fragment);
                //transaction.addToBackStack(null);
                //transaction.commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

    public static void createDataViews(Context ctx, JSONObject json){
        try {
            LayoutInflater layoutInflater = LayoutInflater.from(ctx);
            dataViewHolder.removeAllViews();
            if(userSettings.getRawJsonOn()){
                //print jsonView
                View view = layoutInflater.inflate(R.layout.raw_json_layout, dataViewHolder, false);
                TextView rawJson = (TextView) view.findViewById(R.id.rawJsonText);

                //not working, will have to manually pretty print.
                rawJson.setText(JsonUtility.prettyPrintJson(FileHandler.getFileContents(ctx, FileHandler.NB_JSON_FILENAME)));
                dataViewHolder.addView(view);
            }else{
                //print DataViews
                //will have to filter this out at some point. Should I not keep the entire object?
                ArrayList<View> viewList = new ArrayList<>();
                JSONObject attributes = json.getJSONObject(CovidStats.KEY_ATTRIBUTES);
                Iterator<String> keys = attributes.keys();

                while (keys.hasNext()) {
                    String key = keys.next();
                    String keydata = attributes.getString(key);

                    View view = layoutInflater.inflate(R.layout.data_strip, dataViewHolder, false);

                    TextView dataLabel = (TextView) view.findViewById(R.id.dataLabel);
                    dataLabel.setText(CovidStats.nbKeyLabelMap.get(key) + ":");

                    TextView dataText = (TextView) view.findViewById(R.id.dataText);
                    dataText.setText(keydata);

                    viewList.add(view);
                }
                //if we did get another json set, then we are safe to delete the existing ones
                //could maybe need to have a loading modal
                if(!viewList.isEmpty()){
                    dataViewHolder.removeAllViews();
                    for(View v : viewList){
                        dataViewHolder.addView(v);
                    }
                }
            }

            //always update this
            Date updateDate = new Date();
            updateDate.setTime(json.getLong(CovidStats.KEY_NB_LASTSOURCEUPDATE));
            TextView sourceUpdateText = (TextView) mainLinearLayout.findViewById(R.id.sourceUpdateText);
            sourceUpdateText.setText(updateDate + "");

            //finally if everything goes well, set time of local update
            Date requestDate = new Date();
            requestDate.setTime(json.getLong(CovidStats.KEY_REQUEST_TIME));
            TextView lastCallText = (TextView) mainLinearLayout.findViewById(R.id.lastCallText);
            lastCallText.setText(requestDate+"");

            TextView lastCallDifference = (TextView) mainLinearLayout.findViewById(R.id.lastCallDifference);
            long timeDiff = (new Date().getTime() - requestDate.getTime());
            String lastCallDiffStr = Utility.millisToTime(timeDiff)+"";
            lastCallDifference.setText(lastCallDiffStr);
        }catch (JSONException jse){
            jse.printStackTrace();
        }
    }
}
