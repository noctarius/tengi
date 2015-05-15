/*
 * Copyright (c) 2015, Christoph Engelbert (aka noctarius) and
 * contributors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.noctarius.tengi.core.impl;

import java.io.InputStream;
import java.util.Properties;

public final class VersionUtil {

    /**
     * Build version of this tengi release
     */
    public static final String VERSION;

    /**
     * Build date of this tengi release
     */
    public static final String BUILD_DATE;

    // Property keys from build information file
    private static final String TENGI_VERSION_PROPERTY = "tengi-version";
    private static final String TENGI_BUILD_DATE_PROPERTY = "tengi-build-date";

    // File name for properties file containing build information
    private static final String TENGI_VERSION_FILE = "tengi-version.properties";

    static {
        String version = "Unknown version";
        String buildDate = "Unknown build-date";
        try {
            ClassLoader classLoader = VersionUtil.class.getClassLoader();
            InputStream versionFile = classLoader.getResourceAsStream(TENGI_VERSION_FILE);
            Properties properties = new Properties();
            properties.load(versionFile);

            version = properties.getProperty(TENGI_VERSION_PROPERTY);
            buildDate = properties.getProperty(TENGI_BUILD_DATE_PROPERTY);
        } catch (Exception e) {
            // We really want to ignore this, should never fail but if it does
            // there is no reason to prevent startup!
        }
        VERSION = version;
        BUILD_DATE = buildDate;
    }

    private VersionUtil() {
    }

}
