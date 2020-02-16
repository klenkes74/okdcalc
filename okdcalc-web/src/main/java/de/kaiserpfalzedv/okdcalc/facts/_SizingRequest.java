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
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.immutables.value.Value;

import java.util.Set;

/**
 * @author rlichti
 * @version 1.0.0 2020-02-16
 * @since 1.0.0 2020-02-16
 */
@Immutable
@JsonSerialize(as = SizingRequest.class)
@JsonDeserialize(builder = SizingRequest.Builder.class)
@RegisterForReflection
public interface _SizingRequest {
    int getTotalNumberOfPods();

    _Pod getDefaultPod();

    Set<_NodeDefinition> getNodeDefinitions();

    @Value.Default
    default _OkdVersion getVersion() {
        return OkdVersion.builder()
                .major(3)
                .minor(11)
                .patchlevel(0)
                .build();
    }
}
