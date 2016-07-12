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

public class ServerLoginRequest {

    public String provider;
    public String token;
    public String uid;
    public String user_name;
    public String user_email;
    public String user_photo;
    public String registration_token;

    public ServerLoginRequest(String provider, String token, String uid,
                              String user_name, String user_email, String user_photo,
                              String registration_token) {
        this.provider = provider;
        this.token = token;
        this.uid = uid;
        this.user_name = user_name;
        this.user_email = user_email;
        this.user_photo = user_photo;
        this.registration_token = registration_token;
    }
}


