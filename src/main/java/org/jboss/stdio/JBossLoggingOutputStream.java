/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.stdio;

import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;

/**
 * A convenience {@link java.io.OutputStream} which writes to a {@link JBossLoggingWriter}.
 */
public final class JBossLoggingOutputStream extends WriterOutputStream {

    /**
     * Construct a new instance.
     *
     * @param category the log category to use
     * @param level the level at which to log messages
     */
    public JBossLoggingOutputStream(final String category, final Level level) {
        this(Logger.getLogger(category), level);
    }

    /**
     * Construct a new instance.
     *
     * @param log the logger to use
     * @param level the level at which to log messages
     */
    public JBossLoggingOutputStream(final Logger log, final Level level) {
        super(new JBossLoggingWriter(log, level));
    }
}
