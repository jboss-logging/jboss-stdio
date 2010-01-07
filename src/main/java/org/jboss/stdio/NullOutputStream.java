/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
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
     * @param b ignored
     * @param off ignored
     * @param len ignored
     */
    public void write(final byte[] b, final int off, final int len) {
        // nothing
    }
}
