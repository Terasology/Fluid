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

/**
 * Interface for a generic fluid manager.
 */
public interface FluidManager {
    /**
     * Add a certain volume of fluid to all fluid inventory slots.
     *
     * @param instigator    The entity that's instigating this action
     * @param container     The entity that houses the fluid inventory
     * @param fluidType     The type of fluid being added
     * @param volume        The volume of fluid being added
     */
    boolean addFluid(EntityRef instigator, EntityRef container, String fluidType, float volume);

    /**
     * Add a certain volume of fluid to a particular fluid inventory slot.
     *
     * @param instigator    The entity that's instigating this action
     * @param container     The entity that houses the fluid inventory
     * @param slot          The slot number of the fluid inventory that's intended to be filled
     * @param fluidType     The type of fluid being added
     * @param volume        The volume of fluid being added
     */
    boolean addFluid(EntityRef instigator, EntityRef container, int slot, String fluidType, float volume);

    /**
     * Add fluid to a particular fluid inventory slot from a fluid holder.
     *
     * @param instigator    The entity that's instigating this action
     * @param inventory     The entity that houses the fluid inventory
     * @param holder        The entity that houses the fluid holder that the fluid's being transferred from
     * @param slot          The slot number of the fluid inventory that's intended to be filled
     * @param fluidType     The type of fluid being added
     * @param volume        The volume of fluid being added
     */
    boolean addFluidFromHolder(EntityRef instigator, EntityRef inventory, EntityRef holder, int slot, String fluidType, float volume);

    /**
     * Remove a certain volume of fluid from all fluid inventory slots.
     *
     * @param instigator    The entity that's instigating this action
     * @param container     The entity that houses the fluid inventory
     * @param fluidType     The type of fluid being removed
     * @param volume        The volume of fluid being removed
     */
    boolean removeFluid(EntityRef instigator, EntityRef container, String fluidType, float volume);

    /**
     * Remove a certain volume of fluid from a particular fluid inventory slot.
     *
     * @param instigator    The entity that's instigating this action
     * @param container     The entity that houses the fluid inventory
     * @param slot          The slot number of the fluid inventory that's intended to be used
     * @param fluidType     The type of fluid being removed
     * @param volume        The volume of fluid being removed
     */
    boolean removeFluid(EntityRef instigator, EntityRef container, int slot, String fluidType, float volume);

    /**
     * Transfer fluid from one fluid inventory slot to another.
     *
     * @param instigator    The entity that's instigating this action
     * @param from          The entity that houses the source fluid inventory
     * @param to            The entity that houses the destination fluid inventory
     * @param slotFrom      The slot number of the source fluid inventory that's intended to be used
     * @param fluidType     The type of fluid being transferred
     * @param slotTo        The slot number of the destination fluid inventory that's intended to be used
     * @param volume        The volume of fluid being transferred
     */
    float moveFluid(EntityRef instigator, EntityRef from, EntityRef to, int slotFrom, String fluidType, int slotTo, float volume);
}
