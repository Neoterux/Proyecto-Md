package com.neoterux.server.api.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public final class ThreadFactoryBuilder implements ThreadFactory {
    
    private final String pattern;
    private final AtomicInteger counter;
    
    public ThreadFactoryBuilder (String pattern) {
        this.pattern = pattern;
        this.counter = new AtomicInteger();
    }
    
    @Override
    public Thread newThread (Runnable r) {
        return new Thread(r, String.format(pattern, counter.incrementAndGet()));
    }
}
