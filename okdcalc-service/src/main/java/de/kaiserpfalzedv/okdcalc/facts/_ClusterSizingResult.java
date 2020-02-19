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
 * This is the definition of the cluster sizing calculation. It defines how many
 * nodes of a given size are needed to handle the work load defined by the
 * number of pods and node size. Also the logging is taken into account.
 *
 * @author rlichti
 * @version 1.0.0 2020-02-14
 * @since 1.0.0 2020-02-14
 */
@Immutable
@Value.Modifiable
@JsonSerialize(as = ClusterSizingResult.class)
@JsonDeserialize(builder = ClusterSizingResult.Builder.class)
public interface _ClusterSizingResult {
    /**
     * A helper for making the other definitions more readable.
     */
    long MiB = 1024L * 1024;

    /**
     * A helper for making the other definitions more readable.
     */
    long GiB = 1024 * MiB;

    /**
     * A helper for making the other definitions more readable.
     */
    long MB = 1000 * 1000;

    /**
     * A helper for making the other definitions more readable.
     */
    long GB = 1000 * MB;

    /**
     * Reserve 5 MB per pod for the kubelet.
     */
    long KUBLET_BYTES_PER_POD = 5 * MiB;

    /**
     * Reserve 5 Millicores per pod for the kubelet.
     */
    long KUBLET_MILLICORE_PER_POD = 5;

    /**
     * Reserve 5 MB per pod for the operating system.
     */
    long SYSTEM_BYTES_PER_POD = 5L * MiB;

    /**
     * Reserve 5 Millicores per pod for the operating system.
     */
    long SYSTEM_MILLICORE_PER_POD = 5L;

    /**
     * Reserved RAM per pod (combined kubelet and operating system).
     */
    long RESERVED_BYTES_PER_POD = KUBLET_BYTES_PER_POD + SYSTEM_BYTES_PER_POD;

    /**
     * Reserved CPU per pod (combined kubelet and operating system).
     */
    long RESERVED_MILLICORE_PER_POD = KUBLET_MILLICORE_PER_POD + SYSTEM_MILLICORE_PER_POD;


    /**
     * @return What are the parameters of the node this sizing has been calculated for?
     */
    _NodeDefinition getNodeSizing();

    /**
     * @return What is the default usage per pod this sizing has been calculated for?
     */
    _Pod getDefaultPod();

    /**
     * @return How many nodes are needed?
     */
    int getNodes();

    /**
     * @return How many {@link #getDefaultPod()}s are possible per node?
     */
    int getPodsPerNode();

    /**
     * If a scoring algorithm is used this is the score value of this sizing.
     *
     * @return the scoring value of this solution (if any).
     */
    default long getScore() {
        return getNodes() * getNodeSizing().getScore();
    }


    /**
     * @return How many memory will be used by the kubelet?
     */
    @Value.Default
    default long getKubeletMemory() {
        return getPodsPerNode() * KUBLET_BYTES_PER_POD;
    }

    /**
     * @return How many CPU will be used by the kubelet?
     */
    @Value.Default
    default long getKubeletCPU() {
        return getPodsPerNode() * KUBLET_MILLICORE_PER_POD;
    }

    /**
     * @return How much RAM will be used by the operating system?
     */
    @Value.Default
    default long getSystemMemory() {
        return getPodsPerNode() * SYSTEM_BYTES_PER_POD;
    }

    /**
     * @return How much RAM will be used by the operating system?
     */
    @Value.Default
    default long getSystemCPU() {
        return getPodsPerNode() * SYSTEM_MILLICORE_PER_POD;
    }

    /**
     * @return How many logging events will be emitted by this node when filled with default pods?
     */
    @Value.Default
    default int getLoggingEventsPerNode() {
        return getDefaultPod().getNumberOfLoggingEventsPerSecond() * getPodsPerNode();
    }

    /**
     * Returns the free memory after kubelet and system memory have been blocked.
     *
     * @return The memory in bytes available for pods.
     */
    @Value.Default
    default long getFreeMemory() {
        return getNodeSizing().getMemory() - getKubeletMemory() - getSystemMemory();
    }


    /**
     * Returns the free CPU millicores after kubelet and system cpu usage have
     * been blocked.
     *
     * @return the millicores available for pods.
     */
    @Value.Default
    default long getFreeCPU() {
        return (getNodeSizing().getMillicores()) - getKubeletCPU() - getSystemCPU();
    }

    /**
     * @return How much RAM gets wasted when only default pods are provisioned on a node?
     */
    @Value.Default
    default long getWastedMemory() {
        return getFreeMemory() - getPodsPerNode() * getDefaultPod().getMemory();
    }

    /**
     * @return How much CPU is wasted when only default pods are provisioned on a node?
     */
    @Value.Default
    default long getWastedCPU() {
        return getFreeCPU() - getPodsPerNode() * getDefaultPod().getMilliCores();
    }

    /**
     * @return How many log events are "wasted" when only default pods are provisioned to a node?
     */
    @Value.Default
    default int getWastedLogEvents() {
        return getNodeSizing().getLoggingEventsPerSecondLimit() - getLoggingEventsPerNode();
    }
}
