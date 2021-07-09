// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.fluid.component;

import org.terasology.engine.math.IntegerRange;
import org.terasology.gestalt.entitysystem.component.Component;

import java.util.Map;

/**
 * A component for integration with a Computer module.
 */
public class FluidInventoryAccessComponent implements Component<FluidInventoryAccessComponent> {
    public Map<String, IntegerRange> input;
    public Map<String, IntegerRange> output;

    @Override
    public void copy(FluidInventoryAccessComponent other) {
        this.input.clear();
        for (Map.Entry<String, IntegerRange> entry : other.input.entrySet()) {
            this.input.put(entry.getKey(), entry.getValue().copy());
        }
        this.output.clear();
        for (Map.Entry<String, IntegerRange> entry : other.output.entrySet()) {
            this.output.put(entry.getKey(), entry.getValue().copy());
        }
    }
}
