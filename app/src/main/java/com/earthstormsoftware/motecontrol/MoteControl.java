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

import android.app.Application;
import android.content.Context;

/*
 * There has been some debate whether an Application class should be used or not, since it is a
 * Singleton and therefore can easily be misused and abused. There is an old thread which discusses
 * this here: http://stackoverflow.com/questions/3826905/singletons-vs-application-context-in-android
 *
 * The conclusion seems to be 'handle with care' and as it provides a nice way of making the
 */
public class MoteControl extends Application {

    // Application context - valid for the entire time the app is alive
    private static Context context;

    // Tag for Logging
    public static String TAG = "MOTECTRL";

    // Intent definitions for BroadcastReceivers
    public static String MOTE_API_RESPONSE = "com.earthstormsoftware.motecontrol.MOTE_API_RESPONSE";

    // When the application is started, get the application Context and store it.
    public void onCreate() {
        super.onCreate();
        MoteControl.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MoteControl.context;
    }
}
