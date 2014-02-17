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

import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.fluid.component.FluidComponent;
import org.terasology.fluid.component.FluidInventoryComponent;
import org.terasology.fluid.event.BeforeFluidPutInInventory;
import org.terasology.registry.CoreRegistry;
import org.terasology.registry.Share;

import java.util.List;

@RegisterSystem(value = RegisterMode.AUTHORITY)
@Share(value = {FluidManager.class})
public class FluidManagerImpl extends BaseComponentSystem implements FluidManager {
    @Override
    public boolean addFluid(EntityRef instigator, EntityRef container, String fluidType, float volume) {
        FluidInventoryComponent fluidInventory = container.getComponent(FluidInventoryComponent.class);
        if (fluidInventory == null) {
            return false;
        }

        List<EntityRef> fluidSlots = fluidInventory.fluidSlots;
        List<Float> maximumVolumes = fluidInventory.maximumVolumes;
        for (int i = 0; i < fluidSlots.size(); i++) {
            EntityRef fluidEntity = fluidSlots.get(i);
            FluidComponent fluid = fluidEntity.getComponent(FluidComponent.class);
            if (fluid != null && fluid.fluidType.equals(fluidType)) {
                float maximumVolume = maximumVolumes.get(i);
                if (fluid.volume + volume <= maximumVolume) {
                    fluid.volume += volume;
                    fluidEntity.saveComponent(fluid);
                    return true;
                }
            }
        }

        for (int i = 0; i < fluidSlots.size(); i++) {
            EntityRef fluidEntity = fluidSlots.get(i);
            FluidComponent fluid = fluidEntity.getComponent(FluidComponent.class);
            if (fluid == null) {
                float maximumVolume = maximumVolumes.get(i);
                if (volume <= maximumVolume) {
                    BeforeFluidPutInInventory beforePut = new BeforeFluidPutInInventory(instigator, fluidType, volume, i);
                    container.send(beforePut);
                    if (!beforePut.isConsumed()) {
                        EntityManager entityManager = CoreRegistry.get(EntityManager.class);

                        FluidComponent fluidComponent = new FluidComponent();
                        fluidComponent.fluidType = fluidType;
                        fluidComponent.volume = volume;

                        EntityRef newFluidEntity = entityManager.create(fluidComponent);
                        fluidSlots.set(i, newFluidEntity);
                        container.saveComponent(fluidInventory);

                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public boolean removeFluid(EntityRef instigator, EntityRef container, String fluidType, float volume) {
        FluidInventoryComponent fluidInventory = container.getComponent(FluidInventoryComponent.class);
        if (fluidInventory == null) {
            return false;
        }

        List<EntityRef> fluidSlots = fluidInventory.fluidSlots;
        for (int i = 0; i < fluidSlots.size(); i++) {
            if (removeFluid(instigator, container, i, fluidType, volume)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean removeFluid(EntityRef instigator, EntityRef container, int slot, String fluidType, float volume) {
        FluidInventoryComponent fluidInventory = container.getComponent(FluidInventoryComponent.class);
        if (fluidInventory == null) {
            return false;
        }

        EntityRef fluidEntity = fluidInventory.fluidSlots.get(slot);
        FluidComponent fluid = fluidEntity.getComponent(FluidComponent.class);
        if (fluid != null && fluid.fluidType.equals(fluidType) && fluid.volume >= volume) {
            if (fluid.volume == volume) {
                fluidEntity.destroy();
                fluidInventory.fluidSlots.set(slot, EntityRef.NULL);
                container.saveComponent(fluidInventory);
            } else {
                fluid.volume -= volume;
                fluidEntity.saveComponent(fluid);
            }
            return true;
        }

        return false;
    }
}
