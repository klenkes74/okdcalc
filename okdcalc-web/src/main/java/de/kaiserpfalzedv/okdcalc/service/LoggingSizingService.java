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

package de.kaiserpfalzedv.okdcalc.service;

import de.kaiserpfalzedv.okdcalc.calculator.LoggingClusterSizeCalculator;
import de.kaiserpfalzedv.okdcalc.facts.LoggingSizingResult;
import de.kaiserpfalzedv.okdcalc.facts._LoggingSizingRequest;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Set;

/**
 * Calculates the size of the logging cluster according to the
 * {@link de.kaiserpfalzedv.okdcalc.facts.LoggingEvent}s to be processed by the
 * cluster.
 *
 * @author rlichti
 * @version 1.0.0 2020-02-16
 * @since 1.0.0 2020-02-16
 */
@Path("/logging")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LoggingSizingService {
    @Inject
    LoggingClusterSizeCalculator calculator;

    @Counted(name = "performedLoggingSizings", description = "How many logging cluster sizings were calculated.")
    @Timed(name = "loggingSizingTimer", description = "A measure of how long it takes to perform the logging cluster sizing.", unit = MetricUnits.MILLISECONDS)
    @POST
    public Set<LoggingSizingResult> calculateSingleNodeType(_LoggingSizingRequest request) {
        return calculator.scoreNodetypes(request);
    }
}
