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

    private String provider;
    private String token;
    private String uid;
    private String user_name;
    private String user_email;
    private String user_photo;
    private String registration_token;
    private String version_name;
    private String version_code;

    // getter

    public String getProvider() {
        return provider;
    }

    public String getToken() {
        return token;
    }

    public String getUid() {
        return uid;
    }

    public String getUserName() {
        return user_name;
    }

    public String getUserEmail() {
        return user_email;
    }

    public String getUserPhoto() {
        return user_photo;
    }

    public String getRegistrationToken() {
        return registration_token;
    }

    public String getVersionName() {
        return version_name;
    }

    public String getVersionCode() {
        return version_code;
    }

    // setter

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setUserName(String user_name) {
        this.user_name = user_name;
    }

    public void setUserEmail(String user_email) {
        this.user_email = user_email;
    }

    public void setUserPhoto(String user_photo) {
        this.user_photo = user_photo;
    }

    public void setRegistrationToken(String registration_token) {
        this.registration_token = registration_token;
    }

    public void setVersionName(String version_name) {
        this.version_name = version_name;
    }

    public void setVersionCode(String version_code) {
        this.version_code = version_code;
    }
}


