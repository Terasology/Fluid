// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.fluid.event;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.AbstractConsumableEvent;

/**
 * This event indicates that an entity is placing a fluid in an inventory slot, and contains attributes indicating the
 * state of the fluid before it was placed in the inventory.
 */
public class BeforeFluidPutInInventory extends AbstractConsumableEvent {

    /**
     * The entity which is placing the fluid in the inventory
     */
    private final EntityRef instigator;

    /**
     * The type of the fluid being placed in the inventory
     */
    private final String fluidType;

    /**
     * The volume of the fluid being placed in the inventory
     */
    private final float volume;

    /**
     * The slot number of the inventory slot in which the fluid is being placed
     */
    private final int slot;

    /**
     * Parametrized constructor.
     *
     * @param instigator The entity which is placing the fluid in the inventory
     * @param fluidType The type of the fluid being placed in the inventory
     * @param volume The volume of the fluid being placed in the inventory
     * @param slot The slot number of the inventory slot in which the fluid is being placed
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
