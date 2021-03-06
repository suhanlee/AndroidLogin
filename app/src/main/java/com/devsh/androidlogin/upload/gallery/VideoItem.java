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

package com.devsh.androidlogin.upload.gallery;

public class VideoItem {
    public static int PLUS_BUTTON = 0;
    public static int IMAGE_BUTTON = 1;

    String thumbnail;
    int type;

    public VideoItem() {
        type = IMAGE_BUTTON;
    }

    public String getThumbnailPath() {
        return thumbnail;
    }

    public void setThumbnail(String _thumbnail) {
        thumbnail = _thumbnail;
    }

    public void setType(int _type) {
        type = _type;
    }

    public boolean isImage() {
        return type == IMAGE_BUTTON;
    }

    public boolean isPlusButton() {
        return type == PLUS_BUTTON;
    }
}
