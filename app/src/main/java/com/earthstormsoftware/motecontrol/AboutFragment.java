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

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class AboutFragment extends Fragment {

    public AboutFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_about, container, false);
        TextView tvAppVersion = (TextView) rootView.findViewById(R.id.txtAppVersion);
        TextView tvAppBuild = (TextView) rootView.findViewById(R.id.txtAppBuild);
        TextView tvVersionCode = (TextView) rootView.findViewById(R.id.txtVersionCode);

        // Extract the version name from the package. This combines the version and build datetime
        try {
            PackageInfo pInfo =  getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            String versionName = pInfo.versionName;
            tvAppBuild.setText(versionName);

            // versionName is defined in the Gradle build file to be the version combined with the
            // build datetime, separated bz a '-'character. Extract the version for nice display.
            int vEnd = versionName.indexOf("-");
            if (vEnd != -1) {
                tvAppVersion.setText(versionName.substring(0,vEnd));
            } else {
                tvAppVersion.setText(versionName);
            }

            // Extract the version code. This is how Google Play determines if an app needs to be updated
            // so it is useful to see what the current version is.
            int iVersionCode = pInfo.versionCode;
            tvVersionCode.setText(getString(R.string.lblVersionCode) + ": " + Integer.toString(iVersionCode));

        } catch (PackageManager.NameNotFoundException e) {
            tvAppVersion.setText("Version not known");
            tvAppBuild.setText("Build not known");
            tvVersionCode.setText("VersionCode not known");
        }
        return rootView;
    }
}
