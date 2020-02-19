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
@JsonSerialize(as = NodeDefinition.class)
@JsonDeserialize(builder = NodeDefinition.Builder.class)
public interface _NodeDefinition {
    /**
     * Number of maximum pods OKD can manage per node (stable).
     */
    int OKD_MAX_PODS_PER_NODE = 250;

    /**
     * Number of pods per logical core.
     */
    int OKD_MAX_PODS_PER_CORE = 10;

    /**
     * Number of disks available for logging.
     */
    int DEFAULT_AVAILABLE_LOGGING_DISKS_PER_NODE = 1000;

    /**
     * Number of logging events fluentd is capable of handling per second on
     * this node type,
     */
    int DEFAULT_LOGGING_EVENTS_LIMIT = 700;

    /**
     * @return The RAM of the node type in bytes.
     */
    long getMemory();

    /**
     * @return The CPU definition of this node type.
     */
    CPU getCpu();

    /**
     * @return Size of the disk of the node.
     */
    long getDisk();

    /**
     * @return bandwith of the disk available to the logging cluster in bytes/s.
     */
    long getDiskBandwidth();

    /**
     * If there is a limit for disks that may be attached to a node available
     * solely to the logging cluster, please add the data. Otherwise we assume
     * {@value DEFAULT_AVAILABLE_LOGGING_DISKS_PER_NODE} (value of
     * {@link #DEFAULT_AVAILABLE_LOGGING_DISKS_PER_NODE}).
     *
     * @return how many logging disks may be attached to a logging cluster node?
     */
    @Value.Default
    default int getMaxNumberOfLoggingDisks() {
        return DEFAULT_AVAILABLE_LOGGING_DISKS_PER_NODE;
    }

    /**
     * If not changed, this method will return the default value of
     * {@value #OKD_MAX_PODS_PER_CORE} ({@link #OKD_MAX_PODS_PER_CORE}).
     *
     * @return How many pods should run per (logical) core?
     */
    @Value.Default
    default int getPodsPerCore() {
        return OKD_MAX_PODS_PER_CORE;
    }

    /**
     * @return How many pods can run on this node type
     * ({@value #OKD_MAX_PODS_PER_NODE} - {@link #OKD_MAX_PODS_PER_NODE})
     * into account.
     */
    @Value.Default
    default int getMaxPods() {
        int result = getPodsPerCore() * getCpu().getLogical();
        return Math.min(result, OKD_MAX_PODS_PER_NODE);
    }

    @Value.Default
    default long getMillicores() {
        return getCpu().getLogical() * 1000L;
    }

    /**
     * @return How many log events fluentd can handle (defaults to
     * {@value #DEFAULT_LOGGING_EVENTS_LIMIT} -
     * {@link #DEFAULT_LOGGING_EVENTS_LIMIT}).
     */
    @Value.Default
    default int getLoggingEventsPerSecondLimit() {
        return DEFAULT_LOGGING_EVENTS_LIMIT;
    }

    /**
     * The score of this node type.
     *
     * @return the score of this node.
     */
    @Value.Default
    default long getScore() {
        return 1000L;
    }
}
