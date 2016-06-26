package com.devsh.androidlogin.library.server;

public interface ServerLoginResultCallback {
    void onSuccess(String apiToken);
    void onFail(String error);
}

