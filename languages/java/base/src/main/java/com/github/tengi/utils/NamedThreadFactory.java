package com.github.tengi.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory
    implements ThreadFactory
{

    private final String prefix;

    private final AtomicInteger count = new AtomicInteger();

    public NamedThreadFactory( String prefix )
    {
        this.prefix = prefix;
    }

    @Override
    public Thread newThread( Runnable r )
    {
        return new Thread( r, prefix + "-Thread-" + ( count.incrementAndGet() ) );
    }

}
