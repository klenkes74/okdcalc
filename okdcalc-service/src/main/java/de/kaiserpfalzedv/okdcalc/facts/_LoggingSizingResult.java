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
 * The result for the logging request.
 *
 * @author rlichti
 * @version 1.0.0 2020-02-14
 * @since 1.0.0 2020-02-14
 */
@Immutable
@Value.Modifiable
@JsonSerialize(as = LoggingSizingResult.class)
@JsonDeserialize(builder = LoggingSizingResult.Builder.class)
public interface _LoggingSizingResult {
    /**
     * @return The type of the infranode this data is calculated for.
     */
    _NodeDefinition getInfraNodeType();

    /**
     * @return the scoring value of this solution (if any).
     */
    @Value.Default
    default long getScore() {
        return getNumberOfLoggingPods() * getInfraNodeType().getScore();
    }

    /**
     * @return the number of nodes for the logging cluster.
     */
    int getNodes();

    /**
     * @return The number of pods for the logging cluster.
     */
    int getNumberOfLoggingPods();

    /**
     * @return The disk size for a single logging pod in bytes.
     */
    long getDiskSizePerLoggingPod();

    /**
     * @return The RAM for a single logging pod in bytes.
     */
    long getMemoryPerLoggingPod();

    /**
     * @return The CPU usage for a single logging pod in millicores.
     */
    long getCpuPerLoggingPod();

    /**
     * @return The number of active shards in the logging cluster.
     */
    long getNumberOfActivePrimaryShards();
}
