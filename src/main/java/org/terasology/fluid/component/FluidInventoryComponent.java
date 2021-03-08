/*
 * Copyright 2014 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.fluid.component;

import com.google.common.collect.Lists;
import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.entitySystem.Owns;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.network.Replicate;

import java.util.List;

/**
 * Represents the fluid inventory of an entity which the entity uses to store fluids.
 */
public class FluidInventoryComponent implements Component {

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
}
