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

package de.kaiserpfalzedv.okdcalc.facts;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.kaiserpfalzedv.okdcalc.Immutable;
import org.immutables.value.Value;

/**
 * @author rlichti
 * @version 1.0.0 2020-02-14
 * @since 1.0.0 2020-02-14
 */
@Immutable
@Value.Modifiable
@JsonSerialize(as = Pod.class)
@JsonDeserialize(builder = Pod.Builder.class)
public interface _Pod {
    /**
     * Log events ("lines") generated by this pod and second.
     */
    int DEFAULT_LOGGING_EVENTS_PER_POD_AND_SECOND = 5;

    long getMilliCores();

    long getMemory();

    /**
     * @return How many logging events will be emitted by the default pod?
     */
    @Value.Default
    default int getNumberOfLoggingEventsPerSecond() {
        return DEFAULT_LOGGING_EVENTS_PER_POD_AND_SECOND;
    }
}