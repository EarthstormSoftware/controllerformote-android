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

import android.os.Parcel;
import android.os.Parcelable;

/*
 * This class encapsulates a single Mote unit - the current state as known by the application, the
 * the API calls required to change the state on the device, validation of responses to the API calls,
 * and update of app state once a valid response is received.
 */
public class Mote implements Parcelable {

    // Initially only the URI, on and colour fields are actually used, the additional fields have
    // been added in anticipation of future improvements
    private String uri = "";
    private String id = "";
    private String name = "";
    private boolean on = false;
    private MoteMode mode;
    private int colour;

    public Mote(String moteURI, String moteID, String moteName, boolean moteOn, MoteMode moteMode) {
        this.uri = moteURI;
        this.id = moteID;
        this.name = moteName;
        this.on = moteOn;
        this.mode = moteMode;
    }

    public String getUri() {
        return uri;
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

    public int getColour() {
        return colour;
    }

    public void setColour(int colour) {
        this.colour = colour;
    }

    protected Mote(Parcel in) {
        uri = in.readString();
        id = in.readString();
        name = in.readString();
        on = in.readByte() != 0x00;
        mode = MoteMode.valueOf(in.readString());
        colour = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uri);
        dest.writeString(id);
        dest.writeString(name);
        dest.writeByte((byte) (on ? 0x01 : 0x00));
        dest.writeString(mode.name());
        dest.writeInt(colour);
    }

    public static final Parcelable.Creator<Mote> CREATOR = new Parcelable.Creator<Mote>() {
        @Override
        public Mote createFromParcel(Parcel in) {
            return new Mote(in);
        }

        @Override
        public Mote[] newArray(int size) {
            return new Mote[size];
        }
    };
}
