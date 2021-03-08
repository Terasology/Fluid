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

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.AbstractConsumableEvent;

/**
 * This event indicates that an entity is placing a fluid in an inventory slot, and contains attributes indicating the
 * state of the fluid before it was placed in the inventory.
 */
public class BeforeFluidPutInInventory extends AbstractConsumableEvent {

    /** The entity which is placing the fluid in the inventory */
    private EntityRef instigator;

    /** The type of the fluid being placed in the inventory */
    private String fluidType;

    /** The volume of the fluid being placed in the inventory */
    private float volume;

    /** The slot number of the inventory slot in which the fluid is being placed */
    private int slot;

    /**
     * Parametrized constructor.
     *
     * @param instigator The entity which is placing the fluid in the inventory
     * @param fluidType  The type of the fluid being placed in the inventory
     * @param volume     The volume of the fluid being placed in the inventory
     * @param slot       The slot number of the inventory slot in which the fluid is being placed
     */
    public BeforeFluidPutInInventory(EntityRef instigator, String fluidType, float volume, int slot) {
        this.instigator = instigator;
        this.fluidType = fluidType;
        this.volume = volume;
        this.slot = slot;
    }

    /**
     * Accessor function that returns the fluid type.
     *
     * @return The fluid type of the fluid being placed in the inventory
     */
    public String getFluidType() {
        return fluidType;
    }

    /**
     * Accessor function that returns the instigator of the action.
     *
     * @return An EntityRef to the entity which is placing the fluid in the inventory
     */
    public EntityRef getInstigator() {
        return instigator;
    }

    /**
     * Accessor function that returns the slot number of the slot in which the fluid is being placed.
     *
     * @return The slot number of the inventory slot in which the fluid is being placed
     */
    public int getSlot() {
        return slot;
    }

    /**
     * Accessor function that returns the volume of the fluid being placed in the inventory.
     *
     * @return The volume of the fluid being placed in the inventory
     */
    public float getVolume() {
        return volume;
    }
}
