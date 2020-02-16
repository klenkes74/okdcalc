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

package de.kaiserpfalzedv.okdcalc.calculator;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author rlichti
 * @version 1.0.0 2020-02-16
 * @since 1.0.0 2020-02-16
 */
public class NoSolutionExceptionTest {
    private static final Logger LOG = LoggerFactory.getLogger(NoSolutionExceptionTest.class);


    public static final String MESSAGE = "another message";
    public static final Exception CAUSE = new Exception("cause");


    @Test
    public void shouldWorkWhenCalledWithoutParameter() {
        NoSolutionException result = new NoSolutionException();

        assert "There is no sizing calculation for this sizing requirements".equals(result.getMessage());
    }


    @Test
    public void shouldWorkWhenCalledWithACause() {
        NoSolutionException result = new NoSolutionException(CAUSE);

        assert CAUSE.getMessage().equals(result.getMessage());
        assert CAUSE.equals(result.getCause());
    }

    @Test
    public void shouldWorkWhenCalledWithAMessage() {
        NoSolutionException result = new NoSolutionException(MESSAGE);

        assert MESSAGE.equals(result.getMessage());
    }


    @Test
    public void shouldWorkWhenCalledWithAMessageAndACause() {
        NoSolutionException result = new NoSolutionException(MESSAGE, CAUSE);

        assert MESSAGE.equals(result.getMessage());
        assert CAUSE.equals(result.getCause());
    }
}
