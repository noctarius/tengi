package com.noctarius.tengi.server.server;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

class EventManager implements Service {

    private final ScheduledExecutorService eventExecutor;

    EventManager() {
        eventExecutor = Executors.newScheduledThreadPool(5);
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {
        eventExecutor.shutdown();
    }
}
