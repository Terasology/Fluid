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
package org.terasology.fluid.event;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.AbstractConsumableEvent;

/**
 * This event indicates that an entity is removing a fluid in from inventory slot, and contains attributes indicating
 * the state of the fluid before it was removed from the inventory.
 */
public class BeforeFluidRemovedFromInventory extends AbstractConsumableEvent {

    /** The entity who is removing the fluid from the inventory */
    private EntityRef instigator;

    /** The type of the fluid being removed from the inventory */
    private String fluidType;

    /** The volume of the fluid being removed from the inventory */
    private float volume;

    /** The slot number of the inventory slot from which the fluid is being removed */
    private int slot;

    /**
     * Parametrized constructor.
     *
     * @param instigator The entity who is removing the fluid from the inventory
     * @param fluidType  The type of the fluid being removed from the inventory
     * @param volume     The volume of the fluid being removed from the inventory
     * @param slot       The slot number of the inventory slot from which the fluid is being removed
     */
    public BeforeFluidRemovedFromInventory(EntityRef instigator, String fluidType, float volume, int slot) {
        this.instigator = instigator;
        this.fluidType = fluidType;
        this.volume = volume;
        this.slot = slot;
    }

    /**
     * Accessor function that returns the fluid type.
     *
     * @return The fluid type of the fluid being removed from the inventory
     */
    public String getFluidType() {
        return fluidType;
    }

    /**
     * Accessor function that returns the instigator of the action.
     *
     * @return An EntityRef to the entity who is removing the fluid from the inventory
     */
    public EntityRef getInstigator() {
        return instigator;
    }

    /**
     * Accessor function that returns the slot number of the slot from which the fluid is being removed.
     *
     * @return The slot number of the inventory slot from which the fluid is being removed
     */
    public int getSlot() {
        return slot;
    }

    /**
     * Accessor function that returns the volume of the fluid being removed from the inventory.
     *
     * @return The volume of the fluid being removed from the inventory
     */
    public float getVolume() {
        return volume;
    }
}
