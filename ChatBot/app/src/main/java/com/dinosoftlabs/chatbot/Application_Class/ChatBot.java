package com.dinosoftlabs.chatbot.Application_Class;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by AQEEL on 11/19/2018.
 */

public class ChatBot extends Application {


    // user to save all chat offline
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

}
