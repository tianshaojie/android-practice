// IWebAidlInterface.aidl
package com.yuzhi.fine.library.web;

// Declare any non-default types here with import statements

interface IWebAidlInterface {
    // data
    String getToken();
    boolean isLogin();
    long getUserId();

    // ui
    void invokeShare();
}
