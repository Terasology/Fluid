/*
 * Copyright 2015 MovingBlocks
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
import org.terasology.entitySystem.event.Event;

public class FluidVolumeChangedInInventory implements Event {
    private EntityRef instigator;
    private String fluidType;
    private int slot;
    private float volumeBefore;
    private float volumeAfter;

    public FluidVolumeChangedInInventory(EntityRef instigator, String fluidType, int slot, float volumeBefore, float volumeAfter) {
        this.instigator = instigator;
        this.fluidType = fluidType;
        this.slot = slot;
        this.volumeBefore = volumeBefore;
        this.volumeAfter = volumeAfter;
    }

    public String getFluidType() {
        return fluidType;
    }

    public EntityRef getInstigator() {
        return instigator;
    }

    public int getSlot() {
        return slot;
    }

    public float getVolumeBefore() {
        return volumeBefore;
    }

    public float getVolumeAfter() {
        return volumeAfter;
    }
}
