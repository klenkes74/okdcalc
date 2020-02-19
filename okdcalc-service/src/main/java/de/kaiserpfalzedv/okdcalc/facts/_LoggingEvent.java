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
 * Definition of a logging event sizing input for the ElasticSearchCalculator.
 * Mainly the event size and number of events per hour as input for the
 * calculator.
 *
 * @author rlichti
 * @version 1.0.0 2020-02-14
 * @since 1.0.0 2020-02-14
 */
@Immutable
@Value.Modifiable
@JsonSerialize(as = LoggingEvent.class)
@JsonDeserialize(builder = LoggingEvent.Builder.class)
public interface _LoggingEvent {
    /**
     * The default logging event size if no other value is specified for the
     * request.
     */
    int DEFAULT_LOGGING_EVENT_SIZE = 256;

    /**
     * The default number of events of size {@link #getEventSize()} per hour.
     */
    int DEFAULT_NUMBER_OF_EVENTS_PER_HOUR = 3600;

    /**
     * The default retention time in days of this type of events. Need to be
     * configured, of course.
     */
    int DEFAULT_RETENTION_DAYS = 28;


    /**
     * @return How many namespaces generate this type of logging event?
     */
    int getNumberOfNamespaces();

    /**
     * @return how may bytes this logging event type is long?
     */
    @Value.Default
    default int getEventSize() {
        return DEFAULT_LOGGING_EVENT_SIZE;
    }

    /**
     * @return How many logging events of this type are logged per hour?
     */
    @Value.Default
    default int getDefaultNumberOfEventsPerHour() {
        return DEFAULT_NUMBER_OF_EVENTS_PER_HOUR;
    }

    /**
     * The retention time for this type of logging events. Of course this has
     * to be configured in the logging configuration.
     *
     * @return How many days should the data be stored.
     */
    @Value.Default
    default int getRetentionDays() {
        return DEFAULT_RETENTION_DAYS;
    }

    /**
     * @return How many container log in this type?
     */
    int getNumberOfContainers();
}
