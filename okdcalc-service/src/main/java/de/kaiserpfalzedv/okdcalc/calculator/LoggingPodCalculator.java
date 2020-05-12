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

import de.kaiserpfalzedv.okdcalc.facts.LoggingSizingResult;
import de.kaiserpfalzedv.okdcalc.facts._LoggingEvent;
import de.kaiserpfalzedv.okdcalc.facts._LoggingSizingRequest;
import de.kaiserpfalzedv.okdcalc.facts._NodeDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import java.util.Set;

import static de.kaiserpfalzedv.okdcalc.facts._ClusterSizingResult.GiB;
import static de.kaiserpfalzedv.okdcalc.facts._ClusterSizingResult.MiB;

/**
 * This calculator delivers the number of pods per nodes and number of nodes
 * based on a default pod and a node size.
 *
 * @author rlichti
 * @version 1.0.0 2020-02-14
 * @since 1.0.0 2020-02-14
 */
@Dependent
public class LoggingPodCalculator {
    private static final Logger LOG = LoggerFactory.getLogger(LoggingPodCalculator.class);

    /**
     * Kubelet and operating system reservation + fluentd usage (1GiB).
     */
    private static final long RESERVED_MEMORY_PER_LOGGING_NODE = 50 * MiB + 2 * GiB;
    /**
     * Kubelet and operating system reservation + fluentd usage (500mCores)
     */
    private static final long RESERVED_MILLICORES_PER_LOGGING_NODE = 1050 + 500;

    /**
     * The maximum number of active primary shards in one logging cluster.
     */
    private static final int MAX_ACTIVE_SHARDS = 100000;

    /**
     * 3600 seconds per hour * 24 hours per day.
     */
    private static final int SECONDS_PER_DAY = 86400;

    /**
     * Memory used by a single ES data pod. We don't do any calculation for less
     * than 62 GiB.
     */
    private static final double BYTES_PER_ES_POD = 62D * GiB;


    public LoggingSizingResult calculateLoggingSolution(
            final _NodeDefinition infraNodeType,
            final _LoggingSizingRequest sizingRequest
    ) {
        long bw = calculateDiskBW(sizingRequest.getLoggingEventTypes());
        int disksForBW = (int) Math.ceil((double) bw / infraNodeType.getDiskBandwidth()) * (sizingRequest.getNumberOfReplica() + 1);
        long totalDiskSize = calculateTotalDiskSize(sizingRequest.getLoggingEventTypes(), sizingRequest.getNumberOfReplica());
        int totalDiskCount = (int) Math.max(Math.max(Math.ceil((double) totalDiskSize / infraNodeType.getDisk()), disksForBW), sizingRequest.getNumberOfAvailabilityZones());
        int ESpodPerInfraNode = Math.min(calculateEsPodPerInfraNode(infraNodeType), totalDiskCount / sizingRequest.getNumberOfAvailabilityZones());
        int loggingNodePerAZ = calculateLoggingNodesPerAvailabilityZone(totalDiskCount, ESpodPerInfraNode, infraNodeType, sizingRequest);

        LOG.trace("nodePerAZ={}, podsPerNode={}, totalDiskSize={} GiB, memory={} GiB, millicores={}",
                  loggingNodePerAZ, ESpodPerInfraNode, totalDiskSize / GiB,
                  infraNodeType.getMemory() / GiB, infraNodeType.getMillicores()
        );
        int podCount = sizingRequest.getNumberOfAvailabilityZones() * loggingNodePerAZ * ESpodPerInfraNode;
        long podCpu = infraNodeType.getMillicores() - RESERVED_MILLICORES_PER_LOGGING_NODE;
        long podMemory = infraNodeType.getMemory() - RESERVED_MEMORY_PER_LOGGING_NODE;
        int activePrimaryShards = calculateActivePrimaryShards(sizingRequest.getLoggingEventTypes());

        return LoggingSizingResult.builder()
                .infraNodeType(infraNodeType)
                .nodes(Math.max(podCount, totalDiskCount) / ESpodPerInfraNode)
                .numberOfLoggingPods(Math.max(podCount, totalDiskCount))
                .cpuPerLoggingPod(podCpu)
                .diskSizePerLoggingPod(totalDiskSize / totalDiskCount)
                .memoryPerLoggingPod(podMemory)
                .numberOfActivePrimaryShards(activePrimaryShards)
                .build();
    }

    private long calculateDiskBW(final Set<_LoggingEvent> eventTypes) {
        long result = 0L;

        for (_LoggingEvent e : eventTypes) {
            result += calculateSingleBW(e);
        }

        LOG.trace("logging disk bw: {} b", result);
        return result;
    }

    private long calculateSingleBW(_LoggingEvent e) {
        return e.getEventSize() * e.getDefaultNumberOfEventsPerHour() / 3600 * e.getNumberOfContainers();
    }

    private long calculateTotalDiskSize(final Set<_LoggingEvent> eventTypes, int replicaCount) {
        long result = 0L;

        for (_LoggingEvent e : eventTypes) {
            LOG.trace("event: retention={}, replica={}, bw={}", e.getRetentionDays(), replicaCount, calculateSingleBW(e));
            result += e.getRetentionDays() * SECONDS_PER_DAY * calculateSingleBW(e) * (replicaCount + 1);
        }

        LOG.trace("total disk size: {} GiB", result / GiB);
        return result;
    }

    private int calculateLoggingNodesPerAvailabilityZone(
            final int totalDiskCount,
            final int esPodsPerNode,
            final _NodeDefinition nodeDefinition,
            final _LoggingSizingRequest sizingRequest
    ) {
        LOG.trace(
                "totalDiskCount={}, az={}, maxLoggingDisk={}, podsPerNode={}, computeNodes={}, cpu={}",
                totalDiskCount,
                sizingRequest.getNumberOfAvailabilityZones(),
                nodeDefinition.getMaxNumberOfLoggingDisks(),
                esPodsPerNode,
                sizingRequest.getNumberOfComputeNodes(),
                nodeDefinition.getCpu().getLogical()
        );

        int result = 1;

        result += (totalDiskCount / sizingRequest.getNumberOfAvailabilityZones() / esPodsPerNode > 1) ? 1 : 0;

        int cpuLimit = (int) Math.ceil((double) sizingRequest.getNumberOfComputeNodes() / (sizingRequest.getNumberOfAvailabilityZones() * nodeDefinition.getCpu().getLogical()) - result);
        result = Math.max(cpuLimit, result);

        return result;
    }

    private int calculateEsPodPerInfraNode(_NodeDefinition nodeDefinition) {
        return (int) Math.max(Math.floor(nodeDefinition.getMemory() / BYTES_PER_ES_POD), 1);
    }

    private int calculateActivePrimaryShards(Set<_LoggingEvent> eventTypes) {
        int result = 0;

        for (_LoggingEvent e : eventTypes) {
            result += e.getNumberOfNamespaces() * e.getRetentionDays();
        }

        if (result > MAX_ACTIVE_SHARDS) {
            LOG.warn("Too much active primary shards: {}", result);
        }

        return result;
    }
}
