// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.fluid.component;

import com.google.common.collect.Lists;
import org.terasology.engine.entitySystem.Owns;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.network.Replicate;
import org.terasology.gestalt.entitysystem.component.Component;

import java.util.List;

/**
 * Represents the fluid inventory of an entity which the entity uses to store fluids.
 */
public class FluidInventoryComponent implements Component<FluidInventoryComponent> {

    /** A list of fluid slots which fluids can occupy */
    @Replicate
    @Owns
    public List<EntityRef> fluidSlots = Lists.newLinkedList();

    /** A list of the maximum values of the fluid slots */
    @Replicate
    public List<Float> maximumVolumes = Lists.newLinkedList();

    /**
     * Default constructor.
     */
    public FluidInventoryComponent() {
    }

    /**
     * Parametrized constructor.
     *
     * @param numSlots      The number of slots in the fluid inventory
     * @param maximumVolume The maximum volume of fluid that a slot can contain
     */
    public FluidInventoryComponent(int numSlots, float maximumVolume) {
        for (int i = 0; i < numSlots; ++i) {
            fluidSlots.add(EntityRef.NULL);
            maximumVolumes.add(maximumVolume);
        }
    }

    @Override
    public void copyFrom(FluidInventoryComponent other) {
        this.fluidSlots = Lists.newLinkedList(this.fluidSlots);
        this.maximumVolumes = Lists.newLinkedList(maximumVolumes);
    }
}
