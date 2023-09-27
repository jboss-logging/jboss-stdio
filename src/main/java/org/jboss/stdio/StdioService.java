/*
 * Copyright The JBoss Logging STDIO Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.stdio;

/**
 * An object which exposes the {@code StdioContext} installation methods as a service.
 */
public final class StdioService {

    /**
     * Start the stdio service.
     */
    public void start() {
        StdioContext.install();
    }

    /**
     * Stop the stdio service.
     */
    public void stop() {
        StdioContext.uninstall();
    }
}
