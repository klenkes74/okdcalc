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

import java.util.Set;

/**
 * The sizing request for ElasticSearch within OKD.
 *
 * @author rlichti
 * @version 1.0.0 2020-02-16
 * @since 1.0.0 2020-02-16
 */
@Immutable
@JsonSerialize(as = LoggingSizingRequest.class)
@JsonDeserialize(builder = LoggingSizingRequest.Builder.class)
public interface _LoggingSizingRequest {
    /**
     * The number of replica. Replicas are the copies of the primary shard. So
     * replica 1 means 2 copies of the data (1 primary shard and 1 replica
     * shard).
     */
    int DEFAULT_NUMBER_OF_REPLICA = 1;

    /**
     * The number of available availability zones for replication.
     */
    int DEFAULT_NUMBER_OF_AVAILABILITY_ZONES = 2;

    /**
     * The default OKD version if no other version is specified. Currently only
     * OKD 3.11 is supported.
     */
    _OkdVersion DEFAULT_OKD_VERSION = OkdVersion.builder()
            .major(3)
            .minor(11)
            .patchlevel(0)
            .build();


    /**
     * @return the different log types (size and number of events).
     */
    Set<_LoggingEvent> getLoggingEventTypes();

    /**
     * @return the possible infra node types to be scored.
     */
    Set<_NodeDefinition> getPossibleInfraNodeTypes();

    int getNumberOfComputeNodes();

    int getParallelKibanaUsers();

    @Value.Default
    default int getNumberOfAvailabilityZones() {
        return DEFAULT_NUMBER_OF_AVAILABILITY_ZONES;
    }

    @Value.Default
    default int getNumberOfReplica() {
        return DEFAULT_NUMBER_OF_REPLICA;
    }

    /**
     * @return The version of OKD to calculate the sizing for.
     */
    @Value.Default
    default _OkdVersion getVersion() {
        return DEFAULT_OKD_VERSION;
    }
}
