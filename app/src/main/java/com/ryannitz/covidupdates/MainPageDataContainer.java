package com.ryannitz.covidupdates;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ryannitz.covidupdates.utility.FileHandler;
import com.ryannitz.covidupdates.utility.JsonUtility;
import com.ryannitz.covidupdates.utility.Logger;
import com.ryannitz.covidupdates.utility.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainPageDataContainer.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainPageDataContainer#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainPageDataContainer extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private int jsonFontIndex;

    private FrameLayout dataContainer;

    private  ConstraintLayout metaDataHeader;
    private LinearLayout dataViewHolder;
    private ConstraintLayout rawJsonLayout;
    private TextView jsonText;

    private ImageButton incFontButton;
    private ImageButton decFontButton;
    private ImageButton copyJsonButton;

    private View fragView;

    private OnFragmentInteractionListener mListener;

    public MainPageDataContainer() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainPageDataContainer.
     */
    // TODO: Rename and change types and number of parameters
    public static MainPageDataContainer newInstance(String param1, String param2) {
        MainPageDataContainer fragment = new MainPageDataContainer();
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
        View view = inflater.inflate(R.layout.fragment_main_page_data_container, container, false);
        fragView = view.getRootView();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListener.onFragmentInteraction(fragView);

        jsonFontIndex = Utility.DEFAULT_JSON_FONT_INDEX;

        //fragment view
        dataContainer = fragView.findViewById(R.id.dataContainer);
        metaDataHeader = fragView.findViewById(R.id.metaDataHeader);
        dataViewHolder = fragView.findViewById(R.id.dataViewHolder);


        incFontButton = fragView.findViewById(R.id.incFontButton);
        incFontButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rawJsonLayout = fragView.findViewById(R.id.rawJsonLayout);
                jsonText = rawJsonLayout.findViewById(R.id.rawJsonText);
                jsonText.setTextSize(Utility.JSON_FONTSIZES[jsonFontIndex == Utility.JSON_FONTSIZES.length-1 ? Utility.JSON_FONTSIZES.length-1 : ++jsonFontIndex]);
            }
        });
        decFontButton = fragView.findViewById(R.id.decFontButton);
        decFontButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rawJsonLayout = fragView.findViewById(R.id.rawJsonLayout);
                jsonText = rawJsonLayout.findViewById(R.id.rawJsonText);
                jsonText.setTextSize(Utility.JSON_FONTSIZES[jsonFontIndex == 0 ? 0 : --jsonFontIndex]);
            }
        });
        copyJsonButton = fragView.findViewById(R.id.copyJsonButton);
        copyJsonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rawJsonLayout = fragView.findViewById(R.id.rawJsonLayout);
                jsonText = rawJsonLayout.findViewById(R.id.rawJsonText);
                String clipLabel = "Covid Updates json";
                ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(clipLabel, jsonText.getText());
                clipboardManager.setPrimaryClip(clip);
                Toast toast = Toast.makeText(getContext(), "Copied to clipboard", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

    }

    public void createDataViews(Context ctx, JSONObject json){
        try {
            LayoutInflater layoutInflater = LayoutInflater.from(ctx);
            dataViewHolder.removeAllViews();
            if(MainActivity.userSettings.getRawJsonOn()){
                //print jsonView
                View view = layoutInflater.inflate(R.layout.raw_json_layout, dataViewHolder, false);
                TextView rawJson = view.findViewById(R.id.rawJsonText);

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

                    TextView dataLabel = view.findViewById(R.id.dataLabel);
                    dataLabel.setText(CovidStats.nbKeyLabelMap.get(key) + ":");

                    TextView dataText = view.findViewById(R.id.dataText);
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
            TextView sourceUpdateText = metaDataHeader.findViewById(R.id.sourceUpdateText);
            sourceUpdateText.setText(updateDate+"");

            //finally if everything goes well, set time of local update
            Date requestDate = new Date();
            requestDate.setTime(json.getLong(CovidStats.KEY_REQUEST_TIME));
            TextView lastCallText = metaDataHeader.findViewById(R.id.lastCallText);
            lastCallText.setText(requestDate+"");

            TextView lastCallDifference = metaDataHeader.findViewById(R.id.lastCallDifference);
            long timeDiff = (new Date().getTime() - requestDate.getTime());
            String lastCallDiffStr = Utility.millisToTime(timeDiff)+"";
            lastCallDifference.setText(lastCallDiffStr);
        }catch (JSONException jse){
            jse.printStackTrace();
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
        void onFragmentInteraction(View view);
    }
}
