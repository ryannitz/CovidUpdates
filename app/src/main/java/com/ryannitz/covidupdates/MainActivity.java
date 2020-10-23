package com.ryannitz.covidupdates;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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



import com.ryannitz.covidupdates.utility.FileHandler;
import com.ryannitz.covidupdates.utility.Logger;
import com.ryannitz.covidupdates.utility.NotificationUtility;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.Calendar;

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
    File:
    Code:
        -Clicking the notification will bring person to the app. (When opening app we should call new data but not when notification, because then we won't see the differences on dashboard)
         -Add a secret area to debug
            -toggle send to our group chat
        -use settings/preferences fragment instead of json parsing user settings (Still keep user json for user-data).
        -if for some reason the api makes a call in the background while the user is using the app, then toast a message
        -toast a message "Latest data now"
    Main Screen:
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


public class MainActivity extends AppCompatActivity implements MainPageDataContainer.OnFragmentInteractionListener{

    public LinearLayout mainLinearLayout;
    public LinearLayout dataViewHolder;
    public TextView dataSourceURLText;
    public ConstraintLayout header;
    public ImageView toggleRawJsonButton;
    public MainPageDataContainer mainPageDataContainer;


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

        //AccessToken accessToken = AccessToken.getCurrentAccessToken();
        Log.e(Logger.DEVICE, ctx.getFilesDir().getAbsolutePath());

        userSettings = UserSettings.loadUserSettings(ctx);
        dataSourceURLText = findViewById(R.id.dataSourceURLText);
        dataSourceURLText.setMovementMethod(LinkMovementMethod.getInstance());
        toggleRawJsonButton = findViewById(R.id.rawJsonButton);
        mainLinearLayout = findViewById(R.id.main_layout);
        mainPageDataContainer = (MainPageDataContainer) getSupportFragmentManager().findFragmentById(R.id.mainDataContainer);

        Toolbar myToolBar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolBar);

        FileHandler.initFiles(this, mainPageDataContainer, userSettings);


        if(userSettings.getRawJsonOn()){
            toggleRawJsonButton.setImageResource(R.mipmap.ic_raw_json_foreground);
            LinearLayout jsonTextControls = mainPageDataContainer.getView().findViewById(R.id.jsonTextControls);
            jsonTextControls.setVisibility(View.VISIBLE);
        }


        toggleRawJsonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataViewHolder = mainPageDataContainer.getView().findViewById(R.id.dataViewHolder);

                //change the saved view style
                userSettings.setRawJsonOn(!userSettings.getRawJsonOn());
                userSettings = UserSettings.updateSettings(ctx, userSettings);
                int id = userSettings.getRawJsonOn()? R.mipmap.ic_raw_json_foreground : R.mipmap.ic_raw_json_primary_foreground;
                toggleRawJsonButton.setImageResource(id);

                //toggle the jsonControls
                LinearLayout jsonTextControls = mainPageDataContainer.getView().findViewById(R.id.jsonTextControls);
                jsonTextControls.setVisibility(userSettings.getRawJsonOn()? View.VISIBLE : View.GONE);

                Log.e(Logger.USER, userSettings.getRawJsonOn()+"");
                try {
                    mainPageDataContainer.createDataViews(ctx, new JSONObject(FileHandler.getFileContents(ctx, FileHandler.NB_JSON_FILENAME)));
                    if(!userSettings.getRawJsonOn() && dataViewHolder.getChildCount() > 1){
                        View targetView = mainLinearLayout.findViewById(R.id.mainFooter);
                        targetView.getParent().requestChildFocus(targetView, targetView);
                    }
                }catch (JSONException jse){
                    jse.printStackTrace();
                }
            }
        });

        Button requestBtn = findViewById(R.id.callRequestButton);
        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CasesHTTPRequester fetchedData = new CasesHTTPRequester(mainPageDataContainer, ctx, userSettings, false, false);
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
                            CasesHTTPRequester casesHTTPRequester = new CasesHTTPRequester(mainPageDataContainer,ctx, userSettings, true, true);

                            casesHTTPRequester.execute();
                        }
                    }else{
                        debugClickCount = 0;
                        debugStartTime = null;
                    }
                }
            }
        });

        //AlarmUtility.createNewAlarm(this);
        NotificationUtility.createDefaultNotificationChannel(this);
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    */


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


    @Override
    public void onFragmentInteraction(View view) {
    }
}
