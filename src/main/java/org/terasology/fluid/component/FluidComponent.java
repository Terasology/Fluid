// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.fluid.component;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.network.Replicate;

/**
 * This component indicates that an entity is a fluid, and contains basic attributes of the fluid.
 */
public class FluidComponent implements Component {
    /**
     * The type of the fluid
     */
    @Replicate
    public String fluidType;

    /**
     * The volume of the fluid
     */
    @Replicate
    public float volume;
}
