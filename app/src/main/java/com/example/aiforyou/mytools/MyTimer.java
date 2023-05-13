package com.example.aiforyou.mytools;

import android.os.Handler;
import android.os.Looper;

public class MyTimer {
    public interface TimerTaskManager {
        void doTask(long mils);
    }

    private final Handler handler;
    private Runnable r = null, r2;
    private final TimerTaskManager taskManager;

    private long countedMils;
    private long periodInMils;

    public MyTimer(TimerTaskManager taskManager) {
        this.handler = new Handler(Looper.getMainLooper());
        this.taskManager = taskManager;

        r = () -> {
            countedMils += periodInMils;

            try{
                this.taskManager.doTask(countedMils);
                handler.postDelayed(r, periodInMils);
            }
            catch (Exception e) {
                // Do nothing if manager is killed
            }
        };

        r2 = () -> {
            this.taskManager.doTask(countedMils);
            handler.removeCallbacks(r2);
        };
    }

    public MyTimer start(long periodInMils) {
        countedMils = 0;

        this.periodInMils = periodInMils;
        handler.postDelayed(r, periodInMils);

        return this;
    }

    public MyTimer tick(long periodInMils) {
        handler.postDelayed(r2, periodInMils);

        return this;
    }

    public boolean isTicking() {
        return countedMils != -1;
    }

    public long getProgress() {
        return countedMils;
    }

    public void stop() {
        countedMils = -1;

        handler.removeCallbacks(r);
    }
}
