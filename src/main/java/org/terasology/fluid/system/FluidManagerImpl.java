// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.fluid.system;

import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.network.NetworkComponent;
import org.terasology.engine.registry.CoreRegistry;
import org.terasology.engine.registry.Share;
import org.terasology.fluid.component.FluidComponent;
import org.terasology.fluid.component.FluidContainerItemComponent;
import org.terasology.fluid.component.FluidInventoryComponent;
import org.terasology.fluid.event.BeforeFluidPutInInventory;
import org.terasology.fluid.event.BeforeFluidRemovedFromInventory;
import org.terasology.fluid.event.FluidVolumeChangedInInventory;

import java.util.List;

/**
 * Handles the adding, removing and moving of fluids. An implementation of the FluidManager interface.
 */
@RegisterSystem(RegisterMode.AUTHORITY)
@Share(FluidManager.class)
public class FluidManagerImpl extends BaseComponentSystem implements FluidManager {
    /**
     * Adds a fluid to all fluid inventory slots.
     *
     * @param instigator    The entity that's instigating this action
     * @param container     The entity that houses the fluid inventory
     * @param fluidType     The type of fluid being added
     * @param volume        The volume of fluid being added
     * @return              Whether the fluid was added successfully
     */
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

                // Refill the FluidComponent (fluid inventory) to max using just enough of the provided fluid. This will still
                // empty the item used to fill the inventory slot though.
                if (fluid.volume <= maximumVolume) {
                    float oldVolume = fluid.volume;

                    // Add the fluid into this fluid inventory slot. If it goes over the max, clamp the value to the maximum.
                    fluid.volume = Math.min(maximumVolume, fluid.volume + volume);

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

            // If the fluid in this fluid inventory slot doesn't already exist yet.
            if (fluid == null) {
                float maximumVolume = maximumVolumes.get(i);

                BeforeFluidPutInInventory beforePut = new BeforeFluidPutInInventory(instigator, fluidType, volume, i);
                container.send(beforePut);
                if (!beforePut.isConsumed()) {
                    EntityManager entityManager = CoreRegistry.get(EntityManager.class);

                    FluidComponent fluidComponent = new FluidComponent();
                    fluidComponent.fluidType = fluidType;

                    // Add the fluid into this fluid inventory slot. If it goes over the max, clamp the value to the maximum.
                    fluidComponent.volume = Math.min(maximumVolume, volume);

                    EntityRef newFluidEntity = entityManager.create(fluidComponent);
                    newFluidEntity.addComponent(new NetworkComponent());
                    fluidSlots.set(i, newFluidEntity);
                    container.saveComponent(fluidInventory);

                    container.send(new FluidVolumeChangedInInventory(instigator, fluidType, i, 0, volume));

                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Add a certain volume of fluid to a particular fluid inventory slot.
     *
     * @param instigator    The entity that's instigating this action
     * @param container     The entity that houses the fluid inventory
     * @param slot          The slot number of the fluid inventory that's intended to be filled
     * @param fluidType     The type of fluid being added
     * @param volume        The volume of fluid being added
     * @return              Whether the fluid was added successfully
     */
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

            // Refill the FluidComponent (fluid inventory) to max using just enough of the provided fluid. This will still
            // empty the item used to fill the inventory though.
            if (fluid.volume <= maximumVolume) {
                float oldVolume = fluid.volume;

                // Add the fluid into this fluid inventory slot. If it goes over the max, clamp the value to the maximum.
                fluid.volume = Math.min(maximumVolume, fluid.volume + volume);

                float newVolume = fluid.volume;
                fluidEntity.saveComponent(fluid);
                container.saveComponent(fluidInventory);

                container.send(new FluidVolumeChangedInInventory(instigator, fluidType, slot, oldVolume, newVolume));

                return true;
            }
        }

        // If the fluid in this fluid inventory slot doesn't already exist yet.
        if (fluid == null) {
            float maximumVolume = fluidInventory.maximumVolumes.get(slot);

            BeforeFluidPutInInventory beforePut = new BeforeFluidPutInInventory(instigator, fluidType, volume, slot);
            container.send(beforePut);
            if (!beforePut.isConsumed()) {
                EntityManager entityManager = CoreRegistry.get(EntityManager.class);

                FluidComponent fluidComponent = new FluidComponent();
                fluidComponent.fluidType = fluidType;

                // Add the fluid into this fluid inventory slot. If it goes over the max, clamp the value to the maximum.
                fluidComponent.volume = Math.min(maximumVolume, volume);

                EntityRef newFluidEntity = entityManager.create(fluidComponent);
                newFluidEntity.addComponent(new NetworkComponent());
                fluidInventory.fluidSlots.set(slot, newFluidEntity);
                container.saveComponent(fluidInventory);

                container.send(new FluidVolumeChangedInInventory(instigator, fluidType, slot, 0, volume));

                return true;
            }
        }

        return false;
    }

    /**
     * Add fluid to a particular fluid inventory slot from a fluid holder.
     *
     * @param instigator    The entity that's instigating this action
     * @param inventory     The entity that houses the fluid inventory
     * @param holder        The entity that houses the fluid holder that the fluid's being transferred from
     * @param slot          The slot number of the fluid inventory that's intended to be filled
     * @param fluidType     The type of fluid being added
     * @param volume        The volume of fluid being added
     * @return              Whether the fluid was added successfully
     */
    @Override
    public boolean addFluidFromHolder(EntityRef instigator, EntityRef inventory,
                                      EntityRef holder, int slot, String fluidType, float volume) {
        FluidInventoryComponent fluidInventory = inventory.getComponent(FluidInventoryComponent.class);
        FluidContainerItemComponent fluidHolder = holder.getComponent(FluidContainerItemComponent.class);

        if (fluidInventory == null) {
            return false;
        }

        EntityRef fluidEntity = fluidInventory.fluidSlots.get(slot);
        FluidComponent fluid = fluidEntity.getComponent(FluidComponent.class);
        if (fluid != null && fluid.fluidType.equals(fluidType)) {
            float maximumVolume = fluidInventory.maximumVolumes.get(slot);

            // Refill the FluidComponent (fluid inventory) to max using just enough of the provided fluid. The fluid holder
            // used will be emptied by the transferred amount accordingly.
            if (fluid.volume <= maximumVolume) {
                float oldVolume = fluid.volume;

                // Add the fluid into this fluid inventory slot. If it goes over the max, clamp the value to the maximum.
                fluid.volume = Math.min(maximumVolume, fluid.volume + volume);

                // Remove the fluid from the fluid holder. If it goes under 0, clamp the value to the minimum.
                fluidHolder.volume = Math.max(0f, fluidHolder.volume - (fluid.volume - oldVolume));
                float newVolume = fluid.volume;

                fluidEntity.saveComponent(fluid);
                inventory.saveComponent(fluidInventory);
                holder.saveComponent(fluidHolder);

                inventory.send(new FluidVolumeChangedInInventory(instigator, fluidType, slot, oldVolume, newVolume));

                return true;
            }
        }

        // If the fluid in this fluid inventory slot doesn't already exist yet. Create the FluidComponent and transfer
        // the fluid from the fluid holder to the fluid inventory slot.
        if (fluid == null) {
            float maximumVolume = fluidInventory.maximumVolumes.get(slot);

            BeforeFluidPutInInventory beforePut = new BeforeFluidPutInInventory(instigator, fluidType, volume, slot);
            inventory.send(beforePut);
            if (!beforePut.isConsumed()) {
                EntityManager entityManager = CoreRegistry.get(EntityManager.class);

                FluidComponent fluidComponent = new FluidComponent();
                fluidComponent.fluidType = fluidType;

                // Add the fluid into this fluid inventory slot. If it goes over the max, clamp the value to the maximum.
                fluidComponent.volume = Math.min(maximumVolume, volume);

                // Remove the fluid from the fluid holder. If it goes under 0, clamp the value to the minimum.
                fluidHolder.volume = Math.max(0f, fluidHolder.volume - maximumVolume);

                EntityRef newFluidEntity = entityManager.create(fluidComponent);
                newFluidEntity.addComponent(new NetworkComponent());
                fluidInventory.fluidSlots.set(slot, newFluidEntity);
                inventory.saveComponent(fluidInventory);
                holder.saveComponent(fluidHolder);

                inventory.send(new FluidVolumeChangedInInventory(instigator, fluidType, slot, 0, volume));

                return true;
            }
        }

        return false;
    }

    /**
     * Remove a certain volume of fluid from all fluid inventory slots.
     *
     * @param instigator    The entity that's instigating this action
     * @param container     The entity that houses the fluid inventory
     * @param fluidType     The type of fluid being removed
     * @param volume        The volume of fluid being removed
     * @return              Whether the fluid was removed successfully
     */
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

    /**
     * Remove a certain volume of fluid from a particular fluid inventory slot.
     *
     * @param instigator    The entity that's instigating this action
     * @param container     The entity that houses the fluid inventory
     * @param slot          The slot number of the fluid inventory that's intended to be used
     * @param fluidType     The type of fluid being removed
     * @param volume        The volume of fluid being removed
     * @return              Whether the fluid was removed successfully
     */
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

    /**
     * Remove a certain volume of fluid from a particular fluid container.
     *
     * @param instigator     The instigator of this action
     * @param container      The container from which the fluid is being removed
     * @param fluidType      The type of fluid being removed
     * @param slot           The inventory slot containing the container from which the fluid is being removed
     * @param volume         The volume of fluid being removed
     * @param fluidInventory The fluid inventory containing the fluid being removed
     * @param fluidEntity    An entity reference to the fluid being removed
     * @param fluid          The fluid component of the fluid being removed
     */
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
     * @return              The amount of fluid that was moved successfully
     */
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
            newFluidEntity.addComponent(new NetworkComponent());
            fluidInventoryTo.fluidSlots.set(slotTo, newFluidEntity);
            to.saveComponent(fluidInventoryTo);
        } else {
            fluidTo.volume += volumeToMove;
            fluidEntityTo.saveComponent(fluidTo);
        }

        return volumeToMove;
    }
}
