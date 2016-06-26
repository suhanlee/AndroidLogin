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

package com.devsh.androidlogin.feed.model;

import java.util.List;

public class FeedItem {
    private String title;
    private String movie_url;
    private String thumb_movie_url;
    private String movie_content_type;
    private Author author;
    private Feedback feedback;
    private List<Comment> comments;

    public String getTitle() {
        return title;
    }

    public String getMovie_url() {
        return movie_url;
    }

    public String getThumb_movie_url() {
        return thumb_movie_url;
    }

    public String getMovie_content_type() {
        return movie_content_type;
    }

    public Author getAuthor() {
        return author;
    }

    public Feedback getFeedback() {
        return feedback;
    }

    public List<Comment> getComments() {
        return comments;
    }
}
