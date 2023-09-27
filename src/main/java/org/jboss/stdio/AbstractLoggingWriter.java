/*
 * Copyright The JBoss Logging STDIO Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.stdio;

import java.io.Writer;
import java.io.IOException;

/**
 * Abstract base class for writers which log to a logger.
 */
public abstract class AbstractLoggingWriter extends Writer {

    private final StringBuilder buffer = new StringBuilder();

    /**
     * Construct a new instance.
     */
    protected AbstractLoggingWriter() {
    }

    /** {@inheritDoc} */
    @Override
    public void write(final int c) throws IOException {
        final java.util.logging.Logger logger = getLogger();
        if (logger == null) {
            return;
        }
        synchronized (buffer) {
            if (c == '\n') {
                logger.log(getLevel(), buffer.toString());
                buffer.setLength(0);
            } else {
                buffer.append((char) c);
            }
        }
    }

    /** {@inheritDoc} */
    public void write(final char[] cbuf, final int off, final int len) throws IOException {
        final java.util.logging.Logger logger = getLogger();
        if (logger == null) {
            return;
        }
        synchronized (buffer) {
            int mark = 0;
            int i;
            for (i = 0; i < len; i++) {
                final char c = cbuf[off + i];
                if (c == '\n') {
                    buffer.append(cbuf, mark + off, i - mark);
                    logger.log(getLevel(), buffer.toString());
                    buffer.setLength(0);
                    mark = i + 1;
                }
            }
            buffer.append(cbuf, mark + off, i - mark);
        }
    }

    /** {@inheritDoc} */
    public void flush() throws IOException {
        // ignore
    }

    /**
     * Get the logger to use.
     *
     * @return the logger
     */
    protected abstract java.util.logging.Logger getLogger();

    /**
     * Get the level at which to log.
     *
     * @return the level
     */
    protected abstract java.util.logging.Level getLevel();

    /** {@inheritDoc} */
    public void close() throws IOException {
        // ignore
    }
}
