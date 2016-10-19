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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/*
 * Fragment for displaying the actual content for the ShowLegalNoticesActivity
 */

public class ShowLegalNoticeFragment extends Fragment {

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
     */
    public ShowLegalNoticeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    // The Intent passed in will determine which license (read from a flat file) will be shown
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_showlegalnotice, container, false);
        FragmentActivity activity = this.getActivity();
        Intent intent = activity.getIntent();

        String noticeText = "";
        if (intent.getAction().equals("com.earthstormsoftware.motecontrol.MC_LEGALNOTICE_ACTION") ) {
            noticeText =  readRawTextFile(activity, R.raw.motecontrollicense);

        } else if (intent.getAction().equals("com.earthstormsoftware.motecontrol.GSON_LEGALNOTICE_ACTION") ) {
            noticeText =  readRawTextFile(activity, R.raw.gsonlicense);

        } else if (intent.getAction().equals("com.earthstormsoftware.motecontrol.RETROFIT_LEGALNOTICE_ACTION") ) {
            noticeText =  readRawTextFile(activity, R.raw.retrofitlicense);

        } else if (intent.getAction().equals("com.earthstormsoftware.motecontrol.COLORPICKER_LEGALNOTICE_ACTION") ) {
            noticeText =  readRawTextFile(activity, R.raw.colorpickerlicense);
        }

        TextView tvlegalText = (TextView) rootView.findViewById(R.id.txtLegalText);
        tvlegalText.setText(noticeText);

        return rootView;
    }

    public static String readRawTextFile(Context ctx, int resId)
    {
        InputStream inputStream = ctx.getResources().openRawResource(resId);

        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader buffreader = new BufferedReader(inputreader);
        String line;
        StringBuilder text = new StringBuilder();

        try {
            while (( line = buffreader.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
        } catch (IOException e) {
            return null;
        }
        return text.toString();
    }

}
