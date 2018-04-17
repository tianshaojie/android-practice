// IWebAidlInterface.aidl
package com.android.fine.library.web;

// Declare any non-default types here with import statements

interface IWebAidlInterface {
    // data
    String getToken();
    boolean isLogin();
    long getUserId();

    // ui
    void invokeShare();
}
