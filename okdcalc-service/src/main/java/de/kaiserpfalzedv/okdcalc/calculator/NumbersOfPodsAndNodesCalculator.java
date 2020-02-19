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

import de.kaiserpfalzedv.okdcalc.facts.ClusterSizingResult;
import de.kaiserpfalzedv.okdcalc.facts._NodeDefinition;
import de.kaiserpfalzedv.okdcalc.facts._Pod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;

import static de.kaiserpfalzedv.okdcalc.facts._ClusterSizingResult.RESERVED_BYTES_PER_POD;
import static de.kaiserpfalzedv.okdcalc.facts._ClusterSizingResult.RESERVED_MILLICORE_PER_POD;

/**
 * This calculator delivers the number of pods per nodes and number of nodes
 * based on a default pod and a node size.
 *
 * @author rlichti
 * @version 1.0.0 2020-02-14
 * @since 1.0.0 2020-02-14
 */
@Dependent
public class NumbersOfPodsAndNodesCalculator {
    private static final Logger LOG = LoggerFactory.getLogger(NumbersOfPodsAndNodesCalculator.class);

    /**
     * The maximum number of compute nodes of a cluster.
     */
    private static final int MAX_NODES_PER_CLUSTER = 2000;


    public ClusterSizingResult calculateNumberOfPodsPerNodeAndNumberOfNodes(
            final int totalNumberOfPods,
            final _Pod defaultPod,
            final _NodeDefinition nodeDefintion
    ) throws NoSolutionException {
        int podsPerNode = calcuatePodsPerNode(defaultPod, nodeDefintion);
        int numberOfNodes = calculateNumberOfNodes(podsPerNode, totalNumberOfPods);

        return ClusterSizingResult.builder()
                .podsPerNode(podsPerNode)
                .nodes(numberOfNodes)
                .defaultPod(defaultPod)
                .nodeSizing(nodeDefintion)
                .build();
    }

    private int calcuatePodsPerNode(final _Pod defaultPod,
                                    final _NodeDefinition nodeDefinition)
            throws NoSolutionException {
        if (defaultPod.getMilliCores() + RESERVED_MILLICORE_PER_POD > nodeDefinition.getMillicores()) {
            LOG.warn("Too small nodes for CPU usage of work load.");
            throw new NoSolutionException("Not enough CPU per node for this work load.");
        }

        if (defaultPod.getMemory() + RESERVED_BYTES_PER_POD > nodeDefinition.getMemory()) {
            LOG.warn("Too small nodes for memory usage of work load.");
            throw new NoSolutionException("Not enough memory per node for this work load.");
        }

        long podsForMemory = getPodsForMemory(defaultPod, nodeDefinition);
        long podsForCpu = calculatePodsLimitedByCpu(defaultPod, nodeDefinition);
        long podsForLoggingEvents = calculatePodsLimitedByLogEvents(defaultPod, nodeDefinition);
        int result = (int) Math.min(Math.min(Math.min(podsForCpu, podsForMemory), podsForLoggingEvents), nodeDefinition.getMaxPods());

        LOG.debug(
                "Max pods per node: result={}, memory={}, cpu={}, logging={}, node={}",
                result, podsForMemory, podsForCpu, podsForLoggingEvents, nodeDefinition.getMaxPods()
        );
        return result;
    }

    private long getPodsForMemory(
            final _Pod defaultPod,
            final _NodeDefinition nodeDefinition
    ) {
        long usedMemory;
        long podsForMemory;
        int subtractPod = 0;
        do {
            podsForMemory = nodeDefinition.getMemory() / defaultPod.getMemory() - subtractPod;
            podsForMemory = Math.min(nodeDefinition.getMaxPods(), podsForMemory);
            usedMemory = podsForMemory * (defaultPod.getMemory() + RESERVED_BYTES_PER_POD);
            subtractPod++;
        } while (usedMemory > nodeDefinition.getMemory());

        LOG.debug("Needed {} iteration(s) for memory usage calculation: pods={}", subtractPod, podsForMemory);
        return podsForMemory;
    }

    private long calculatePodsLimitedByCpu(
            final _Pod defaultPod,
            final _NodeDefinition nodeDefinition
    ) {
        long usedCpu;
        long podsForCpu;
        long subtractPod = 0;
        do {
            podsForCpu = nodeDefinition.getMillicores() / defaultPod.getMilliCores() - subtractPod;
            podsForCpu = Math.min(nodeDefinition.getMaxPods(), podsForCpu);
            usedCpu = podsForCpu * (defaultPod.getMilliCores() + RESERVED_MILLICORE_PER_POD);
            subtractPod++;
        } while (usedCpu > nodeDefinition.getMillicores());

        LOG.debug("Needed {} iteration(s) for cpu usage calculation: pods={}", subtractPod, podsForCpu);
        return podsForCpu;
    }

    private int calculatePodsLimitedByLogEvents(final _Pod defaultPod, final _NodeDefinition nodeDefinition) {
        int result = (int) Math.ceil((double) nodeDefinition.getLoggingEventsPerSecondLimit()
                                             / defaultPod.getNumberOfLoggingEventsPerSecond());

        LOG.debug("Logging (fluentd) limit: pod={}, logevents={}", result, result * defaultPod.getNumberOfLoggingEventsPerSecond());
        return result;
    }

    private int calculateNumberOfNodes(final int podsPerNode, final int totalNumberOfPods) throws NoSolutionException {
        if (Math.ceil((double) totalNumberOfPods / podsPerNode) > MAX_NODES_PER_CLUSTER) {
            LOG.warn("Too many nodes in cluster: {}", Math.ceil((double) totalNumberOfPods / podsPerNode));

            throw new NoSolutionException("Too many nodes in cluster.");
        }

        return (int) Math.min(Math.ceil((double) totalNumberOfPods / podsPerNode), MAX_NODES_PER_CLUSTER);
    }
}
