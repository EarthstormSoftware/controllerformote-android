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

package com.earthstormsoftware.motecontrol.com.earthstormsoftware.motecontrol.moteutil;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import com.earthstormsoftware.motecontrol.MoteControl;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*
 * This class encapsulates a single Mote unit - the current state as known by the application, the
 * the API calls required to change the state on the device, validation of responses to the API calls,
 * and update of app state once a valid response is received.
 */
public class Mote {

    // Initially only the URI, on and colour fields are actually used, the additional fields have
    // been added in anticipation of future improvements
    private String uri = "";
    private String id = "";
    private String name = "";
    private boolean on = false;
    private MoteMode mode;
    private int colour;

    // The Retrofit and API instances are reused
    private Retrofit retrofit;
    private MoteAPI moteAPI;

    public Mote(String moteURI, String moteID, String moteName, boolean moteOn, MoteMode moteMode) {
        this.uri = moteURI;
        this.id = moteID;
        this.name = moteName;
        this.on = moteOn;
        this.mode = moteMode;
    }

    // Common initialisation for Retrofit for each API call.
    private MoteAPI getAPIInstance(){

        if (retrofit == null) {

            // Uncomment to enable Retrofit logging

            //HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            //logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            //httpClient.addInterceptor(logging);

            retrofit = new Retrofit.Builder()
                    .baseUrl(uri)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }

        if (moteAPI == null) {
            moteAPI = retrofit.create(MoteAPI.class);
        }
        return moteAPI;
    }

    /*
     * This method handles 'good' responses from the API. A 'good' response is one where the HTTP
     * request completed, not that the call worked. For example, an HTTP 404 counts as a 'good'
     * respone.
     *
     * Currently the API returns the same JSON object for every call, so a common method can be
     * used. If that changes, this approach will need to be reviewed.
     */
    private void handleResponse(Response<MoteAPIResponse> response){

        // Check that any response is what would be expected
        MoteAPIResponseType mrt = validateResponse(response);

        // If the response was validated, parse it into our local object
        if (mrt != MoteAPIResponseType.VALIDATION_ERROR) {

            // A 'successful' response is an HTTP 200 return code
            if (response.isSuccessful()) {

                // Update the local state
                MoteAPIResponse mar = response.body();
                if (mar.getStatus() == 1) {
                    on = true;
                } else {
                    on = false;
                }

                colour = Color.parseColor("#" + mar.getColour());

                // Otherwise the HTTP call failed in some way.
            } else {
                mrt = MoteAPIResponseType.API_ERROR;
            }
        }
        // Send a broadcast indicating the result of the API call.
        Intent i = new Intent(MoteControl.MOTE_API_RESPONSE);
        i.putExtra("result", mrt);
        MoteControl.getAppContext().sendBroadcast(i);

    }

    /*
     * This method handles 'failure' responses from the API, where the HTTP call was not able to
     * complete (i.e. there is no HTTP response code). This includes, for example, ConnectionRefused
     * and SocketTimeout errors. These errors could be pulled out in a more granular fashion by
     * checking the Exception that was thrown.
     */
    private void handleFailure(Call<MoteAPIResponse> call, Throwable t){
        MoteAPIResponseType mrt = MoteAPIResponseType.IO_ERROR;
        Intent i = new Intent(MoteControl.MOTE_API_RESPONSE);
        i.putExtra("result", mrt);
        MoteControl.getAppContext().sendBroadcast(i);
    }

    // If the HTTP call completed in some way, we should check to the response to ensure it is
    // correctly formatted before trying to use it
    private MoteAPIResponseType validateResponse(Response<MoteAPIResponse> response){
        MoteAPIResponseType mart = MoteAPIResponseType.OK;
        if (response.isSuccessful()){
            MoteAPIResponse mar = response.body();

            // Status should be 1 or 0 for on and off
            if ((mar.getStatus() == 1) || (mar.getStatus() == 0))  {
                mart = MoteAPIResponseType.OK;
            } else {
                mart = MoteAPIResponseType.VALIDATION_ERROR;
            }

            // The colour string can be tested by running it through the Android Color parser. If
            // it is not a valid colour, an exception will be thrown.
            try {
                int testColour = Color.parseColor("#" + mar.getColour());
            } catch (IllegalArgumentException iae) {
                mart = MoteAPIResponseType.VALIDATION_ERROR;
            }
        }
        return mart;
    }

    // Call the Mote API to get the current state of the Mote as known by the host device
    public void updateMoteStatus(){

        // Initialise the API if required
        if (moteAPI == null) {
            moteAPI = getAPIInstance();
        }

        // This is where the API actually gets called.
        // Note: Using Enqueue means this is an asynchronous call, and not handled on the UI thread.
        final Call<MoteAPIResponse> call = moteAPI.getMoteStatus();
        call.enqueue(new Callback<MoteAPIResponse>() {
            @Override
            public void onResponse(Call<MoteAPIResponse> call, Response<MoteAPIResponse> response) {
                handleResponse(response);
            }

            @Override
            public void onFailure(Call<MoteAPIResponse> call, Throwable t) {
                handleFailure(call, t);
            }
        });
    }

    // Call the Mote API to turn the Mote on or off
    public void setMoteState(boolean newState){

        // Initialise the API if required
        if (moteAPI == null) {
            moteAPI = getAPIInstance();
        }

        // Call different API methods for turning on and off
        Call<MoteAPIResponse> call;
        if (newState == true) {
            call = moteAPI.setMoteOn();
        } else {
            call = moteAPI.setMoteOff();
        }
        call.enqueue(new Callback<MoteAPIResponse>() {
            @Override
            public void onResponse(Call<MoteAPIResponse> call, Response<MoteAPIResponse> response) {
                handleResponse(response);
            }

            @Override
            public void onFailure(Call<MoteAPIResponse> call, Throwable t) {
                handleFailure(call, t);
            }
        });
    }

    // Call the Mote API to set the colour of the Mote
    public void setMoteColour(){

        // Initialise the API if required
        if (moteAPI == null) {
            moteAPI = getAPIInstance();
        }

        // Android stores colour as ints, but the API expects RGB values in the form RRGGBB, so we
        // need to convert before calling the API
        String strColour = String.format("%06X", (0xFFFFFF & colour));

        // This is where the API actually gets called.
        // Note: Using Enqueue means this is an asynchronous call, and not handled on the UI thread.
        final Call<MoteAPIResponse> call = moteAPI.setMoteColour(strColour);
        call.enqueue(new Callback<MoteAPIResponse>() {
            @Override
            public void onResponse(Call<MoteAPIResponse> call, Response<MoteAPIResponse> response) {
                handleResponse(response);
            }

            @Override
            public void onFailure(Call<MoteAPIResponse> call, Throwable t) {
                handleFailure(call, t);
            }
        });
    }


    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    public MoteMode getMode() {
        return mode;
    }

    public void setMode(MoteMode mode) {
        this.mode = mode;
    }

    public int getColour() {
        return colour;
    }

    public void setColour(int colour) {
        this.colour = colour;
    }
}
