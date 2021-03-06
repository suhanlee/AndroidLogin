/*
 *
 *  Copyright (C) 2016 Suhan Lee
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.devsh.androidlogin.login;
import retrofit2.http.Body;
import retrofit2.Call;
import retrofit2.http.POST;

public interface ServerLoginService {

//    @GET("/android/login")
//    Call<ServerLoginServiceResponse> login(@Query("provider") String provider,
//                                           @Query("token") String token,
//                                           @Query("uid") String uid,
//                                           @Query("userName") String username,
//                                           @Query("userEmail") String userEmail,
//                                           @Query("userPhoto") String userPhoto,
//                                           @Query("registration_token") String registrationToken,
//                                           @Query("version_name") String versionName,
//                                           @Query("version_code") String versionCode);


    @POST("/android/login")
    Call<ServerLoginServiceResponse> login(@Body ServerLoginRequest request);

}
