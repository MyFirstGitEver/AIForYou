package com.example.aiforyou;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.cloudinary.android.MediaManager;

import java.util.HashMap;
import java.util.Map;

//doa0jsihz
public class RunOnceCode extends Application
{
    public RunOnceCode()
    {

    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "doa0jsihz");
        MediaManager.init(this, config);

        createNotificationChannel();
    }

    private void createNotificationChannel() {
        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(getString(R.string.notification_channel), name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}