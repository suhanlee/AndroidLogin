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

package com.devsh.androidlogin.movie;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CommentService {

    @POST("/android/comment.json")
    @FormUrlEncoded
    Call<CommentServiceResponse> postComment(@Field("api_token") String api_token,
                           @Field("comment[movie_id]") String movieId,
                           @Field("comment[message]") String message);

    @DELETE("/android/comment/{commentId}.json")
    Call<CommentServiceResponse> deleteComment(@Path("commentId") String commentId, @Query("api_token") String api_token);

}
