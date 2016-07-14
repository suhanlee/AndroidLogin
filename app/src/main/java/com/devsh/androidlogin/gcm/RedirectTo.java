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

public class RedirectTo {
    public String movie_id;
    public String comment_id;

    public RedirectTo(String movie_id, String commnet_id) {
        this.movie_id = movie_id;
        this.comment_id = commnet_id;
    }

    public String getMovieId() {
        return movie_id;
    }

    public String getCommentId() {
        return comment_id;
    }
}
