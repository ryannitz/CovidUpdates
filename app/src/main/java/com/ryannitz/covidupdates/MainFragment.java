package com.ryannitz.covidupdates;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ryannitz.covidupdates.utility.FileHandler;
import com.ryannitz.covidupdates.utility.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public static LinearLayout mainLinearLayout;
    public static LinearLayout dataViewHolder;
    public TextView dataSourceURLText;
    public ConstraintLayout header;
    public ImageView toggleRawJsonButton;
    public MainPageDataContainer mainPageDataContainer;
    public ImageButton incFontButton;
    public ImageButton decFontButton;
    public ImageButton copyJsonButton;
    public Activity activity;

    private Context ctx;
    private Calendar debugStartTime;
    private int debugClickCount;
    public static boolean active = false;
    public static UserStats userStats;


    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        View v = getView();
        Toolbar myToolBar = v.findViewById(R.id.my_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolBar);

        mainLinearLayout = (LinearLayout) v.findViewById(R.id.main_layout);
        dataViewHolder = (LinearLayout) v.findViewById(R.id.dataViewHolder);
        dataSourceURLText = (TextView) v.findViewById(R.id.dataSourceURLText);
        dataSourceURLText.setMovementMethod(LinkMovementMethod.getInstance());
        toggleRawJsonButton = (ImageView) v.findViewById(R.id.rawJsonButton);

        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        mainPageDataContainer = new MainPageDataContainer();
        fragmentTransaction.add(R.id.mainDataContainer, mainPageDataContainer);
        fragmentTransaction.commit();

        if(userStats.getRawJsonOn()){
            toggleRawJsonButton.setImageResource(R.mipmap.ic_raw_json_foreground);
        }

        if(FileHandler.provinceHasJsonFile(ctx, FileHandler.NB_JSON_FILENAME)){
            Log.e(Logger.FILE, "JSON file Exists. Pulling data from file.");
            try {
                String jsonStr = FileHandler.getFileContents(ctx, FileHandler.NB_JSON_FILENAME);
                JSONObject json = new JSONObject(jsonStr);
                //createDataViews(ctx, json);
            }catch(JSONException jse){
                jse.printStackTrace();
            }
        }else{
            Log.e(Logger.FILE, "JSON file not found. Creating new file.");
            CasesHTTPRequester fetchedData = new CasesHTTPRequester(mainPageDataContainer, ctx, userStats, false, false);
            fetchedData.execute();
        }


        toggleRawJsonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userStats.setRawJsonOn(!userStats.getRawJsonOn());
                int id = userStats.getRawJsonOn()? R.mipmap.ic_raw_json_foreground : R.mipmap.ic_raw_json_primary_foreground;
                toggleRawJsonButton.setImageResource(id);
                userStats = UserStats.updateSettings(ctx, userStats);
                Log.e(Logger.USER, userStats.getRawJsonOn()+"");
//                try {
                    //createDataViews(ctx, new JSONObject(FileHandler.getFileContents(ctx, FileHandler.NB_JSON_FILENAME)));
                    if(!userStats.getRawJsonOn() && dataViewHolder.getChildCount() > 1){
                        View targetView = mainLinearLayout.findViewById(R.id.mainFooter);
                        targetView.getParent().requestChildFocus(targetView, targetView);
                    }
//                }catch (JSONException jse){
//                    jse.printStackTrace();
//                }
            }
        });

        Button requestBtn = (Button) v.findViewById(R.id.callRequestButton);
        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CasesHTTPRequester fetchedData = new CasesHTTPRequester(mainPageDataContainer, ctx, userStats, false, false);
                fetchedData.execute();
            }
        });

        header = v.findViewById(R.id.header);
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
                            CasesHTTPRequester casesHTTPRequester = new CasesHTTPRequester(mainPageDataContainer, ctx, userStats, true, true);
                            casesHTTPRequester.execute();
                        }
                    }else{
                        debugClickCount = 0;
                        debugStartTime = null;
                    }
                }
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
