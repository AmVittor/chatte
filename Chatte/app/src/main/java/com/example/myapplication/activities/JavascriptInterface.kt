package com.example.myapplication.activities

import android.webkit.JavascriptInterface

class JavascriptInterface(val chatActivity: ChatActivity) {

    @JavascriptInterface
    public fun onPeerConnected() {
        chatActivity.onPeerConnected()
    }

}