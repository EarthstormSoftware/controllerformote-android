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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/*
 * Activity to display the relevant legal information required by the various libraries the app
 * includes to do useful stuff
 */
public class ShowLegalNoticeActivity extends AppCompatActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_showlegalnotice);
		Toolbar toolbar = (Toolbar) findViewById(R.id.showlegalnotice_toolbar);
		setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Check the intent to determine what legal information is being displayed, and set the
        // title accordingly
        Intent intent = this.getIntent();
        String noticeTitle = "";
        if (intent.getAction().equals("com.earthstormsoftware.motecontrol.MC_LEGALNOTICE_ACTION") ) {
            noticeTitle = getResources().getString(R.string.lblMoteControlLicense);
        } else if (intent.getAction().equals("com.earthstormsoftware.motecontrol.GSON_LEGALNOTICE_ACTION") ) {
            noticeTitle = getResources().getString(R.string.lblGsonLibrary);
        } else if (intent.getAction().equals("com.earthstormsoftware.motecontrol.RETROFIT_LEGALNOTICE_ACTION") ) {
            noticeTitle = getResources().getString(R.string.lblRetrofitLibrary);
        } else if (intent.getAction().equals("com.earthstormsoftware.motecontrol.COLORPICKER_LEGALNOTICE_ACTION") ) {
            noticeTitle = getResources().getString(R.string.lblColorPickerLibrary);
        }
        this.getSupportActionBar().setTitle(noticeTitle);
	}
}
