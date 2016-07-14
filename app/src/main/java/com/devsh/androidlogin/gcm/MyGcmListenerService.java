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
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.devsh.androidlogin.FeedActivity;
import com.devsh.androidlogin.MovieActivity;
import com.devsh.androidlogin.R;
import com.devsh.androidlogin.common.Common;
import com.google.android.gms.gcm.GcmListenerService;

import java.util.concurrent.ExecutionException;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";
    private static String SOCIAL_TYPE="social";
    private static String NOTIFICATION_TYPE = "notification";
    private int NOTIFICATION_ID = 1233;

    class PushMessage {
        String type;
        String icon_url;
        String title;
        String message;
        String redirect_to;

        public PushMessage(String type, String icon_url, String title, String message, String redirect_to) {
            this.type = type;
            this.icon_url = icon_url;
            this.title = title;
            this.message = message;
            this.redirect_to = redirect_to;
        }

        public boolean isIconUrl() {
            return icon_url == null || !icon_url.equals("");
        }
    }

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
        String icon_url = data.getString("icon_url");
        String title = data.getString("title");
        String message = data.getString("message");
        String redirectTo = data.getString("redirect_to");

        PushMessage pushMessage = new PushMessage(type, icon_url, title, message, redirectTo);

        Log.d(TAG, "token type: " + type);        // Sender-Id
        Log.d(TAG, "token message: " + message);

        Intent notificationIntent = new Intent(getApplicationContext(), FeedActivity.class);
        notificationIntent.putExtra(Common.REDIRECT_TO_KEY, redirectTo);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        sendNotificationNormal(pushMessage, pendingIntent);
    }

    private void sendNotificationNormal(PushMessage pushMessage, PendingIntent pendingIntent) {
        Notification.Builder builder = new Notification.Builder(this);

        try {
            Bitmap largeIcon = Glide
                    .with(getApplicationContext())
                    .load(pushMessage.icon_url)
                    .asBitmap()
                    .into(100, 100)
                    .get();

            if (pushMessage.isIconUrl()) {
                builder.setLargeIcon(largeIcon);
            }

            builder.setSmallIcon(R.mipmap.ic_launcher);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        // 알림이 출력될 때 상단에 나오는 문구.
        builder.setTicker(pushMessage.title);

        // 알림 출력 시간.
        builder.setWhen(System.currentTimeMillis());

        if (pushMessage.title == null) {
            builder.setContentTitle(getString(R.string.app_name));
        } else {
            builder.setContentTitle(pushMessage.title);
        }

        // 알림 내용.
        builder.setContentText(pushMessage.message);

        // 알림시 사운드, 진동, 불빛을 설정 가능.
        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS);

        // 알림 터치시 반응.
        builder.setContentIntent(pendingIntent);

        // 알림 터치시 반응 후 알림 삭제 여부.
        builder.setAutoCancel(true);

        // 우선순위.
        builder.setPriority(Notification.PRIORITY_MAX);

        // 고유ID로 알림을 생성.
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(NOTIFICATION_ID, builder.build());
    }
}
