/*
 * Copyright (c) 2020  Kaiserpfalz EDV-Service, Roland T. Lichti.
 *
 * This is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kaiserpfalzedv.okdcalc;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * This exception wraps a normal exception into a {@link RuntimeException}. All reading methods are then delegated to
 * the wrapped exception.
 *
 * @author rlichti
 * @version 1.0.0 2020-02-08
 * @since 1.0.0 2020-02-08
 */
public class OkdCalcWrappedException extends BaseOkdCalcRuntimeException {
    private Throwable wrapped;

    public OkdCalcWrappedException(final Throwable wrapped) {
        super(wrapped);

        this.wrapped = wrapped;
    }

    @Override
    public String getMessage() {
        return wrapped.getMessage();
    }

    @Override
    public String getLocalizedMessage() {
        return wrapped.getLocalizedMessage();
    }

    @Override
    public Throwable getCause() {
        return wrapped.getCause();
    }

    @Override
    public void printStackTrace() {
        wrapped.printStackTrace();
    }

    @Override
    public void printStackTrace(PrintStream s) {
        wrapped.printStackTrace(s);
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        wrapped.printStackTrace(s);
    }

    @Override
    public Throwable fillInStackTrace() {
        if (wrapped != null)
            return wrapped.fillInStackTrace();
        else
            return this;
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        return wrapped.getStackTrace();
    }

    @Override
    public int hashCode() {
        return wrapped.hashCode();
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object obj) {
        return wrapped.equals(obj);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    /**
     * Since {@link #getClass()} returns the class UmameWrappedException, we need a method to get the original class.
     *
     * @return the class of the wrapped exception.
     */
    public Class<? extends Throwable> getWrappedClass() {
        return wrapped.getClass();
    }

    /**
     * To retrieve the unwrapped exception itself.
     *
     * @return The original wrapped exception.
     */
    public Throwable unwrap() {
        return wrapped;
    }

    @Override
    public String toString() {
        return wrapped.toString();
    }
}
