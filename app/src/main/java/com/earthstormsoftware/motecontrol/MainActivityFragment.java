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

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*
    Fragment used to display the main UI. Using fragments is generally considered good practice,
    even though the initial UI is quite simple.
 */

public class MainActivityFragment extends Fragment {

    private String moteURI;

    private ToggleButton tglMoteSwitch;
    private Button btnColourPicker;
    private int initialPickerColor;

    private ColorPickerDialog colorPickerDialog;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_main, container,false);

        // Retrieve the URL of the Mote API from device storage
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        moteURI = prefs.getString("mote_uri", "http://127.0.0.1");

        // Setup the colour picker dialog that will be called when the user clicks the button
        initialPickerColor = Color.WHITE;
        colorPickerDialog = new ColorPickerDialog(getActivity(), initialPickerColor, new ColorPickerDialog.OnColorSelectedListener() {

            @Override
            public void onColorSelected(int color) {
                GradientDrawable bgShape = (GradientDrawable)btnColourPicker.getBackground();
                bgShape.setColor(color);
                setMoteColour(color);
            }
        });

        // Setup the colour picker button so when clicked, it shows the dialog defined above.
        btnColourPicker = (Button) view.findViewById(R.id.btnColourPicker);
        btnColourPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                colorPickerDialog.show();
            }
        });


        // Setup the toggle button which will turn the Mote on and off
        tglMoteSwitch = (ToggleButton) view.findViewById(R.id.tglMoteSwitch);
        tglMoteSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    toggleMoteStatus(true);
                } else {
                    toggleMoteStatus(false);
                }
            }
        });

        // Try and get the current status of the Mote
        updateMoteStatus();

        return view;
    }

    // Call the Mote API to get the current state and colour of the Mote
    public void updateMoteStatus(){

        // Uncomment to enable Retrofit logging
        //HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        //logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        //httpClient.addInterceptor(logging);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(moteURI)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        MoteAPI moteAPI = retrofit.create(MoteAPI.class);

        // This is where the API actually gets called.
        // Note: Using Enqueue means this is an asynchronous call, and not handled on the UI thread.
        final Call<MoteStatus> call = moteAPI.getMoteStatus();
        call.enqueue(new Callback<MoteStatus>() {
            @Override
            public void onResponse(Call<MoteStatus> call, Response<MoteStatus> response) {

                // Change the toggle switch setting depending on the Mote status
                if (response.body().getStatus() == 1){
                    tglMoteSwitch.setChecked(true);
                } else {
                    tglMoteSwitch.setChecked(false);
                }

                // Change the colour button depending on the Mote colour/
                int curColour = Color.parseColor("#" + response.body().getColour());
                GradientDrawable bgShape = (GradientDrawable)btnColourPicker.getBackground();
                bgShape.setColor(curColour);
            }

            @Override
            public void onFailure(Call<MoteStatus> call, Throwable t) {
                // If the API call fails for any reason a short toast will be popped up.
                Toast.makeText(getActivity(), R.string.txt_mote_api_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Call the Mote API to set the current state (on or off) of the Mote
    public void toggleMoteStatus(boolean newState){

        //HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        //logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        //httpClient.addInterceptor(logging);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(moteURI)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        MoteAPI moteAPI = retrofit.create(MoteAPI.class);

        // Call different API methods for turning on and off
        Call<MoteStatus> call;
        if (newState == true) {
            call = moteAPI.setMoteOn();
        } else {
            call = moteAPI.setMoteOff();
        }

        call.enqueue(new Callback<MoteStatus>() {
            @Override
            public void onResponse(Call<MoteStatus> call, Response<MoteStatus> response) {

                // If the call worked, the toggle switch will already have been set to the desired
                // state, so we just need to update the current colour, which is provided in the
                // response
                int curColour = Color.parseColor("#" + response.body().getColour());
                GradientDrawable bgShape = (GradientDrawable)btnColourPicker.getBackground();
                bgShape.setColor(curColour);
            }

            @Override
            public void onFailure(Call<MoteStatus> call, Throwable t) {
                Toast.makeText(getActivity(), R.string.txt_mote_api_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Call the Mote API to set the desired colour of the Mote
    public void setMoteColour(int newColour){

        //HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        //logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        //httpClient.addInterceptor(logging);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(moteURI)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        MoteAPI moteAPI = retrofit.create(MoteAPI.class);

        // Android stores colour as ints, but the API expects RGB values in the form RRGGBB, so we
        // need to convert before calling the API
        String strColour = String.format("%06X", (0xFFFFFF & newColour));
        final Call<MoteStatus> call = moteAPI.setMoteColour(strColour);

        call.enqueue(new Callback<MoteStatus>() {
            @Override
            public void onResponse(Call<MoteStatus> call, Response<MoteStatus> response) {

                // The state (on or off) was not changed, so just update the colour
                int curColour = Color.parseColor("#" + response.body().getColour());
                GradientDrawable bgShape = (GradientDrawable)btnColourPicker.getBackground();
                bgShape.setColor(curColour);
            }

            @Override
            public void onFailure(Call<MoteStatus> call, Throwable t) {
                Toast.makeText(getActivity(), R.string.txt_mote_api_error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
