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
import org.terasology.fluid.event.BeforeFluidRemovedFromInventory;
import org.terasology.fluid.event.FluidVolumeChangedInInventory;
import org.terasology.network.NetworkComponent;
import org.terasology.registry.CoreRegistry;
import org.terasology.registry.Share;

import java.util.List;

@RegisterSystem(value = RegisterMode.AUTHORITY)
@Share(FluidManager.class)
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
                    float oldVolume = fluid.volume;
                    fluid.volume += volume;
                    float newVolume = fluid.volume;

                    fluidEntity.saveComponent(fluid);
                    container.saveComponent(fluidInventory);

                    container.send(new FluidVolumeChangedInInventory(instigator, fluidType, i, oldVolume, newVolume));

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
                        newFluidEntity.addComponent(new NetworkComponent());
                        fluidSlots.set(i, newFluidEntity);
                        container.saveComponent(fluidInventory);

                        container.send(new FluidVolumeChangedInInventory(instigator, fluidType, i, 0, volume));

                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public boolean addFluid(EntityRef instigator, EntityRef container, int slot, String fluidType, float volume) {
        FluidInventoryComponent fluidInventory = container.getComponent(FluidInventoryComponent.class);
        if (fluidInventory == null) {
            return false;
        }

        EntityRef fluidEntity = fluidInventory.fluidSlots.get(slot);
        FluidComponent fluid = fluidEntity.getComponent(FluidComponent.class);
        if (fluid != null && fluid.fluidType.equals(fluidType)) {
            float maximumVolume = fluidInventory.maximumVolumes.get(slot);
            if (fluid.volume + volume <= maximumVolume) {
                float oldVolume = fluid.volume;
                fluid.volume += volume;
                float newVolume = fluid.volume;
                fluidEntity.saveComponent(fluid);
                container.saveComponent(fluidInventory);

                container.send(new FluidVolumeChangedInInventory(instigator, fluidType, slot, oldVolume, newVolume));

                return true;
            }
        }

        if (fluid == null) {
            float maximumVolume = fluidInventory.maximumVolumes.get(slot);
            if (volume <= maximumVolume) {
                BeforeFluidPutInInventory beforePut = new BeforeFluidPutInInventory(instigator, fluidType, volume, slot);
                container.send(beforePut);
                if (!beforePut.isConsumed()) {
                    EntityManager entityManager = CoreRegistry.get(EntityManager.class);

                    FluidComponent fluidComponent = new FluidComponent();
                    fluidComponent.fluidType = fluidType;
                    fluidComponent.volume = volume;

                    EntityRef newFluidEntity = entityManager.create(fluidComponent);
                    fluidInventory.fluidSlots.set(slot, newFluidEntity);
                    container.saveComponent(fluidInventory);

                    container.send(new FluidVolumeChangedInInventory(instigator, fluidType, slot, 0, volume));

                    return true;
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
            BeforeFluidRemovedFromInventory beforePut = new BeforeFluidRemovedFromInventory(instigator, fluidType, volume, slot);
            container.send(beforePut);
            if (!beforePut.isConsumed()) {
                removeFluidFromContainer(instigator, container, fluidType, slot, volume, fluidInventory, fluidEntity, fluid);
                return true;
            }
        }

        return false;
    }

    private void removeFluidFromContainer(EntityRef instigator, EntityRef container, String fluidType, int slot, float volume,
                                          FluidInventoryComponent fluidInventory, EntityRef fluidEntity, FluidComponent fluid) {
        float volumeBefore = fluid.volume;
        float volumeAfter;
        if (fluid.volume == volume) {
            fluidEntity.destroy();
            fluidInventory.fluidSlots.set(slot, EntityRef.NULL);
            volumeAfter = 0;
        } else {
            fluid.volume -= volume;
            fluidEntity.saveComponent(fluid);
            volumeAfter = fluid.volume;
        }
        container.saveComponent(fluidInventory);
        container.send(new FluidVolumeChangedInInventory(instigator, fluidType, slot, volumeBefore, volumeAfter));
    }

    @Override
    public float moveFluid(EntityRef instigator, EntityRef from, EntityRef to, int slotFrom, String fluidType, int slotTo, float volume) {
        if (volume <= 0) {
            return 0;
        }
        FluidInventoryComponent fluidInventoryFrom = from.getComponent(FluidInventoryComponent.class);
        FluidInventoryComponent fluidInventoryTo = to.getComponent(FluidInventoryComponent.class);
        if (fluidInventoryFrom == null || fluidInventoryTo == null) {
            return 0;
        }

        EntityRef fluidEntityFrom = fluidInventoryFrom.fluidSlots.get(slotFrom);
        FluidComponent fluidFrom = fluidEntityFrom.getComponent(FluidComponent.class);

        EntityRef fluidEntityTo = fluidInventoryTo.fluidSlots.get(slotTo);
        FluidComponent fluidTo = fluidEntityTo.getComponent(FluidComponent.class);

        // Ignore the command when either:
        // 1. There is no fluid in the from entity, or is of different type
        // 2. There is a fluid in the to entity of a different type
        // 3. The volume in the from is lower than the volume requested to be moved
        if (fluidFrom == null || !fluidFrom.fluidType.equals(fluidType)
                || (fluidTo != null && !fluidTo.fluidType.equals(fluidType))
                || fluidFrom.volume < volume) {
            return 0;
        }

        float maximumTargetVolume = fluidInventoryTo.maximumVolumes.get(slotTo);
        float volumeToMove;
        if (fluidTo == null) {
            volumeToMove = Math.min(volume, maximumTargetVolume);
        } else {
            volumeToMove = Math.min(volume, maximumTargetVolume - fluidTo.volume);
        }

        BeforeFluidRemovedFromInventory beforeRemoved = new BeforeFluidRemovedFromInventory(instigator, fluidType, volumeToMove, slotFrom);
        from.send(beforeRemoved);
        if (beforeRemoved.isConsumed()) {
            return 0;
        }

        if (fluidTo == null) {
            BeforeFluidPutInInventory beforePut = new BeforeFluidPutInInventory(instigator, fluidType, volumeToMove, slotTo);
            to.send(beforePut);
            if (beforePut.isConsumed()) {
                return 0;
            }
        }

        removeFluidFromContainer(instigator, from, fluidType, slotFrom, volumeToMove, fluidInventoryFrom, fluidEntityFrom, fluidFrom);

        if (fluidTo == null) {
            EntityManager entityManager = CoreRegistry.get(EntityManager.class);

            FluidComponent fluidComponent = new FluidComponent();
            fluidComponent.fluidType = fluidType;
            fluidComponent.volume = volumeToMove;

            EntityRef newFluidEntity = entityManager.create(fluidComponent);
            fluidInventoryTo.fluidSlots.set(slotTo, newFluidEntity);
            to.saveComponent(fluidInventoryTo);
        } else {
            fluidTo.volume += volumeToMove;
            fluidEntityTo.saveComponent(fluidTo);
        }

        return volumeToMove;
    }
}
