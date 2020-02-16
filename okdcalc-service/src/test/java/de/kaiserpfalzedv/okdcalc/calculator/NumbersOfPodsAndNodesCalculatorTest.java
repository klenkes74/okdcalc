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
import de.kaiserpfalzedv.okdcalc.facts.NodeDefinition;
import de.kaiserpfalzedv.okdcalc.facts.Pod;
import de.kaiserpfalzedv.okdcalc.facts.SizingResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author rlichti
 * @version 1.0.0 2020-02-15
 * @since 1.0.0 2020-02-15
 */
public class NumbersOfPodsAndNodesCalculatorTest {
    private static final Logger LOG = LoggerFactory.getLogger(NumbersOfPodsAndNodesCalculatorTest.class);

    private static final Pod SMALL_POD = Pod.builder()
            .memory(100000L)
            .milliCores(100L)
            .build();

    private static final Pod NORMAL_POD = Pod.builder()
            .memory(1024000L)
            .milliCores(100L)
            .build();

    private static final Pod BIG_PODS_MEMORY = Pod.builder()
            .memory(8192000L)
            .milliCores(100L)
            .build();

    private static final Pod BIG_PODS_CPU = Pod.builder()
            .memory(100000L)
            .milliCores(100000L)
            .build();

    private static final NodeDefinition DEFAULT_NODE = NodeDefinition.builder()
            .cpu(
                    CPU.builder()
                            .sockets(1)
                            .cores(8)
                            .logical(16)
                            .build()
            )
            .disk((long) 1024 * 1024 * 1024 * 1024)
            .memory((long) 64 * 1024 * 1024 * 1024)
            .podsPerCore(10)
            .build();

    private static final NodeDefinition BIG_NODES = NodeDefinition.builder()
            .cpu(
                    CPU.builder()
                            .sockets(4)
                            .cores(32)
                            .logical(64)
                            .build()
            )
            .disk((long) 1024 * 1024 * 1024 * 1024)
            .memory((long) 256 * 1024 * 1024 * 1024)
            .podsPerCore(10)
            .build();

    private static final NodeDefinition SMALL_NODES = NodeDefinition.builder()
            .cpu(
                    CPU.builder()
                            .sockets(1)
                            .cores(1)
                            .logical(2)
                            .build()
            )
            .disk((long) 1024 * 1024 * 1024 * 1024)
            .memory((long) 2 * 1024 * 1024)
            .podsPerCore(10)
            .build();

    private NumbersOfPodsAndNodesCalculator service;

    @Test
    public void shouldReturn2NodesWith145PodsWhen250SmallPodsAreGiven() throws NoSolutionException {
        SizingResult result = service.calculateNumberOfPodsPerNodeAndNumberOfNodes(250, SMALL_POD, DEFAULT_NODE);
        LOG.debug("Result: {}", result);

        assert result.getPodsPerNode() == 145;
        assert result.getNodes() == 2;
    }

    @Test
    public void shouldReturn32NodesWith250PodsWhen8000NormalPodsAndBigNodesAreGiven() throws NoSolutionException {
        SizingResult result = service.calculateNumberOfPodsPerNodeAndNumberOfNodes(8000, NORMAL_POD, BIG_NODES);
        LOG.debug("Result: {}", result);

        assert result.getPodsPerNode() == 250;
        assert result.getNodes() == 32;
    }

    @Test
    public void shouldThrowNoSolutionExceptionWhenNotEnoughMemoryPerNodeIsGiven() {
        try {
            SizingResult result = service.calculateNumberOfPodsPerNodeAndNumberOfNodes(10, BIG_PODS_MEMORY, SMALL_NODES);
            LOG.debug("Result: {}", result);
            Assertions.fail("A NoSolutionException should have been thrown!");
        } catch (NoSolutionException e) {
            // everything is fine.
        }
    }

    @Test
    public void shouldThrowNoSolutionExceptionWhenNotEnoughCPUPerNodeIsGiven() {
        try {
            SizingResult result = service.calculateNumberOfPodsPerNodeAndNumberOfNodes(10, BIG_PODS_CPU, SMALL_NODES);
            LOG.debug("Result: {}", result);
            Assertions.fail("A NoSolutionException should have been thrown!");
        } catch (NoSolutionException e) {
            // everything is fine.
        }
    }

    @Test
    public void shouldThrowNoSolutionExceptionWhenTooManyNodesWouldBeUsed() {
        try {
            SizingResult result = service.calculateNumberOfPodsPerNodeAndNumberOfNodes(100000000, NORMAL_POD, DEFAULT_NODE);
            LOG.debug("Result: {}", result);
            Assertions.fail("A NoSolutionException should have been thrown!");
        } catch (NoSolutionException e) {
            // everything is fine.
        }
    }


    @BeforeEach
    public void setUpService() {
        service = new NumbersOfPodsAndNodesCalculator();
    }
}
