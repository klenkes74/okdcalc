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
import de.kaiserpfalzedv.okdcalc.facts._NodeDefinition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * @author rlichti
 * @version 1.0.0 2020-02-15
 * @since 1.0.0 2020-02-15
 */
public class NodeTypeCalculatorTest {
    private static final Logger LOG = LoggerFactory.getLogger(NodeTypeCalculatorTest.class);


    private static final Pod NORMAL_POD = Pod.builder()
            .memory(1024L)
            .milliCores(100L)
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
            .score(5000L)
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
            .memory((long) 2 * 1024 * 1024 * 1024)
            .podsPerCore(10)
            .score(200L)
            .build();

    private NodeTypeCalculator service;

    @Test
    public void shouldScoreAllDefitionsWhenGivenThreeValidNodeDefitions() {
        HashSet<_NodeDefinition> nodeTypes = new HashSet<>();
        nodeTypes.add(SMALL_NODES);
        nodeTypes.add(DEFAULT_NODE);
        nodeTypes.add(BIG_NODES);

        Set<SizingResult> result = service.scoreNodetypes(250, NORMAL_POD, nodeTypes);
        LOG.debug("Result: {}", result);

        assert result.size() == 3;
        assert result.toArray(new SizingResult[0])[0].getScore() == 14 * 200L;
        assert result.toArray(new SizingResult[0])[1].getScore() == 2 * 1000L;
        assert result.toArray(new SizingResult[0])[2].getScore() == 5000L;
    }

    @Test
    public void shouldScoreOnlyTwoDefinitionsWhenGivenTwoValidAndOneInvalidDefinition() {
        HashSet<_NodeDefinition> nodeTypes = new HashSet<>();
        nodeTypes.add(SMALL_NODES);
        nodeTypes.add(DEFAULT_NODE);
        nodeTypes.add(BIG_NODES);

        Set<SizingResult> result = service.scoreNodetypes(250, NORMAL_POD.withMilliCores(3000L), nodeTypes);
        LOG.debug("Result: {}", result);

        assert result.size() == 2;
        assert result.toArray(new SizingResult[0])[0].getScore() == 50 * 1000L;
        assert result.toArray(new SizingResult[0])[1].getScore() == 12 * 5000L;
    }

    @Test
    public void shouldCreateAValidNodeTypeCalculatorWhenAnotherCalculatorIsGiven() {
        NumbersOfPodsAndNodesCalculator calculator = new NumbersOfPodsAndNodesCalculator();

        assert !service.calculator.equals(calculator);

        service = new NodeTypeCalculator(calculator);

        assert calculator.equals(service.calculator);
    }

    @BeforeEach
    public void setUpService() {
        service = new NodeTypeCalculator();
    }
}
