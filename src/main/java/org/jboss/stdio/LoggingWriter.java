/*
 * Copyright The JBoss Logging STDIO Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.stdio;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A writer which sends its data to a logger.
 */
public final class LoggingWriter extends AbstractLoggingWriter {

    @SuppressWarnings({ "NonConstantLogger" })
    private final Logger log;
    private final Level level;

    /**
     * Construct a new instance.
     *
     * @param category  the log category to use
     * @param levelName the name of the level at which to log messages
     */
    public LoggingWriter(final String category, final String levelName) {
        this(category, Level.parse(levelName));
    }

    /**
     * Construct a new instance.
     *
     * @param category the log category to use
     * @param level    the level at which to log messages
     */
    public LoggingWriter(final String category, final Level level) {
        this(Logger.getLogger(category), level);
    }

    /**
     * Construct a new instance.
     *
     * @param log   the logger to use
     * @param level the level at which to log messages
     */
    public LoggingWriter(final Logger log, final Level level) {
        this.log = log;
        this.level = level;
    }

    /** {@inheritDoc} */
    protected Logger getLogger() {
        return log;
    }

    /** {@inheritDoc} */
    protected Level getLevel() {
        return level;
    }
}
