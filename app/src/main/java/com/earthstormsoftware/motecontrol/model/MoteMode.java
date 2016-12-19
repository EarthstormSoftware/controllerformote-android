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

package com.earthstormsoftware.motecontrol.model;

import com.earthstormsoftware.motecontrol.MoteControl;
import com.earthstormsoftware.motecontrol.R;

public enum MoteMode {
    COLOUR(R.string.mote_mode_colour),
    RAINBOW(R.string.mote_mode_rainbow),
    FIRE(R.string.mote_mode_fire),
    WATER(R.string.mote_mode_water),
    UNKNOWN(R.string.mote_mode_unknown);

    private int resId = -1;

    MoteMode(int i) {
        this.resId = i;
    }

    public String toString() {
        return MoteControl.getAppContext().getString(resId);
    }
}
