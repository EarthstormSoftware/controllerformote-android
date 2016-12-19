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

package com.earthstormsoftware.motecontrol.api;

import com.google.gson.annotations.SerializedName;

/*
 When Retrofit receives the response to the API call, it can parse the response, and map it to an
 object. In our case, the response is in JSON so we use GSON to convert that to a usable object,
 defined here.
 */
public class MoteAPIResponseV0 {
    @SerializedName("status")
    public int status;

    @SerializedName("colour")
    public String colour;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }
}
