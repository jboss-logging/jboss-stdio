/*
 * Copyright The JBoss Logging STDIO Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.stdio;

import java.io.InputStream;

/**
 * An input stream that is always in an EOF condition.
 */
public final class NullInputStream extends InputStream {

    private static final NullInputStream INSTANCE = new NullInputStream();

    /**
     * Get the singleton instance.
     *
     * @return the null input stream instance
     */
    public static NullInputStream getInstance() {
        return INSTANCE;
    }

    /**
     * Read a byte. Always returns EOF.
     *
     * @return -1 always
     */
    public int read() {
        return -1;
    }
}
