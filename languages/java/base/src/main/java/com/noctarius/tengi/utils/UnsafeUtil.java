package com.noctarius.tengi.utils;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * Simple class to obtain access to the {@link Unsafe} object. {@link Unsafe} is required to allow efficient CAS
 * operations on arrays. Note that the versions in {@link java.util.concurrent.atomic}, such as
 * {@link java.util.concurrent.atomic.AtomicLongArray}, require extra memory ordering guarantees which are generally not
 * needed in these algorithms and are also expensive on most processors.
 */
@SuppressWarnings( "restriction" )
public class UnsafeUtil
{
    private UnsafeUtil()
    {
    } // dummy private constructor

    /**
     * Fetch the Unsafe. Use With Caution.
     */
    public static Unsafe getUnsafe()
    {
        // Not on bootclasspath
        if ( UnsafeUtil.class.getClassLoader() == null )
        {
            return Unsafe.getUnsafe();
        }
        try
        {
            final Field fld = Unsafe.class.getDeclaredField( "theUnsafe" );
            fld.setAccessible( true );
            return (Unsafe) fld.get( UnsafeUtil.class );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Could not obtain access to sun.misc.Unsafe", e );
        }
    }
}
