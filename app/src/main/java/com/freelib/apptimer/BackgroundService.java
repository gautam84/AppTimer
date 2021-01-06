package com.freelib.apptimer;

import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.freelib.apptimer.MainActivity.FACEBOOK_COUNTER;
import static com.freelib.apptimer.MainActivity.WHATSAPP_COUNTER;

public class BackgroundService extends Service {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public BackgroundService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sharedPreferences = getSharedPreferences("App Duration", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        TimerTask detectApp = new TimerTask() {
            @Override
            public void run() {
                sharedPreferences = getSharedPreferences("App Duration", MODE_PRIVATE);
                editor = sharedPreferences.edit();
                UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(USAGE_STATS_SERVICE);
                long endTime = System.currentTimeMillis();
                long beginTime = endTime / (1000);
                List<UsageStats> usageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, beginTime, endTime);
                if (usageStats != null) {
                    for (UsageStats usageStat : usageStats) {
                        if (usageStat.getPackageName().toLowerCase().contains("com.whatsapp")) {
                            editor.putLong(WHATSAPP_COUNTER, usageStat.getTotalTimeInForeground());
                        }
                        if (usageStat.getPackageName().toLowerCase().contains("com.facebook.katana")) {
                            editor.putLong(FACEBOOK_COUNTER, usageStat.getTotalTimeInForeground());
                        }
                        editor.apply();
                    }
                }

            }
        };
        Timer detectAppTimer = new Timer();
        detectAppTimer.scheduleAtFixedRate(detectApp, 0, 1000);
        return super.onStartCommand(intent, flags, startId);

    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}