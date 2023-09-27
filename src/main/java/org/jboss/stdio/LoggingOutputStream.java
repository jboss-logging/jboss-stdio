/*
 * Copyright The JBoss Logging STDIO Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.stdio;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A convenience {@code OutputStream} which writes to a {@code LoggingWriter}.
 */
public final class LoggingOutputStream extends WriterOutputStream {
    /**
     * Construct a new instance.
     *
     * @param category the log category to use
     * @param levelName the name of the level at which to log messages
     */
    public LoggingOutputStream(final String category, final String levelName) {
        this(category, Level.parse(levelName));
    }

    /**
     * Construct a new instance.
     *
     * @param category the log category to use
     * @param level the level at which to log messages
     */
    public LoggingOutputStream(final String category, final Level level) {
        this(Logger.getLogger(category), level);
    }

    /**
     * Construct a new instance.
     *
     * @param log the logger to use
     * @param level the level at which to log messages
     */
    public LoggingOutputStream(final Logger log, final Level level) {
        super(new LoggingWriter(log, level));
    }
}
