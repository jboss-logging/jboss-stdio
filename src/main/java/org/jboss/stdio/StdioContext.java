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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.security.Permission;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A context for console input and output.
 */
public final class StdioContext {
    private static final StdioContext SYSTEM_STDIO_CONTEXT = new StdioContext(System.in, System.out, System.err);

    private static final ThreadLocal<Boolean> entered = new ThreadLocal<Boolean>();

    private static final Permission CREATE_CONTEXT_PERMISSION = new RuntimePermission("createStdioContext", null);
    private static final Permission SET_CONTEXT_SELECTOR_PERMISSION = new RuntimePermission("setStdioContextSelector", null);
    private static final Permission INSTALL_PERMISSION = new RuntimePermission("installStdioContextSelector", null);

    private enum State {
        UNINSTALLED,
        INSTALLING,
        INSTALLED,
        UNINSTALLING,
    }

    private static final AtomicReference<State> state = new AtomicReference<State>(State.UNINSTALLED);

    private final InputStream in;
    private final PrintStream out;
    private final PrintStream err;

    StdioContext(final InputStream in, final PrintStream out, final PrintStream err) {
        this.in = in;
        this.out = out;
        this.err = err;
    }

    /**
     * Create a console I/O context.
     *
     * @param in the input stream for this context
     * @param out the output stream for this context
     * @param err the error stream for this context
     * @return the new context
     * @throws SecurityException if the caller does not have the {@code createStdioContext} {@link RuntimePermission}
     */
    public static StdioContext create(final InputStream in, final PrintStream out, final PrintStream err) throws SecurityException {
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(CREATE_CONTEXT_PERMISSION);
        }
        return new StdioContext(in, out, err);
    }

    /**
     * Create a console I/O context.  The given output streams are wrapped in {@code PrintStream} instances.
     *
     * @param in the input stream for this context
     * @param out the output stream for this context
     * @param err the error stream for this context
     * @return the new context
     * @throws SecurityException if the caller does not have the {@code createStdioContext} {@link RuntimePermission}
     */
    public static StdioContext create(final InputStream in, final OutputStream out, final OutputStream err) throws SecurityException {
        return create(in, new PrintStream(out, true), new PrintStream(err, true));
    }

    /**
     * Get the current console I/O context.
     *
     * @return the current context
     */
    public static StdioContext getStdioContext() {
        return stdioContextSelector.getStdioContext();
    }

    /**
     * Get the input stream for this context.
     *
     * @return the input stream
     */
    public InputStream getIn() {
        return in;
    }

    /**
     * Get the output stream for this context.
     *
     * @return the output stream
     */
    public PrintStream getOut() {
        return out;
    }

    /**
     * Get the error stream for this context.
     *
     * @return the error stream
     */
    public PrintStream getErr() {
        return err;
    }

    private static volatile StdioContextSelector stdioContextSelector = new SimpleStdioContextSelector(SYSTEM_STDIO_CONTEXT);

    /**
     * Install the StdioContext streams.
     *
     * @throws SecurityException if the caller does not have the {@code installStdioContextSelector} {@link RuntimePermission}
     * @throws IllegalStateException if the streams are already installed
     */
    public static void install() throws SecurityException, IllegalStateException {
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(INSTALL_PERMISSION);
        }
        if (! state.compareAndSet(State.UNINSTALLED, State.INSTALLING)) {
            throw new IllegalStateException("Already installed");
        }
        System.setOut(new DelegatingPrintStream() {
            PrintStream getDelegate() {
                return stdioContextSelector.getStdioContext().out;
            }
        });
        System.setErr(new DelegatingPrintStream() {
            PrintStream getDelegate() {
                return stdioContextSelector.getStdioContext().err;
            }
        });
        System.setIn(new DelegatingInputStream() {
            InputStream getDelegate() {
                return stdioContextSelector.getStdioContext().in;
            }
        });
        state.set(State.INSTALLED);
    }

    /**
     * Uninstall the StdioContext streams.
     *
     * @throws SecurityException if the caller does not have the {@code installStdioContextSelector} {@link RuntimePermission}
     * @throws IllegalStateException if the streams are already uninstalled
     */
    public static void uninstall() throws SecurityException, IllegalStateException {
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(INSTALL_PERMISSION);
        }
        if (! state.compareAndSet(State.INSTALLED, State.UNINSTALLING)) {
            throw new IllegalStateException("Already uninstalled");
        }
        System.setOut(SYSTEM_STDIO_CONTEXT.out);
        System.setErr(SYSTEM_STDIO_CONTEXT.err);
        System.setIn(SYSTEM_STDIO_CONTEXT.in);
        state.set(State.UNINSTALLED);
    }

    /**
     * Set the standard I/O context selector.  You must have the {@code setStdioContextSelector} {@link RuntimePermission} in
     * order to invoke this method.
     *
     * @param stdioContextSelector the selector to use
     * @throws SecurityException if the caller does not have the {@code installStdioContextSelector} {@link RuntimePermission}
     */
    public static void setStdioContextSelector(final StdioContextSelector stdioContextSelector) throws SecurityException {
        if (stdioContextSelector == null) {
            throw new NullPointerException("stdioContextSelector is null");
        }
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(SET_CONTEXT_SELECTOR_PERMISSION);
        }
        StdioContext.stdioContextSelector = stdioContextSelector;
    }

    private static abstract class DelegatingPrintStream extends PrintStream {

        protected DelegatingPrintStream() {
            super(NullOutputStream.getInstance());
        }

        abstract PrintStream getDelegate();

        public void flush() {
            if (entered.get() != null) {
                return;
            }
            try {
                entered.set(Boolean.TRUE);
                getDelegate().flush();
            } finally {
                entered.remove();
            }
        }

        public void close() {
            if (entered.get() != null) {
                return;
            }
            try {
                entered.set(Boolean.TRUE);
                getDelegate().close();
            } finally {
                entered.remove();
            }
        }

        public boolean checkError() {
            if (entered.get() != null) {
                return false;
            }
            try {
                entered.set(Boolean.TRUE);
                return getDelegate().checkError();
            } finally {
                entered.remove();
            }
        }

        public void write(final int b) {
            if (entered.get() != null) {
                return;
            }
            try {
                entered.set(Boolean.TRUE);
                getDelegate().write(b);
            } finally {
                entered.remove();
            }
        }

        public void write(final byte[] buf, final int off, final int len) {
            if (entered.get() != null) {
                return;
            }
            try {
                entered.set(Boolean.TRUE);
                getDelegate().write(buf, off, len);
            } finally {
                entered.remove();
            }
        }

        public void print(final boolean b) {
            if (entered.get() != null) {
                return;
            }
            try {
                entered.set(Boolean.TRUE);
                getDelegate().print(b);
            } finally {
                entered.remove();
            }
        }

        public void print(final char c) {
            if (entered.get() != null) {
                return;
            }
            try {
                entered.set(Boolean.TRUE);
                getDelegate().print(c);
            } finally {
                entered.remove();
            }
        }

        public void print(final int i) {
            if (entered.get() != null) {
                return;
            }
            try {
                entered.set(Boolean.TRUE);
                getDelegate().print(i);
            } finally {
                entered.remove();
            }
        }

        public void print(final long l) {
            if (entered.get() != null) {
                return;
            }
            try {
                entered.set(Boolean.TRUE);
                getDelegate().print(l);
            } finally {
                entered.remove();
            }
        }

        public void print(final float f) {
            if (entered.get() != null) {
                return;
            }
            try {
                entered.set(Boolean.TRUE);
                getDelegate().print(f);
            } finally {
                entered.remove();
            }
        }

        public void print(final double d) {
            if (entered.get() != null) {
                return;
            }
            try {
                entered.set(Boolean.TRUE);
                getDelegate().print(d);
            } finally {
                entered.remove();
            }
        }

        public void print(final char[] s) {
            if (entered.get() != null) {
                return;
            }
            try {
                entered.set(Boolean.TRUE);
                getDelegate().print(s);
            } finally {
                entered.remove();
            }
        }

        public void print(final String s) {
            if (entered.get() != null) {
                return;
            }
            try {
                entered.set(Boolean.TRUE);
                getDelegate().print(s);
            } finally {
                entered.remove();
            }
        }

        public void print(final Object obj) {
            if (entered.get() != null) {
                return;
            }
            try {
                entered.set(Boolean.TRUE);
                getDelegate().print(obj);
            } finally {
                entered.remove();
            }
        }

        public void println() {
            try {
                entered.set(Boolean.TRUE);
                getDelegate().println();
            } finally {
                entered.remove();
            }
        }

        public void println(final boolean x) {
            try {
                entered.set(Boolean.TRUE);
                getDelegate().println(x);
            } finally {
                entered.remove();
            }
        }

        public void println(final char x) {
            if (entered.get() != null) {
                return;
            }
            try {
                entered.set(Boolean.TRUE);
                getDelegate().println(x);
            } finally {
                entered.remove();
            }
        }

        public void println(final int x) {
            if (entered.get() != null) {
                return;
            }
            try {
                entered.set(Boolean.TRUE);
                getDelegate().println(x);
            } finally {
                entered.remove();
            }
        }

        public void println(final long x) {
            if (entered.get() != null) {
                return;
            }
            try {
                entered.set(Boolean.TRUE);
                getDelegate().println(x);
            } finally {
                entered.remove();
            }
        }

        public void println(final float x) {
            if (entered.get() != null) {
                return;
            }
            try {
                entered.set(Boolean.TRUE);
                getDelegate().println(x);
            } finally {
                entered.remove();
            }
        }

        public void println(final double x) {
            if (entered.get() != null) {
                return;
            }
            try {
                entered.set(Boolean.TRUE);
                getDelegate().println(x);
            } finally {
                entered.remove();
            }
        }

        public void println(final char[] x) {
            if (entered.get() != null) {
                return;
            }
            try {
                entered.set(Boolean.TRUE);
                getDelegate().println(x);
            } finally {
                entered.remove();
            }
        }

        public void println(final String x) {
            if (entered.get() != null) {
                return;
            }
            try {
                entered.set(Boolean.TRUE);
                getDelegate().println(x);
            } finally {
                entered.remove();
            }
        }

        public void println(final Object x) {
            if (entered.get() != null) {
                return;
            }
            try {
                entered.set(Boolean.TRUE);
                getDelegate().println(x);
            } finally {
                entered.remove();
            }
        }

        public PrintStream printf(final String format, final Object... args) {
            if (entered.get() != null) {
                return this;
            }
            try {
                entered.set(Boolean.TRUE);
                return getDelegate().printf(format, args);
            } finally {
                entered.remove();
            }
        }

        public PrintStream printf(final Locale l, final String format, final Object... args) {
            if (entered.get() != null) {
                return this;
            }
            try {
                entered.set(Boolean.TRUE);
                return getDelegate().printf(l, format, args);
            } finally {
                entered.remove();
            }
        }

        public PrintStream format(final String format, final Object... args) {
            if (entered.get() != null) {
                return this;
            }
            try {
                entered.set(Boolean.TRUE);
                return getDelegate().format(format, args);
            } finally {
                entered.remove();
            }
        }

        public PrintStream format(final Locale l, final String format, final Object... args) {
            if (entered.get() != null) {
                return this;
            }
            try {
                entered.set(Boolean.TRUE);
                return getDelegate().format(l, format, args);
            } finally {
                entered.remove();
            }
        }

        public PrintStream append(final CharSequence csq) {
            if (entered.get() != null) {
                return this;
            }
            try {
                entered.set(Boolean.TRUE);
                return getDelegate().append(csq);
            } finally {
                entered.remove();
            }
        }

        public PrintStream append(final CharSequence csq, final int start, final int end) {
            if (entered.get() != null) {
                return this;
            }
            try {
                entered.set(Boolean.TRUE);
                return getDelegate().append(csq, start, end);
            } finally {
                entered.remove();
            }
        }

        public PrintStream append(final char c) {
            if (entered.get() != null) {
                return this;
            }
            try {
                entered.set(Boolean.TRUE);
                return getDelegate().append(c);
            } finally {
                entered.remove();
            }
        }
    }

    private static abstract class DelegatingInputStream extends InputStream {
        abstract InputStream getDelegate();

        public int read() throws IOException {
            return getDelegate().read();
        }

        public int read(final byte[] b) throws IOException {
            return getDelegate().read(b);
        }

        public int read(final byte[] b, final int off, final int len) throws IOException {
            return getDelegate().read(b, off, len);
        }

        public long skip(final long n) throws IOException {
            return getDelegate().skip(n);
        }

        public int available() throws IOException {
            return getDelegate().available();
        }

        public void close() throws IOException {
            getDelegate().close();
        }

        public void mark(final int readLimit) {
            getDelegate().mark(readLimit);
        }

        public void reset() throws IOException {
            getDelegate().reset();
        }

        public boolean markSupported() {
            return getDelegate().markSupported();
        }
    }
}
