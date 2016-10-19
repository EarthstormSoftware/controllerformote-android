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
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

/*
 This Fragment shows a clickable list of the various componentments included in the app, and when
 clicked, starts a new activity to show the relevant legal information for that component.
 */
public class LegalInfoFragment extends ListFragment implements AdapterView.OnItemClickListener {

    public LegalInfoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_legal_info, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // The list is populated from an array specified as a resource.
        ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(), R.array.Licenses, android.R.layout.simple_list_item_1);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {

        // The actions here are defined in the ShowLegalNoticeActivity definition in the
        // manifest, and are used by that to determine which notice should be shown.
        Intent intent = new Intent(getActivity(), ShowLegalNoticeActivity.class);
        String[] licenses = getResources().getStringArray(R.array.Licenses);
        switch (licenses[position]) {
            case "Controller for Mote":
                intent.setAction("com.earthstormsoftware.motecontrol.MC_LEGALNOTICE_ACTION");
                break;
            case "GSON":
                intent.setAction("com.earthstormsoftware.motecontrol.GSON_LEGALNOTICE_ACTION");
                break;
            case "Retrofit":
                intent.setAction("com.earthstormsoftware.motecontrol.RETROFIT_LEGALNOTICE_ACTION");
                break;
            case "ColorPicker":
                intent.setAction("com.earthstormsoftware.motecontrol.COLORPICKER_LEGALNOTICE_ACTION");
                break;
        }
        startActivity(intent);
    }
}
