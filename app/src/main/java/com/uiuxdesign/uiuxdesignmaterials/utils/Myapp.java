package com.uiuxdesign.uiuxdesignmaterials.utils;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class Myapp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration configuration = new RealmConfiguration.Builder()
                .name("my.db")
                .schemaVersion(0)
                .build();
        Realm.setDefaultConfiguration(configuration);
    }
}
