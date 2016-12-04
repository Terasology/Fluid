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
     * @param instigator    the entity that's instigating this action
     * @param container     the entity that houses the fluid inventory
     * @param fluidType     the type of fluid being added
     * @param volume        the volume of fluid being added
     */
    boolean addFluid(EntityRef instigator, EntityRef container, String fluidType, float volume);

    /**
     * Add a certain volume of fluid to a particular fluid inventory slot.
     *
     * @param instigator    the entity that's instigating this action
     * @param container     the entity that houses the fluid inventory
     * @param slot          the slot number of the fluid inventory that's intended to be filled
     * @param fluidType     the type of fluid being added
     * @param volume        the volume of fluid being added
     */
    boolean addFluid(EntityRef instigator, EntityRef container, int slot, String fluidType, float volume);

    /**
     * Add fluid to a particular fluid inventory slot from a fluid holder.
     *
     * @param instigator    the entity that's instigating this action
     * @param inventory     the entity that houses the fluid inventory
     * @param holder        the entity that houses the fluid holder that the fluid's being transferred from
     * @param slot          the slot number of the fluid inventory that's intended to be filled
     * @param fluidType     the type of fluid being added
     * @param volume        the volume of fluid being added
     */
    boolean addFluidFromHolder(EntityRef instigator, EntityRef inventory, EntityRef holder, int slot, String fluidType, float volume);

    /**
     * Remove a certain volume of fluid from all fluid inventory slots.
     *
     * @param instigator    the entity that's instigating this action
     * @param container     the entity that houses the fluid inventory
     * @param fluidType     the type of fluid being removed
     * @param volume        the volume of fluid being removed
     */
    boolean removeFluid(EntityRef instigator, EntityRef container, String fluidType, float volume);

    /**
     * Remove a certain volume of fluid from a particular fluid inventory slot.
     *
     * @param instigator    the entity that's instigating this action
     * @param container     the entity that houses the fluid inventory
     * @param slot          the slot number of the fluid inventory that's intended to be used
     * @param fluidType     the type of fluid being removed
     * @param volume        the volume of fluid being removed
     */
    boolean removeFluid(EntityRef instigator, EntityRef container, int slot, String fluidType, float volume);

    /**
     * Transfer fluid from one fluid inventory slot to another.
     *
     * @param instigator    the entity that's instigating this action
     * @param from          the entity that houses the source fluid inventory
     * @param to            the entity that houses the destination fluid inventory
     * @param slotFrom      the slot number of the source fluid inventory that's intended to be used
     * @param fluidType     the type of fluid being transferred
     * @param slotTo        the slot number of the destination fluid inventory that's intended to be used
     * @param volume        the volume of fluid being transferred
     */
    float moveFluid(EntityRef instigator, EntityRef from, EntityRef to, int slotFrom, String fluidType, int slotTo, float volume);
}
