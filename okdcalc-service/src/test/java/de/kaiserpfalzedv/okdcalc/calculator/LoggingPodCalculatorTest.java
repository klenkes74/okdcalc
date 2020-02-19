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

import de.kaiserpfalzedv.okdcalc.facts.CPU;
import de.kaiserpfalzedv.okdcalc.facts.LoggingEvent;
import de.kaiserpfalzedv.okdcalc.facts.LoggingSizingRequest;
import de.kaiserpfalzedv.okdcalc.facts.NodeDefinition;
import de.kaiserpfalzedv.okdcalc.facts._LoggingSizingResult;
import de.kaiserpfalzedv.okdcalc.facts._NodeDefinition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.kaiserpfalzedv.okdcalc.facts._ClusterSizingResult.GiB;

/**
 * @author rlichti
 * @version 1.0.0 2020-02-19
 * @since 1.0.0 2020-02-19
 */
public class LoggingPodCalculatorTest {
    private static final Logger LOG = LoggerFactory.getLogger(LoggingPodCalculatorTest.class);

    private static final _NodeDefinition SMALL_INFRA_NODE = NodeDefinition.builder()
            .score(1000)
            .cpu(CPU.builder()
                         .sockets(4)
                         .cores(8)
                         .logical(16)
                         .build()
            )
            .memory(64 * GiB)
            .disk(2048 * GiB)
            .diskBandwidth(GiB)
            .maxNumberOfLoggingDisks(8)
            .build();

    private static final _NodeDefinition MEDIUM_INFRA_NODE = NodeDefinition.builder()
            .score(3000)
            .cpu(CPU.builder()
                         .sockets(8)
                         .cores(16)
                         .logical(32)
                         .build()
            )
            .memory(128 * GiB)
            .disk(4096 * GiB)
            .diskBandwidth(3 * GiB)
            .maxNumberOfLoggingDisks(8)
            .build();

    private static final _NodeDefinition BIG_INFRA_NODE = NodeDefinition.builder()
            .score(8000)
            .cpu(CPU.builder()
                         .sockets(16)
                         .cores(32)
                         .logical(64)
                         .build()
            )
            .memory(256 * GiB)
            .disk(4096 * GiB)
            .diskBandwidth(6 * GiB)
            .maxNumberOfLoggingDisks(8)
            .build();


    private LoggingPodCalculator service;


    @Test
    public void shouldReturnASizingWhenSmallNodesAreGiven() throws NoSolutionException {
        LoggingSizingRequest sizingRequest = LoggingSizingRequest.builder()
                .addPossibleInfraNodeTypes(SMALL_INFRA_NODE, MEDIUM_INFRA_NODE, BIG_INFRA_NODE)
                .addLoggingEventTypes(LoggingEvent.builder()
                                              .defaultNumberOfEventsPerHour(5 * 3600)
                                              .eventSize(256)
                                              .numberOfContainers(4000)
                                              .numberOfNamespaces(400)
                                              .retentionDays(28)
                                              .build()
                )
                .numberOfAvailabilityZones(3)
                .numberOfComputeNodes(30)
                .parallelKibanaUsers(50)
                .numberOfReplica(1)
                .build();

        _LoggingSizingResult result = service.calculateLoggingSolution(SMALL_INFRA_NODE, sizingRequest);
        LOG.trace("result: {}", result);

        assert result.getScore() == 12000;
    }


    @Test
    public void shouldReturnASizingWhenMediumNodesAreGiven() throws NoSolutionException {
        LoggingSizingRequest sizingRequest = LoggingSizingRequest.builder()
                .addPossibleInfraNodeTypes(SMALL_INFRA_NODE, MEDIUM_INFRA_NODE, BIG_INFRA_NODE)
                .addLoggingEventTypes(LoggingEvent.builder()
                                              .defaultNumberOfEventsPerHour(5 * 3600)
                                              .eventSize(256)
                                              .numberOfContainers(4000)
                                              .numberOfNamespaces(400)
                                              .retentionDays(28)
                                              .build()
                )
                .numberOfAvailabilityZones(3)
                .numberOfComputeNodes(30)
                .parallelKibanaUsers(50)
                .numberOfReplica(1)
                .build();

        _LoggingSizingResult result = service.calculateLoggingSolution(MEDIUM_INFRA_NODE, sizingRequest);
        LOG.trace("result: {}", result);

        assert result.getScore() == 18000;
    }


    @Test
    public void shouldReturnASizingWhenBigNodesAreGiven() throws NoSolutionException {
        LoggingSizingRequest sizingRequest = LoggingSizingRequest.builder()
                .addPossibleInfraNodeTypes(SMALL_INFRA_NODE, MEDIUM_INFRA_NODE, BIG_INFRA_NODE)
                .addLoggingEventTypes(LoggingEvent.builder()
                                              .defaultNumberOfEventsPerHour(5 * 3600)
                                              .eventSize(256)
                                              .numberOfContainers(4000)
                                              .numberOfNamespaces(400)
                                              .retentionDays(28)
                                              .build()
                )
                .numberOfAvailabilityZones(3)
                .numberOfComputeNodes(30)
                .parallelKibanaUsers(50)
                .numberOfReplica(1)
                .build();

        _LoggingSizingResult result = service.calculateLoggingSolution(BIG_INFRA_NODE, sizingRequest);
        LOG.trace("result: {}", result);

        assert result.getScore() == 48000;
    }


    @BeforeEach
    public void setUpService() {
        service = new LoggingPodCalculator();
    }
}
