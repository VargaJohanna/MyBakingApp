package com.movies.mybakingapp.utilities;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutors {
    private static final Object LOCK = new Object();
    private static AppExecutors sInstance;
    private final Executor networkIO;
    private final Executor diskIO;

    private AppExecutors(Executor networkIO, Executor diskIO) {
        this.networkIO = networkIO;
        this.diskIO = diskIO;
    }

    public static AppExecutors getInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new AppExecutors(Executors.newFixedThreadPool(3),
                        Executors.newSingleThreadExecutor());
            }
        }
        return sInstance;
    }

    public Executor networkIO() {
        return networkIO;
    }
    public Executor diskIO() {
        return diskIO;
    }
}
