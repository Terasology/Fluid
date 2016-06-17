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
package org.terasology.fluid.system;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.fluid.component.FluidComponent;
import org.terasology.fluid.component.FluidContainerItemComponent;
import org.terasology.fluid.component.FluidInventoryComponent;

/**
 * A set of utilities for managing fluids.
 */
public final class FluidUtils {
    private FluidUtils() {
    }

    /**
     * Sets the fluid type of a fluid container.
     *
     * @param container     Reference to entity that houses the fluid container item.
     * @param fluidType     Name of the fluid type.
     */
    public static void setFluidForContainerItem(EntityRef container, String fluidType) {
        FluidContainerItemComponent resultContainer = container.getComponent(FluidContainerItemComponent.class);
        resultContainer.fluidType = fluidType;

        // If the fluid type is set to null, empty the container.
        if (fluidType == null) {
            resultContainer.volume = 0;
        }

        container.saveComponent(resultContainer);
    }

    /**
     * Sets the fluid type and the current volume of a fluid container.
     *
     * @param container     Reference to entity that houses the fluid container item.
     * @param fluidType     Name of the fluid type.
     * @param volume        The amount of fluid being set.
     */
    public static void setFluidForContainerItem(EntityRef container, String fluidType, float volume) {
        FluidContainerItemComponent resultContainer = container.getComponent(FluidContainerItemComponent.class);
        resultContainer.fluidType = fluidType;

        // If the fluid type is set to null, empty the container.
        if (fluidType == null) {
            resultContainer.volume = 0;
        }
        // If it's not, set the volume of this container to the passed volume argument.
        else {
            resultContainer.volume = volume;
        }

        container.saveComponent(resultContainer);
    }

    /**
     * Get the fluid type of the fluid stored in this particular fluid inventory slot.
     *
     * @param entity        Reference to entity that houses the fluid inventory component.
     * @param slot          Slot number of the fluid inventory to access.
     */
    public static String getFluidAt(EntityRef entity, int slot) {
        FluidInventoryComponent fluidInventoryComponent = entity.getComponent(FluidInventoryComponent.class);
        if (fluidInventoryComponent != null) {
            EntityRef fluidEntity = fluidInventoryComponent.fluidSlots.get(slot);
            FluidComponent fluid = fluidEntity.getComponent(FluidComponent.class);
            if (fluid != null) {
                return fluid.fluidType;
            }
        }

        return null;
    }

    /**
     * Get the volume of fluid stored in this particular fluid inventory slot.
     *
     * @param entity        Reference to entity that houses the fluid inventory component.
     * @param slot          Slot number of the fluid inventory to access.
     */
    public static float getFluidAmount(EntityRef entity, int slot) {
        FluidInventoryComponent fluidInventoryComponent = entity.getComponent(FluidInventoryComponent.class);
        if (fluidInventoryComponent != null) {
            EntityRef fluidEntity = fluidInventoryComponent.fluidSlots.get(slot);
            FluidComponent fluid = fluidEntity.getComponent(FluidComponent.class);
            if (fluid != null) {
                return fluid.volume;
            }
        }

        return 0;
    }

    /**
     * Get the number of slots present in this fluid inventory.
     *
     * @param entity        Reference to entity that houses the fluid inventory component.
     */
    public static int getFluidSlotCount(EntityRef entity) {
        FluidInventoryComponent fluidInventoryComponent = entity.getComponent(FluidInventoryComponent.class);
        if (fluidInventoryComponent != null) {
            return fluidInventoryComponent.fluidSlots.size();
        }
        return 0;
    }
}
