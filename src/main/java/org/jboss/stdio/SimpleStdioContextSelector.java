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

/**
 * A simple {@code org.jboss.stdio.StdioContextSelector} implementation which always selects the same context.
 */
public final class SimpleStdioContextSelector implements StdioContextSelector {
    private final StdioContext context;

    /**
     * Construct a new instance.
     *
     * @param context the context to use (must not be {@code null})
     */
    public SimpleStdioContextSelector(final StdioContext context) {
        if (context == null) {
            throw new NullPointerException("context is null");
        }
        this.context = context;
    }

    /** {@inheritDoc} */
    public StdioContext getStdioContext() {
        return context;
    }
}
