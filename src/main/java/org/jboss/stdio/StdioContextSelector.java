/*
 * Copyright The JBoss Logging STDIO Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.stdio;

/**
 * A mechanism for determining what the current standard I/O context is.  All I/O to {@link System#in}, {@link System#out},
 * and {@link System#err} are handled by the current standard I/O context which is returned by the active instance
 * of this interface.
 *
 * @see org.jboss.stdio.StdioContext#setStdioContextSelector(StdioContextSelector)
 */
public interface StdioContextSelector {

    /**
     * Get the current stdio context.  Must not return {@code null}.
     *
     * @return the current stdio context
     */
    StdioContext getStdioContext();
}
