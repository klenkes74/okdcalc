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

import de.kaiserpfalzedv.okdcalc.BaseOkdCalcException;

/**
 * The NoSolutionException is thrown if the given base data does not compute to
 * a valid sizing result.
 *
 * @author rlichti
 * @version 1.0.0 2020-02-16
 * @since 1.0.0 2020-02-16
 */
public class NoSolutionException extends BaseOkdCalcException {
    public NoSolutionException() {
        super("There is no sizing calculation for this sizing requirements");
    }

    public NoSolutionException(final Throwable cause) {
        super(cause.getMessage(), cause);
    }

    public NoSolutionException(final String message) {
        super(message);
    }

    public NoSolutionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
