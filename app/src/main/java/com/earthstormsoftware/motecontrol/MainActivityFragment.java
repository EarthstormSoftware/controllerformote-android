/*
 * Copyright 2016 Richard Ruston
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.earthstormsoftware.motecontrol;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.earthstormsoftware.motecontrol.api.MoteAPIService;
import com.earthstormsoftware.motecontrol.model.Mote;
import com.earthstormsoftware.motecontrol.api.MoteAPIResponseType;
import com.earthstormsoftware.motecontrol.model.MoteMode;

/*
 *  Fragment used to display the main UI. Using fragments is generally considered good practice,
 *  even though the initial UI is quite simple.
 */

public class MainActivityFragment extends Fragment {

    private Mote mote;
    private ToggleButton tglMoteSwitch;
    private Button btnColourPicker;

    private BroadcastReceiver moteUpdateReceiver;
    private ColorPickerDialog colorPickerDialog;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_main, container,false);

        // Retrieve the URL of the Mote API from device storage
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String moteURI = prefs.getString("mote_uri", null);

        // Create the local Mote object and get the current status of the Mote from the host device
        if (moteURI != null){
            mote = new Mote(moteURI,"1","Mote1",false, MoteMode.COLOUR);
            MoteAPIService.startActionGetState(getContext(),mote);
        } else {
            Toast.makeText(getActivity(), R.string.configure_api_url, Toast.LENGTH_SHORT).show();
        }

        // Setup the colour picker dialog that will be called when the user clicks the button
        int initialPickerColor = Color.WHITE;
        colorPickerDialog = new ColorPickerDialog(getActivity(), initialPickerColor, new ColorPickerDialog.OnColorSelectedListener() {

            @Override
            public void onColorSelected(int color) {
                Log.i(MoteControl.TAG,"Color selected: " + color);
                if (mote != null) {
                    mote.setColour(color);
                }
                setMoteColour(color);
            }
        });

        // Setup the colour picker button so when clicked, it shows the dialog defined above.
        btnColourPicker = (Button) view.findViewById(R.id.btnColourPicker);
        btnColourPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mote != null) {
                    colorPickerDialog.show();
                } else {
                    Toast.makeText(getActivity(), R.string.configure_api_url, Toast.LENGTH_SHORT).show();
                }

            }
        });

        // Setup the toggle button which will turn the Mote on and off
        tglMoteSwitch = (ToggleButton) view.findViewById(R.id.tglMoteSwitch);
        tglMoteSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mote != null) {
                    if (isChecked) {
                        setMoteState(true);
                    } else {
                        setMoteState(false);
                    }
                } else {
                    if (isChecked) {
                        tglMoteSwitch.setChecked(false);
                        Toast.makeText(getActivity(), R.string.configure_api_url, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        /*
         * When the Fragment is visible and active, use a BroadcastReceiver to receive notifications
         * when an API response is received so the displayed information can be updated.
         */
        if (moteUpdateReceiver == null){
            moteUpdateReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    MoteAPIResponseType mrt = (MoteAPIResponseType) intent.getSerializableExtra("result");
                    if (mrt == MoteAPIResponseType.OK) {
                        mote.setOn(intent.getBooleanExtra("state",false));
                        mote.setColour(intent.getIntExtra("colour",0));
                        updateDisplay();
                    } else {
                        Toast.makeText(getActivity(), mrt.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            };
            Log.i(MoteControl.TAG,"BroadcastReceiver created");
        }
        IntentFilter intent = new IntentFilter(MoteControl.MOTE_API_RESPONSE);
        getActivity().registerReceiver(moteUpdateReceiver, intent);
        Log.i(MoteControl.TAG,"BroadcastReceiver registered");

        // Update the display whenever the user returns to this fragment
        updateDisplay();
    }

    @Override
    public void onPause() {
        super.onPause();

        // If the user navigates away from the fragment, unregister the BroadcastReveiver as
        // notifications are not required.
        getActivity().unregisterReceiver(moteUpdateReceiver);
        Log.i(MoteControl.TAG,"BroadcastReceiver unregistered");
    }

    // Update the UI elements based on the current known state
    private void updateDisplay(){

        Log.i(MoteControl.TAG,"Updating UI based on current status");

        if (mote != null) {
            // Change the toggle switch setting depending on the Mote status
            if (mote.isOn()){
                tglMoteSwitch.setChecked(true);
            } else {
                tglMoteSwitch.setChecked(false);
            }

            // Change the colour button depending on the Mote colour/
            GradientDrawable bgShape = (GradientDrawable)btnColourPicker.getBackground();
            bgShape.setColor(mote.getColour());
        }
    }

    // Call the Mote API to get the current status of the Mote from the host device
    public void updateMoteStatus(){
        if (mote != null){
            Log.i(MoteControl.TAG,"Requesting Mote status update");
            MoteAPIService.startActionGetState(getContext(), mote);
            updateDisplay();
        } else {
            Toast.makeText(getActivity(), R.string.configure_api_url, Toast.LENGTH_SHORT).show();
        }
    }

    // Call the Mote API to set the current state (on or off) on the Mote device
    public void setMoteState(boolean newState){
        if (mote != null){
            Log.i(MoteControl.TAG,"Setting new Mote state");
            MoteAPIService.startActionSetState(getContext(), mote, newState);
            updateDisplay();
        } else {
            Toast.makeText(getActivity(), R.string.configure_api_url, Toast.LENGTH_SHORT).show();
        }
    }

    // Call the Mote API to set the desired colour of the Mote on the host device
    public void setMoteColour(int newColour){
        if (mote != null){
            Log.i(MoteControl.TAG,"Setting new Mote colour");
            MoteAPIService.startActionSetColour(getContext(),mote,newColour);
            updateDisplay();
        } else {
            Toast.makeText(getActivity(), R.string.configure_api_url, Toast.LENGTH_SHORT).show();
        }

    }
}
