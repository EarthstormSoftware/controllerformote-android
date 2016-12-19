package com.earthstormsoftware.motecontrol.api;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.graphics.Color;

import com.earthstormsoftware.motecontrol.MoteControl;
import com.earthstormsoftware.motecontrol.model.Mote;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MoteAPIService extends IntentService {

    private static final String ACTION_GET_STATE = "com.earthstormsoftware.motecontrol.api.action.GET_STATE";
    private static final String ACTION_SET_STATE = "com.earthstormsoftware.motecontrol.api.action.SET_STATE";
    private static final String ACTION_SET_COLOUR = "com.earthstormsoftware.motecontrol.api.action.SET_COLOUR";

    private static final String EXTRA_MOTE = "com.earthstormsoftware.motecontrol.api.extra.MOTE";
    private static final String EXTRA_STATE = "com.earthstormsoftware.motecontrol.api.extra.STATE";
    private static final String EXTRA_COLOUR = "com.earthstormsoftware.motecontrol.api.extra.COLOUR";

    public MoteAPIService() {
        super("MoteAPIService");
    }

    /**
     * Starts the service to call the Mote API to get the current state. If
     * the service is already performing a task this action will be queued.
     */
    public static void startActionGetState(Context context, Mote mote) {
        Intent intent = new Intent(context, MoteAPIService.class);
        intent.setAction(ACTION_GET_STATE);
        intent.putExtra(EXTRA_MOTE, mote);
        context.startService(intent);
    }

    /**
     * Starts the service to call the Mote API to set the current state. If
     * the service is already performing a task this action will be queued.
     */
    public static void startActionSetState(Context context, Mote mote, boolean newState) {
        Intent intent = new Intent(context, MoteAPIService.class);
        intent.setAction(ACTION_SET_STATE);
        intent.putExtra(EXTRA_MOTE, mote);
        intent.putExtra(EXTRA_STATE, newState);
        context.startService(intent);
    }

    /**
     * Starts the service to call the Mote API to change the current colour. If
     * the service is already performing a task this action will be queued.
     */
    public static void startActionSetColour(Context context, Mote mote, int newColour) {
        Intent intent = new Intent(context, MoteAPIService.class);
        intent.setAction(ACTION_SET_COLOUR);
        intent.putExtra(EXTRA_MOTE, mote);
        intent.putExtra(EXTRA_COLOUR, newColour);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GET_STATE.equals(action)) {
                final Mote mote = intent.getParcelableExtra(EXTRA_MOTE);
                handleActionGetState(mote);
            } else if (ACTION_SET_STATE.equals(action)) {
                final Mote mote = intent.getParcelableExtra(EXTRA_MOTE);
                final boolean newState = intent.getBooleanExtra(EXTRA_STATE, false);
                handleActionSetState(mote, newState);
            } else if (ACTION_SET_COLOUR.equals(action)) {
                final Mote mote = intent.getParcelableExtra(EXTRA_MOTE);
                final int newColour = intent.getIntExtra(EXTRA_COLOUR, 0);
                handleActionSetColour(mote, newColour);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionGetState(Mote mote) {
        MoteAPIV0 moteAPI = getAPIInstance(mote);


        // This is where the API actually gets called.
        // Note: Using Enqueue means this is an asynchronous call, and not handled on the UI thread.
        final Call<MoteAPIResponseV0> call = moteAPI.getMoteStatus();
        call.enqueue(new Callback<MoteAPIResponseV0>() {
            @Override
            public void onResponse(Call<MoteAPIResponseV0> call, Response<MoteAPIResponseV0> response) {
                handleResponse(response);
            }

            @Override
            public void onFailure(Call<MoteAPIResponseV0> call, Throwable t) {
                handleFailure(call, t);
            }
        });
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionSetState(Mote mote, boolean newState) {

        MoteAPIV0 moteAPI = getAPIInstance(mote);

        // Call different API methods for turning on and off
        Call<MoteAPIResponseV0> call;
        if (newState) {
            call = moteAPI.setMoteOn();
        } else {
            call = moteAPI.setMoteOff();
        }
        call.enqueue(new Callback<MoteAPIResponseV0>() {
            @Override
            public void onResponse(Call<MoteAPIResponseV0> call, Response<MoteAPIResponseV0> response) {
                handleResponse(response);
            }

            @Override
            public void onFailure(Call<MoteAPIResponseV0> call, Throwable t) {
                handleFailure(call, t);
            }
        });
    }

    private void handleActionSetColour(Mote mote, int newColour) {
        // Initialise the API if required
        MoteAPIV0 moteAPI = getAPIInstance(mote);

        // Android stores colour as ints, but the API expects RGB values in the form RRGGBB, so we
        // need to convert before calling the API
        String strColour = String.format("%06X", (0xFFFFFF & newColour));

        // This is where the API actually gets called.
        // Note: Using Enqueue means this is an asynchronous call, and not handled on the UI thread.
        final Call<MoteAPIResponseV0> call = moteAPI.setMoteColour(strColour);
        call.enqueue(new Callback<MoteAPIResponseV0>() {
            @Override
            public void onResponse(Call<MoteAPIResponseV0> call, Response<MoteAPIResponseV0> response) {
                handleResponse(response);
            }

            @Override
            public void onFailure(Call<MoteAPIResponseV0> call, Throwable t) {
                handleFailure(call, t);
            }
        });
    }

    // Common initialisation for Retrofit for each API call.
    private MoteAPIV0 getAPIInstance(Mote mote){


        // Uncomment to enable Retrofit logging

        //HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        //logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        //httpClient.addInterceptor(logging);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mote.getUri())
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        return retrofit.create(MoteAPIV0.class);
    }

    /*
 * This method handles 'good' responses from the API. A 'good' response is one where the HTTP
 * request completed, not that the call worked. For example, an HTTP 404 counts as a 'good'
 * respone.
 *
 * Currently the API returns the same JSON object for every call, so a common method can be
 * used. If that changes, this approach will need to be reviewed.
 */
    private void handleResponse(Response<MoteAPIResponseV0> response){

        // Create an intent for sending the response
        Intent i = new Intent(MoteControl.MOTE_API_RESPONSE);

        // Check that any response is what would be expected
        MoteAPIResponseType mrt = validateResponse(response);

        // If the response was validated, parse it into our local object
        if (mrt != MoteAPIResponseType.VALIDATION_ERROR) {

            // A 'successful' response is an HTTP 200 return code
            if (response.isSuccessful()) {

                // Update the local state
                MoteAPIResponseV0 mar = response.body();
                if (mar.getStatus() == 1) {
                    i.putExtra("state", true);
                } else {
                    i.putExtra("state", false);
                }

                int colour = Color.parseColor("#" + mar.getColour());
                i.putExtra("colour", colour);

                // Otherwise the HTTP call failed in some way.
            } else {
                mrt = MoteAPIResponseType.API_ERROR;
            }
        }
        // Send a broadcast indicating the result of the API call.
        i.putExtra("result", mrt);
        MoteControl.getAppContext().sendBroadcast(i);

    }

    /*
     * This method handles 'failure' responses from the API, where the HTTP call was not able to
     * complete (i.e. there is no HTTP response code). This includes, for example, ConnectionRefused
     * and SocketTimeout errors. These errors could be pulled out in a more granular fashion by
     * checking the Exception that was thrown.
     */
    private void handleFailure(Call<MoteAPIResponseV0> call, Throwable t){
        MoteAPIResponseType mrt = MoteAPIResponseType.IO_ERROR;
        Intent i = new Intent(MoteControl.MOTE_API_RESPONSE);
        i.putExtra("result", mrt);
        MoteControl.getAppContext().sendBroadcast(i);
    }

    // If the HTTP call completed in some way, we should check to the response to ensure it is
    // correctly formatted before trying to use it
    private MoteAPIResponseType validateResponse(Response<MoteAPIResponseV0> response){
        MoteAPIResponseType mart = MoteAPIResponseType.OK;
        if (response.isSuccessful()){
            MoteAPIResponseV0 mar = response.body();

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
}