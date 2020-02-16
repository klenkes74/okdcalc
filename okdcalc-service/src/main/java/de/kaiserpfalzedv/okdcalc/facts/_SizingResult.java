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
@JsonSerialize(as = SizingResult.class)
@JsonDeserialize(builder = SizingResult.Builder.class)
public interface _SizingResult {
    long KUBLET_BYTES_PER_POD = 5L * 1024 * 1024;
    long KUBLET_MILLICORE_PER_POD = 5L;
    long SYSTEM_BYTES_PER_POD = 5L * 1024 * 1024;
    long SYSTEM_MILLICORE_PER_POD = 5L;

    long RESERVED_BYTES_PER_POD = KUBLET_BYTES_PER_POD + SYSTEM_BYTES_PER_POD;
    long RESERVED_MILLICORE_PER_POD = KUBLET_MILLICORE_PER_POD + SYSTEM_MILLICORE_PER_POD;

    _NodeDefinition getNodeSizing();

    _Pod getDefaultPod();

    int getNodes();

    int getPodsPerNode();

    /**
     * If a scoring algorithm is used this is the score value of this sizing.
     *
     * @return the scoring value of this solution (if any).
     */
    default long getScore() {
        return getNodes() * getNodeSizing().getScore();
    }


    @Value.Default
    default long getKubeletMemory() {
        return getPodsPerNode() * KUBLET_BYTES_PER_POD;
    }

    @Value.Default
    default long getKubeletCPU() {
        return getPodsPerNode() * KUBLET_MILLICORE_PER_POD;
    }

    @Value.Default
    default long getSystemMemory() {
        return getPodsPerNode() * SYSTEM_BYTES_PER_POD;
    }

    @Value.Default
    default long getSystemCPU() {
        return getPodsPerNode() * SYSTEM_MILLICORE_PER_POD;
    }

    /**
     * Returns the free memory after kubelet and system memory have been blocked.
     *
     * @return The memory in bytes available for pods.
     */
    @Value.Default
    default long getFreeMemory() {
        return getNodeSizing().getMemory()
                - getKubeletMemory()
                - getSystemMemory();
    }


    /**
     * Returns the free CPU millicores after kubelet and system cpu usage have
     * been blocked.
     *
     * @return the millicores available for pods.
     */
    @Value.Default
    default long getFreeCPU() {
        return (getNodeSizing().getMillicores())
                - getKubeletCPU()
                - getSystemCPU();
    }

    @Value.Default
    default long getWastedMemory() {
        return getFreeMemory()
                - getPodsPerNode() * getDefaultPod().getMemory();
    }

    @Value.Default
    default long getWastedCPU() {
        return getFreeCPU()
                - getPodsPerNode() * getDefaultPod().getMilliCores();
    }
}
