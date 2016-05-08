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

package com.devsh.androidlogin.library.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedData {
    private static String GOOGLE_ID_TOKEN_KEY = "GOOGLE_ID_TOKEN_KEY";
    private static String GOOGLE_ID_KEY = "GOOGLE_ID_KEY";
    private static String GOOGLE_USER_NAME_KEY = "GOOGLE_USER_NAME_KEY";
    private static String GOOGLE_USER_EMAIL_KEY = "GOOGLE_USER_EMAIL_KEY";
    private static String GOOGLE_USER_PHOTO_KEY = "GOOGLE_USER_PHOTO_KEY";

    public static void putGoogleUserEmail(Context context, String userEmail) {
        putSharedPreferenceString(context, GOOGLE_USER_EMAIL_KEY, userEmail);
    }

    public static void putGoogleUserPhoto(Context context, String userPhoto) {
        putSharedPreferenceString(context, GOOGLE_USER_PHOTO_KEY, userPhoto);
    }

    public static void putGoogleUserName(Context context, String userName) {
        putSharedPreferenceString(context, GOOGLE_USER_NAME_KEY, userName);
    }

    public static String getGoogleUserName(Context context) {
        return getSharedPreferenceString(context, GOOGLE_USER_NAME_KEY);
    }

    public static void putGoogleId(Context context, String id) {
        putSharedPreferenceString(context, GOOGLE_ID_KEY, id);
    }

    public static void putGoogleIdToken(Context context, String idToken) {
        putSharedPreferenceString(context, GOOGLE_ID_TOKEN_KEY, idToken);
    }

    public static String getGoogleId(Context context) {
        return getSharedPreferenceString(context, GOOGLE_ID_KEY);
    }

    public static String getGoogleIdToken(Context context) {
        return getSharedPreferenceString(context, GOOGLE_ID_TOKEN_KEY);
    }

    private static String getSharedPreferenceString(Context context, String key) {
        return getSharedPreferenceString(context, key, null);
    }

    private static String getSharedPreferenceString(Context context, String key, String defaultReturnValue) {
        try{
            SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            return mSharedPrefs.getString(key, defaultReturnValue);
        }catch(NullPointerException e){
            e.printStackTrace();
            return defaultReturnValue;
        }
    }

    public static void putSharedPreferenceString(Context context, String key, String value){
        putSharedPreference(context, key, value);
    }

    private static void putSharedPreference(Context context, String key, Object value) {
        if (context == null) {
            return;
        }
        SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = mSharedPrefs.edit();

        if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else {
            throw new IllegalArgumentException("Not supported data type");
        }
        editor.commit();
    }

    public static void clearSharedPreference(Context context) {
        SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = mSharedPrefs.edit();
        editor.clear();
        editor.commit();
    }



}
