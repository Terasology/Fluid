// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.fluid.component;

import org.terasology.engine.network.Replicate;
import org.terasology.gestalt.entitysystem.component.Component;

/**
 * This component indicates that an entity is a fluid, and contains basic attributes of the fluid.
 */
public class FluidComponent implements Component<FluidComponent> {
    /** The type of the fluid */
    @Replicate
    public String fluidType;

    /** The volume of the fluid */
    @Replicate
    public float volume;

    @Override
    public void copy(FluidComponent other) {
        this.fluidType = other.fluidType;
        this.volume = other.volume;
    }
}
