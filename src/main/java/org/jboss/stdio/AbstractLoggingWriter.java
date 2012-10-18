/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
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

import java.io.IOException;
import java.io.Writer;

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

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final int c) throws IOException {
        if (doWrite()) {
            synchronized (buffer) {
                if (c == '\n') {
                    log(buffer.toString());
                    buffer.setLength(0);
                } else {
                    buffer.append((char) c);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void write(final char[] cbuf, final int off, final int len) throws IOException {
        if (doWrite()) {
            synchronized (buffer) {
                int mark = 0;
                int i;
                for (i = 0; i < len; i++) {
                    final char c = cbuf[off + i];
                    if (c == '\n') {
                        buffer.append(cbuf, mark + off, i - mark);
                        log(buffer.toString());
                        buffer.setLength(0);
                        mark = i + 1;
                    }
                }
                buffer.append(cbuf, mark + off, i - mark);
            }
        }
    }

    protected abstract boolean doWrite();

    protected abstract void log(String msg);

    /**
     * {@inheritDoc}
     */
    public void flush() throws IOException {
        // ignore
    }

    /**
     * {@inheritDoc}
     */
    public void close() throws IOException {
        // ignore
    }
}
