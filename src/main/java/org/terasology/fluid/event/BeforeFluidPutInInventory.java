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
 * This event indicates that an entity is putting a fluid in an inventory slot, and contains attributes indicating the
 * state of the fluid before it was put in the inventory.
 */
public class BeforeFluidPutInInventory extends AbstractConsumableEvent {
    /** The entity who is putting the fluid in the inventory */
    private EntityRef instigator;
    /** The type of the fluid being put in the inventory */
    private String fluidType;
    /** The volume of the fluid being put in the inventory */
    private float volume;
    /** The slot number of the inventory slot in which the fluid is being put */
    private int slot;

    /**
     * Parametrized constructor.
     *
     * @param instigator the entity who is putting the fluid in the inventory
     * @param fluidType  the type of the fluid being put in the inventory
     * @param volume     the volume of the fluid being put in the inventory
     * @param slot       the slot number of the inventory slot in which the fluid is being put
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
     * @return the fluid type of the fluid being put in the inventory
     */
    public String getFluidType() {
        return fluidType;
    }

    /**
     * Accessor function that returns the instigator of the action.
     *
     * @return an EntityRef to the entity who is putting the fluid in the inventory
     */
    public EntityRef getInstigator() {
        return instigator;
    }

    /**
     * Accessor function that returns the slot number of the slot in which the fluid is being put.
     *
     * @return the slot number of the inventory slot in which the fluid is being put
     */
    public int getSlot() {
        return slot;
    }

    /**
     * Accessor function that returns the volume of the fluid being put in the inventory.
     *
     * @return the volume of the fluid being put in the inventory
     */
    public float getVolume() {
        return volume;
    }
}
