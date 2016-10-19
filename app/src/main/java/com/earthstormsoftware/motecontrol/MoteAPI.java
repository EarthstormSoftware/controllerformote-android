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

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/*
 This Interface is used bz Retrofit to map the Mote HTTP interface to a function call. The API
  endpoints are defined here, and Retrofit enables them to easily be called from code.
 */

public interface MoteAPI {
    @GET("/mote/api/v1.0/status")
    Call<MoteStatus> getMoteStatus();

    @GET("/mote/api/v1.0/on")
    Call<MoteStatus> setMoteOn();

    @GET("/mote/api/v1.0/off")
    Call<MoteStatus> setMoteOff();

    @GET("/mote/api/v1.0/set/{colour}")
    Call<MoteStatus> setMoteColour(@Path("colour") String colour);
}
