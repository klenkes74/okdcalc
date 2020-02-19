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

package de.kaiserpfalzedv.okdcalc;

import de.kaiserpfalzedv.okdcalc.facts.CPU;
import de.kaiserpfalzedv.okdcalc.facts.ClusterSizingRequest;
import de.kaiserpfalzedv.okdcalc.facts.NodeDefinition;
import de.kaiserpfalzedv.okdcalc.facts.Pod;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.kaiserpfalzedv.okdcalc.facts._ClusterSizingResult.GiB;
import static io.restassured.RestAssured.given;

/**
 * @author rlichti
 * @version 1.0.0 2020-02-16
 * @since 1.0.0 2020-02-16
 */
@QuarkusTest
@Tag("integration")
public class ClusterSizingTest {
    private static final Logger LOG = LoggerFactory.getLogger(ClusterSizingTest.class);


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
            .disk(4096 * GiB)
            .diskBandwidth(3 * GiB)
            .maxNumberOfLoggingDisks(8)
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
            .disk(4096 * GiB)
            .diskBandwidth(3 * GiB)
            .maxNumberOfLoggingDisks(8)
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
            .disk(4096 * GiB)
            .diskBandwidth(3 * GiB)
            .maxNumberOfLoggingDisks(8)
            .memory((long) 2 * 1024 * 1024 * 1024)
            .podsPerCore(10)
            .score(200L)
            .build();


    @Test
    public void shouldReturnAValidSizingWhenAlNodeTypesAreGiven() {
        given()
                .when()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(ClusterSizingRequest.builder()
                              .totalNumberOfPods(250)
                              .defaultPod(NORMAL_POD)
                              .addNodeDefinitions(
                                      SMALL_NODES,
                                      DEFAULT_NODE,
                                      BIG_NODES
                              )
                              .build()
                )
                .log().all(true)
                .post("/cluster")
                .then()
                .log().all(true)
                .statusCode(200);
    }
}
