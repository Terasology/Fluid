// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.fluid.event;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.event.Event;

/**
 * This event indicates that the volume of a fluid was changed while it was in an inventory.
 */
public class FluidVolumeChangedInInventory implements Event {

    /** The instigator of the action */
    private EntityRef instigator;

    /** The type of the fluid whose volume was changed */
    private String fluidType;

    /** The slot number of the inventory slot in which the volume of the fluid was changed */
    private int slot;

    /** The volume of the fluid before the change */
    private float volumeBefore;

    /** The volume of the fluid after the change */
    private float volumeAfter;

    /**
     * Parametrized constructor.
     *
     * @param instigator   The instigator of the action
     * @param fluidType    The type of the fluid
     * @param slot         The slot number in which the volume was changed
     * @param volumeBefore The volume before the change
     * @param volumeAfter  The volume after the change
     */
    public FluidVolumeChangedInInventory(EntityRef instigator, String fluidType, int slot, float volumeBefore, float volumeAfter) {
        this.instigator = instigator;
        this.fluidType = fluidType;
        this.slot = slot;
        this.volumeBefore = volumeBefore;
        this.volumeAfter = volumeAfter;
    }

    /**
     * Accessor function that returns the type of the fluid whose volume was changed.
     *
     * @return The type of the fluid whose volume was changed
     */
    public String getFluidType() {
        return fluidType;
    }

    /**
     * Accessor function that returns the instigator of the change in volume.
     *
     * @return The instigator of the event
     */
    public EntityRef getInstigator() {
        return instigator;
    }

    /**
     * Accessor function that returns the slot number in which the volume of the fluid was changed
     *
     * @return The slot number of the slot in which the volume of the fluid was changed
     */
    public int getSlot() {
        return slot;
    }

    /**
     * Accessor function that returns the volume before the change.
     *
     * @return The volume before the change
     */
    public float getVolumeBefore() {
        return volumeBefore;
    }

    /**
     * Accessor function that returns the volume after the change.
     *
     * @return The volume after the change
     */
    public float getVolumeAfter() {
        return volumeAfter;
    }
}
