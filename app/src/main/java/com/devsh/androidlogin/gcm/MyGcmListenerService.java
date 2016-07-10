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

package com.devsh.androidlogin.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;

import com.devsh.androidlogin.MainActivity;
import com.devsh.androidlogin.R;
import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.Gson;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";
    private static String SOCIAL_TYPE="social";
    private static String NOTIFICATION_TYPE = "notification";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs,
     *             For Set of keys use data.KeySet().
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String type = data.getString("type");
        String message = data.getString("message");
        String redirectTo = data.getString("redirect_to");

        Gson gson = new Gson();
        RedirectTo rt = gson.fromJson(redirectTo, RedirectTo.class);

        Log.d(TAG, "token type: " + type);        // Sender-Id
        Log.d(TAG, "token message: " + message);

//        if (type != null && type.equals(SOCIAL_TYPE)) {
//            String title = data.getString("title");
//            String description = data.getString("description");
//            String image_url = data.getString("image_url");
//            String redirect_url = data.getString("redirect_url");
//
//            Log.d(TAG, "token title : " + title);
//            Log.d(TAG, "token description : " + description);
//            Log.d(TAG, "token image_url : " + image_url);
//            Log.d(TAG, "token redirect_url : " + redirect_url);
//
//        }
        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        sendNotification2(message);
    }

    private void sendNotification2(String message) {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        // 작은 아이콘 이미지.
        builder.setSmallIcon(R.mipmap.ic_launcher);

        // 알림이 출력될 때 상단에 나오는 문구.
        builder.setTicker("미리보기 입니다.");

        // 알림 출력 시간.
        builder.setWhen(System.currentTimeMillis());

        // 알림 제목.
        builder.setContentTitle(getString(R.string.app_name));

//        // 프로그래스 바.
//        builder.setProgress(100, 50, false);

        // 알림 내용.
        builder.setContentText(message);

        // 알림시 사운드, 진동, 불빛을 설정 가능.
        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS);

        // 알림 터치시 반응.
        builder.setContentIntent(pendingIntent);

        // 알림 터치시 반응 후 알림 삭제 여부.
        builder.setAutoCancel(true);

        // 우선순위.
        builder.setPriority(Notification.PRIORITY_MAX);

//        builder.setVisibility(Notification.VISIBILITY_PUBLIC);

        // 행동 최대3개 등록 가능.
//        builder.addAction(R.mipmap.ic_launcher, "Show", pendingIntent);
//        builder.addAction(R.mipmap.ic_launcher, "Hide", pendingIntent);
//        builder.addAction(R.mipmap.ic_launcher, "Remove", pendingIntent);

        // 고유ID로 알림을 생성.
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(123456, builder.build());
    }
    /**
     * Create and show a simple notification containing the received GCM messages.
     * @param message GCM message received.
     */
    private void sendNotification(String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("GCM Message")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setVisibility(View.VISIBLE)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }
}
