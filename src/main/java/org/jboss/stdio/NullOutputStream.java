/*
 * Copyright The JBoss Logging STDIO Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.stdio;

import java.io.OutputStream;

/**
 * A null output stream, which ignores all output.
 */
public final class NullOutputStream extends OutputStream {

    private static final NullOutputStream INSTANCE = new NullOutputStream();

    /**
     * Get the singleton instance.
     *
     * @return the instance
     */
    public static NullOutputStream getInstance() {
        return INSTANCE;
    }

    /**
     * Ignore the written byte.
     *
     * @param b ignored
     */
    public void write(final int b) {
        // nothing
    }

    /**
     * Ignore the written bytes.
     *
     * @param b ignored
     */
    public void write(final byte[] b) {
        // nothing
    }

    /**
     * Ignore the written bytes.
     *
     * @param b   ignored
     * @param off ignored
     * @param len ignored
     */
    public void write(final byte[] b, final int off, final int len) {
        // nothing
    }
}
