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

import de.kaiserpfalzedv.okdcalc.facts.SizingResult;
import de.kaiserpfalzedv.okdcalc.facts._NodeDefinition;
import de.kaiserpfalzedv.okdcalc.facts._Pod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author rlichti
 * @version 1.0.0 2020-02-16
 * @since 1.0.0 2020-02-16
 */
@Dependent
public class NodeTypeCalculator {
    private static final Logger LOG = LoggerFactory.getLogger(NodeTypeCalculator.class);

    @Inject
    NumbersOfPodsAndNodesCalculator calculator;

    public NodeTypeCalculator() {
        calculator = new NumbersOfPodsAndNodesCalculator();
    }

    public NodeTypeCalculator(final NumbersOfPodsAndNodesCalculator calculator) {
        this.calculator = calculator;
    }

    public Set<SizingResult> scoreNodetypes(
            final int totalNumberOfPods,
            final _Pod defaultPod,
            final Collection<_NodeDefinition> nodeDefintions
    ) {
        HashSet<SizingResult> result = new HashSet<>(nodeDefintions.size());

        for (_NodeDefinition d : nodeDefintions) {
            try {
                result.add(calculator.calculateNumberOfPodsPerNodeAndNumberOfNodes(totalNumberOfPods, defaultPod, d));
            } catch (NoSolutionException e) {
                LOG.warn("No solution for: {}", d);
            }
        }

        return result;
    }
}
