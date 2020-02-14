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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author rlichti
 * @version 1.0.0 2020-02-08
 * @since 1.0.0 2020-02-08
 */
public class OkdCalcWrappedExceptionTest {
    private static final String MESSAGE = "message";
    private static final Exception CAUSE = new Exception("cause");
    private static final Exception WRAPPED = new Exception(MESSAGE, CAUSE);

    private OkdCalcWrappedException service;

    @Test
    public void shouldReturnWrappedMessage() {
        assert WRAPPED.getMessage().equals(service.getMessage());
    }

    @Test
    public void shouldReturnWrappedCause() {
        assert WRAPPED.getCause().equals(service.getCause());
    }

    @Test
    public void shouldReturnWrappedLocalizedMessage() {
        assert WRAPPED.getLocalizedMessage().equals(service.getLocalizedMessage());
    }

    @Test
    public void shouldReturnWrappedStackTrace() {
        assert service.getStackTrace().length == WRAPPED.getStackTrace().length;
    }

    @Test
    public void shouldReturnWrappedSuppressed() {
        assert Arrays.equals(WRAPPED.getSuppressed(), service.getSuppressed());
    }

    @Test
    public void shouldReturnWrappedFillInStackTrace() {
        assert WRAPPED.fillInStackTrace().equals(service.fillInStackTrace());
    }


    @Test
    public void shouldPrintStackTraceOfWrappedException() {
        service.printStackTrace(); // can't check this one ...
    }

    @Test
    public void shouldPrintStackTraceOfWrappedExceptionToStream() throws IOException {
        String serviceOutput = streamStackTrace(service);
        String wrappedOutput = streamStackTrace(WRAPPED);

        assert serviceOutput.equals(wrappedOutput);
    }

    private String streamStackTrace(final Throwable cause) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream stream = new PrintStream(baos, true, StandardCharsets.UTF_8);

        try (baos; stream) {
            cause.printStackTrace(stream);

            return new String(baos.toByteArray(), StandardCharsets.UTF_8);
        }
    }

    @Test
    public void shouldPrintStackTraceOfWrappedExceptionToWriter() {
        String serviceOutput = printWriteStackTrace(service);
        String wrappedOutput = printWriteStackTrace(WRAPPED);

        assert serviceOutput.equals(wrappedOutput);
    }

    private String printWriteStackTrace(final Throwable cause) {
        StringWriter writer = new StringWriter();
        PrintWriter printer = new PrintWriter(writer);

        cause.printStackTrace(printer);

        return writer.toString();
    }

    @Test
    public void shouldGiveTheSameClassAsWrappedException() {
        assert service.getWrappedClass().equals(WRAPPED.getClass());
    }

    @Test
    public void shouldUnwrapCorrectly() {
        assert WRAPPED.equals(service.unwrap());
    }

    @Test
    public void shouldThrowCloneNotSupportedExceptionWhenCloned() {
        try {
            service.clone();

            fail("There should have been a CloneNotSupportedException!");
        } catch (CloneNotSupportedException e) {
            // every thing is fine!
        }
    }

    @Test
    public void shouldHaveTheSameToStringAsWrappedException() {
        assert service.toString().equals(WRAPPED.toString());
    }

    @Test
    public void shouldHaveTheSameHashcodeAsWrappedException() {
        assert service.hashCode() == WRAPPED.hashCode();
    }

    @Test
    public void shouldEqualWrappedException() {
        assert service.equals(WRAPPED);
    }

    @Test
    public void shouldHaveSameToStringAsWrappedException() {
        assert service.toString().equals(WRAPPED.toString());
    }


    @BeforeEach
    public void setUpService() {
        service = new OkdCalcWrappedException(WRAPPED);
    }
}
