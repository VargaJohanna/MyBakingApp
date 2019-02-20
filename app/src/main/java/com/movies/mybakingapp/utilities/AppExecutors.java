package com.movies.mybakingapp.utilities;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutors {
    private static final Object LOCK = new Object();
    private static AppExecutors sInstance;
    private final Executor networkIO;

    private AppExecutors(Executor networkIO) {
        this.networkIO = networkIO;
    }

    public static AppExecutors getInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new AppExecutors(Executors.newFixedThreadPool(3));
            }
        }
        return sInstance;
    }

    public Executor networkIO() {
        return networkIO;
    }
}
