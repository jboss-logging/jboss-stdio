/*
 * Copyright The JBoss Logging STDIO Authors
 * SPDX-License-Identifier: Apache-2.0
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

    /**
     * Attempt to install this context selector as the current one.
     *
     * @see StdioContext#setStdioContextSelector(StdioContextSelector)
     */
    public void install() {
        StdioContext.setStdioContextSelector(this);
    }
}
