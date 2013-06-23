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

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.RandomBasedGenerator;

import java.util.Random;
import java.util.UUID;

public final class ConcurrentUuidUtil
{

    private static final ThreadLocal<RandomBasedGenerator> UUID_GENERATOR_CACHE = new ThreadLocal<>();

    private ConcurrentUuidUtil()
    {
    }

    public static UUID generateRandomUUID()
    {
        RandomBasedGenerator generator = UUID_GENERATOR_CACHE.get();
        if ( generator == null )
        {
            generator = Generators.randomBasedGenerator( new Random() );
            UUID_GENERATOR_CACHE.set( generator );
        }
        return generator.generate();
    }

    public static void shutdown()
    {
    }

}
